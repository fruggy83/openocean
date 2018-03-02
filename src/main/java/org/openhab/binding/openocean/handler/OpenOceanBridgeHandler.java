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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
import org.openhab.binding.openocean.internal.transceiver.TransceiverErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

/**
 * The {@link OpenOceanBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBridgeHandler extends ConfigStatusBridgeHandler implements TransceiverErrorListener {

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
                sendMessage(ESP3PacketFactory.CO_RD_REPEATER,
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
                sendMessage(ESP3PacketFactory.CO_WR_REPEATER((StringType) command),
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

        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING, "trying to connect to gateway...");
        Object devId = getConfig().get(NEXTDEVICEID);
        if (devId != null) {
            nextDeviceId = ((BigDecimal) devId).intValue();
        } else {
            nextDeviceId = 0;
        }

        if (connectorTask == null || connectorTask.isDone()) {
            connectorTask = scheduler.scheduleWithFixedDelay(new Runnable() {

                @Override
                public void run() {
                    if (thing.getStatus() != ThingStatus.ONLINE) {
                        initTransceiver();
                    }
                }

            }, 0, 60, TimeUnit.SECONDS);
        }
    }

    private synchronized void initTransceiver() {

        try {
            if (transceiver == null) {
                transceiver = new OpenOceanSerialTransceiver((String) getThing().getConfiguration().get(PORT), this);
            }

            if (transceiver != null) {
                transceiver.ShutDown();

                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING, "opening serial port...");
                transceiver.Initialize();

                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING, "starting rx thread...");
                transceiver.StartReceiving(scheduler);

                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING,
                        "trying to get bridge base id...");
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
                                    transceiver.setFilteredDeviceId(baseId);

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

        } catch (NoSuchPortException e) {
            logger.debug("error during bridge init occured", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Port could not be found");
        } catch (PortInUseException e) {
            logger.debug("error during bridge init occured", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Port already in use");
        } catch (Exception e) {
            logger.debug("error during bridge init occured", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Port could not be initialized");
            return;
        }

        /*
         * transceiver.sendESP3Packet(ESP3PacketFactory.CO_WR_SUBTEL(true),
         * new ResponseListenerIgnoringTimeouts<BaseResponse>() {
         *
         * @Override
         * public void responseReceived(BaseResponse response) {
         *
         * if (response.isOK()) {
         * logger.debug("set subtel");
         * } else if (response.getResponseType() == ResponseType.RET_NOT_SUPPORTED) {
         * logger.debug("set subtel not supported");
         * } else if (response.getResponseType() == ResponseType.RET_WRONG_PARAM) {
         * logger.debug("set subtel wrong param");
         * }
         * }
         * });
         */

    }

    @Override
    public synchronized void dispose() {
        if (transceiver != null) {
            transceiver.ShutDown();
            transceiver = null;
        }

        if (connectorTask != null && !connectorTask.isDone()) {
            connectorTask.cancel(true);
            connectorTask = null;
        }

        super.dispose();
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        Collection<ConfigStatusMessage> configStatusMessages = new LinkedList<ConfigStatusMessage>();

        // The serial port must be provided
        final String bridgePort = (String) getThing().getConfiguration().get(PORT);
        if (bridgePort == null || bridgePort.isEmpty()) {
            configStatusMessages.add(ConfigStatusMessage.Builder.error(PORT)
                    .withMessageKeySuffix(OpenOceanConfigStatusMessage.PORT_MISSING.getMessageKey()).withArguments(PORT)
                    .build());
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

    public <T extends Response> void sendMessage(ESP3Packet message, ResponseListener<T> responseListener) {
        try {
            transceiver.sendESP3Packet(message, responseListener);
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
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

    @Override
    public void ErrorOccured(Throwable exception) {
        transceiver.ShutDown();
        transceiver = null;
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, exception.getMessage());

    }
}
