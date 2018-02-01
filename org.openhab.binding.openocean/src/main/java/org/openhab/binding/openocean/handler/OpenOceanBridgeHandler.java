/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.openocean.internal.OpenOceanConfigStatusMessage;
import org.openhab.binding.openocean.internal.OpenOceanException;
import org.openhab.binding.openocean.internal.messages.BaseResponse;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.messages.ESP3PacketFactory;
import org.openhab.binding.openocean.internal.messages.RDBaseIdResponse;
import org.openhab.binding.openocean.internal.messages.RDRepeaterResponse;
import org.openhab.binding.openocean.internal.messages.RDVersionResponse;
import org.openhab.binding.openocean.internal.messages.Response;
import org.openhab.binding.openocean.internal.transceiver.ESP3PacketListener;
import org.openhab.binding.openocean.internal.transceiver.Helper;
import org.openhab.binding.openocean.internal.transceiver.OpenOceanSerialTransceiver;
import org.openhab.binding.openocean.internal.transceiver.OpenOceanTransceiver;
import org.openhab.binding.openocean.internal.transceiver.ResponseListener;
import org.openhab.binding.openocean.internal.transceiver.ResponseListenerIgnoringTimeouts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OpenOceanBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBridgeHandler extends ConfigStatusBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(OpenOceanBridgeHandler.class);

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_BRIDGE);

    private OpenOceanTransceiver transceiver;
    private ScheduledFuture<?> connectorTask;

    private int[] baseId = null;
    private Thing[] sendingThings = new Thing[128];

    private int nextDeviceId = 0;

    public OpenOceanBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (transceiver == null) {
            updateStatus(ThingStatus.OFFLINE);
            return;
        }

        if (channelUID.getId().equals(REPEATERMODE)) {
            if (command instanceof RefreshType) {
                transceiver.sendESP3Packet(ESP3PacketFactory.CO_RD_REPEATER,
                        new ResponseListenerIgnoringTimeouts<RDRepeaterResponse>() {

                            @Override
                            public void responseReceived(RDRepeaterResponse response) {
                                if (response.isValid() && response.isOK()) {
                                    updateState(channelUID, response.getRepeaterLevel());
                                } else {
                                    updateState(channelUID, StringType.valueOf(REPEATERMODE_OFF));
                                }
                            }
                        });
            } else if (command instanceof StringType) {
                transceiver.sendESP3Packet(ESP3PacketFactory.CO_WR_REPEATER((StringType) command),
                        new ResponseListenerIgnoringTimeouts<BaseResponse>() {

                            @Override
                            public void responseReceived(BaseResponse response) {
                                if (response.isOK()) {
                                    updateState(channelUID, (StringType) command);
                                }
                            }

                        });
            }
        }
    }

    @Override
    public void initialize() {

        // String result = Transformation.transform("MAP", "test.map", "CLOSE");
        // logger.debug(result);

        // TODO use scheduled task to try reconnect to gateway
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING, "trying to connect to gateway...");
        Object devId = getConfig().get(NEXTDEVICEID);
        if (devId != null) {
            nextDeviceId = ((BigDecimal) devId).intValue();
        } else {
            nextDeviceId = 0;
        }

        if (connectorTask != null) {
            connectorTask.cancel(true);
            dispose();
            connectorTask = null;
        }

        if (connectorTask == null || connectorTask.isCancelled()) {
            connectorTask = scheduler.schedule(new Runnable() {

                @Override
                public void run() {
                    initTransceiver();
                }

            }, 1, TimeUnit.SECONDS);
        }
    }

    private void initTransceiver() {

        transceiver = new OpenOceanSerialTransceiver((String) getThing().getConfiguration().get(PORT));

        try {
            transceiver.Initialize();
        } catch (OpenOceanException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            return;
        }

        transceiver.StartSendingAndReading(scheduler);

        logger.debug("request base id");
        transceiver.sendESP3Packet(ESP3PacketFactory.CO_RD_IDBASE,
                new ResponseListenerIgnoringTimeouts<RDBaseIdResponse>() {

                    @Override
                    public void responseReceived(RDBaseIdResponse response) {

                        logger.debug("received response for base id");

                        if (response.isValid() && response.isOK()) {
                            baseId = response.getBaseId().clone();
                            updateProperty(PROPERTY_Base_ID, Helper.bytesToHexString(response.getBaseId()));
                            updateProperty(PROPERTY_REMAINING_WRITE_CYCLES_Base_ID,
                                    Integer.toString(response.getRemainingWriteCycles()));

                            updateStatus(ThingStatus.ONLINE);
                        } else {
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                                    "Could not get BaseId");
                        }
                    }
                });

        transceiver.sendESP3Packet(ESP3PacketFactory.CO_RD_VERSION,
                new ResponseListenerIgnoringTimeouts<RDVersionResponse>() {

                    @Override
                    public void responseReceived(RDVersionResponse response) {

                        if (response.isValid() && response.isOK()) {
                            updateProperty(PROPERTY_APP_VERSION, response.getAPPVersion());
                            updateProperty(PROPERTY_API_VERSION, response.getAPIVersion());
                            updateProperty(PROPERTY_CHIP_ID, response.getChipID());
                            updateProperty(PROPERTY_DESCRIPTION, response.getDescription());
                        }
                    }
                });
    }

    @Override
    public void dispose() {
        if (transceiver != null) {
            transceiver.ShutDown();
        }
        super.dispose();
    }

    @Override
    public void handleRemoval() {
        if (transceiver != null) {
            transceiver.ShutDown();
        }
        updateStatus(ThingStatus.REMOVED);
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        Collection<ConfigStatusMessage> configStatusMessages;

        // The serial port must be provided
        final String bridgePort = (String) getThing().getConfiguration().get(PORT);
        if (bridgePort == null || bridgePort.isEmpty()) {
            configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(PORT)
                    .withMessageKeySuffix(OpenOceanConfigStatusMessage.PORT_MISSING.getMessageKey()).withArguments(PORT)
                    .build());
        } else {
            configStatusMessages = Collections.emptyList();
        }

        return configStatusMessages;
    }

    public int[] getBaseId() {
        return baseId.clone();
    }

    public int getNextDeviceId(Thing sender) {
        return getNextDeviceId(sender.getUID().getId());
    }

    public int getNextDeviceId(String senderId) {
        if (nextDeviceId != 0 && sendingThings[nextDeviceId] == null) {
            int result = nextDeviceId;
            Configuration config = getConfig();
            config.put(NEXTDEVICEID, null);
            updateConfiguration(config);
            nextDeviceId = 0;

            return result;
        }

        for (byte i = 1; i < sendingThings.length; i++) {
            if (sendingThings[i] == null || sendingThings[i].getUID().getId().equals(senderId)) {
                return i;
            }
        }

        return -1;
    }

    public boolean existsSender(int id, Thing sender) {
        return sendingThings[id] != null && !sendingThings[id].getUID().getId().equals(sender.getUID().getId());
    }

    public void addSender(int id, Thing thing) {
        sendingThings[id] = thing;
    }

    public void removeSender(int id) {
        sendingThings[id] = null;
    }

    public <T extends Response> void sendMessage(ESP3Packet message, ResponseListener<T> response) {
        transceiver.sendESP3Packet(message, response);
    }

    public void addPacketListener(ESP3PacketListener listener) {
        transceiver.addPacketListener(listener);
    }

    public void startDiscovery(ESP3PacketListener teachInListener) {
        transceiver.startDiscovery(teachInListener);
    }

    public void stopDiscovery() {
        transceiver.stopDiscovery();
    }
}
