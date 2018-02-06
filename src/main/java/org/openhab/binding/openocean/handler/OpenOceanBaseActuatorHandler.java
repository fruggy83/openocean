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

import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.openocean.internal.config.OpenOceanActuatorConfig;
import org.openhab.binding.openocean.internal.eep.EEP;
import org.openhab.binding.openocean.internal.eep.EEPFactory;
import org.openhab.binding.openocean.internal.eep.EEPType;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.transceiver.Helper;

import com.google.common.collect.Sets;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBaseActuatorHandler extends OpenOceanBaseSensorHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_UNIVERSALACTUATOR,
            THING_TYPE_CENTRALCOMMAND, THING_TYPE_ROCKERSWITCH, THING_TYPE_MEASUREMENTSWITCH);

    protected int[] senderId;
    protected int[] destinationId;

    protected EEPType sendingEEPType = null;

    public OpenOceanBaseActuatorHandler(Thing thing) {
        super(thing);
    }

    private boolean validateSenderIdOffset(int senderIdOffset) {
        if (senderIdOffset == -1) {
            return true;
        }

        if (senderIdOffset >= 0 && senderIdOffset < 128) {

            OpenOceanBridgeHandler bridgeHandler = getBridgeHandler();
            if (bridgeHandler != null) {
                return !bridgeHandler.existsSender(senderIdOffset, this.thing);
            }
        }

        return false;
    }

    @Override
    boolean validateConfig() {
        if (super.validateConfig()) {
            OpenOceanActuatorConfig config = thing.getConfiguration().as(OpenOceanActuatorConfig.class);

            try {
                sendingEEPType = EEPType.getType(config.getSendingEEPId());
                updateChannels(sendingEEPType, true);

                destinationId = Helper.hexStringToBytes(thing.getUID().getId());

            } catch (Exception e) {
                configurationErrorDescription = "EEP is not supported";
                return false;
            }

            if (validateSenderIdOffset(config.senderIdOffset)) {
                return initializeIdForSending();
            } else {
                configurationErrorDescription = "Sender Id is not valid for bridge";
            }
        }

        return false;
    }

    private boolean initializeIdForSending() {
        OpenOceanActuatorConfig cfg = getConfigAs(OpenOceanActuatorConfig.class);
        int senderId = cfg.senderIdOffset;

        String thingId = this.getThing().getThingTypeUID().getId();
        String rsid = THING_TYPE_ROCKERSWITCH.getId();

        if (senderId == -1 && thingId.equals(rsid)) {
            return true;
        }

        OpenOceanBridgeHandler bridgeHandler = getBridgeHandler();
        if (bridgeHandler == null) {
            return false;
        }

        if (senderId == -1) {
            Configuration config = editConfiguration();
            senderId = bridgeHandler.getNextDeviceId(thing);
            if (senderId == -1) {
                configurationErrorDescription = "Could not get a free sender Id from Bridge";
                return false;
            }
            config.put(PARAMETER_SENDERIDOFFSET, senderId);
            updateConfiguration(config);
        }

        int[] baseId = bridgeHandler.getBaseId();
        baseId[3] += senderId;
        this.senderId = baseId;

        this.updateProperty(PROPERTY_Enocean_ID, Helper.bytesToHexString(this.senderId));
        bridgeHandler.addSender(senderId, thing);

        return true;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        // we do not support refreshs
        if (command == RefreshType.REFRESH) {
            return;
        }

        // We must have a valid sender id to send commands
        if (senderId == null) {
            return;
        }

        String channelId = channelUID.getId();
        Channel channel = getLinkedChannels().get(channelId);
        if (channel == null) {
            return;
        }

        EEPType type = sendingEEPType;
        EEP eep = EEPFactory.createEEP(type);

        if (eep.getSupportedChannels().contains(channelId)) {

            Configuration config = channel.getConfiguration();
            State currentState = channelState.get(channelId);

            ESP3Packet msg = // eep.setSenderId(senderId)
                    eep.setSenderId(new int[] { 0x00, 0xff, 0xff, 0xff }).setDestinationId(destinationId)
                            .convertFromCommand(channelId, command, currentState, config).getERP1Message();

            getBridgeHandler().sendMessage(msg, null);

            while (eep.PrepareNextMessage()) {
                getBridgeHandler().sendMessage(eep.getERP1Message(), null);
            }

        }
    }

    @Override
    public void handleRemoval() {
        OpenOceanActuatorConfig config = thing.getConfiguration().as(OpenOceanActuatorConfig.class);
        if (config.senderIdOffset >= 0) {
            OpenOceanBridgeHandler bridgeHandler = getBridgeHandler();
            if (bridgeHandler != null) {
                bridgeHandler.removeSender(config.senderIdOffset);
            }
        }

        super.handleRemoval();
    }
}
