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
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.core.util.HexUtils;
import org.openhab.binding.openocean.internal.config.OpenOceanBaseConfig;
import org.openhab.binding.openocean.internal.eep.EEP;
import org.openhab.binding.openocean.internal.eep.EEPFactory;
import org.openhab.binding.openocean.internal.eep.EEPType;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.transceiver.ESP3PacketListener;

import com.google.common.collect.Sets;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBaseSensorHandler extends OpenOceanBaseThingHandler implements ESP3PacketListener {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_ROOMOPERATINGPANEL,
            THING_TYPE_MECHANICALHANDLE, THING_TYPE_CONTACTANDSWITCH, THING_TYPE_TEMPERATURESENSOR,
            THING_TYPE_HUMIDITYTEMPERATURESENSOR, THING_TYPE_ROCKERSWITCH, THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR,
            THING_TYPE_PUSHBUTTON);

    protected EEPType receivingEEPType = null;

    protected String enoceanId;

    public OpenOceanBaseSensorHandler(Thing thing) {
        super(thing);
    }

    @Override
    boolean validateConfig() {
        OpenOceanBaseConfig cfg = getConfigAs(OpenOceanBaseConfig.class);
        try {
            if (cfg.getReceivingEEPId() != null && !cfg.getReceivingEEPId().isEmpty()) {

                receivingEEPType = EEPType.getType(cfg.getReceivingEEPId());
                updateChannels(receivingEEPType, true);
            } else {
                receivingEEPType = null;
            }
        } catch (Exception e) {
            configurationErrorDescription = "Receiving EEP is not supported";
            return false;
        }

        enoceanId = thing.getUID().getId();
        if (validateEnoceanId(enoceanId)) {
            if (receivingEEPType != null) {
                getBridgeHandler().addPacketListener(this);
            }
            return true;
        }

        configurationErrorDescription = "Thing Id is not a valid Enocean Id";
        return false;
    }

    @Override
    public long getSenderIdToListenTo() {
        return Long.parseLong(enoceanId, 16);
    }

    @Override
    public void handleRemoval() {

        if (getBridgeHandler() != null) {
            getBridgeHandler().removePacketListener(this);
        }
        super.handleRemoval();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    protected EEPType receivingEEPTyp() {
        return receivingEEPType;
    }

    @Override
    public void espPacketReceived(ESP3Packet packet) {

        if (receivingEEPType == null) {
            return;
        }

        EEP eep = EEPFactory.buildEEP(receivingEEPTyp(), (ERP1Message) packet);

        logger.debug("ESP Packet {} for {} received", HexUtils.bytesToHex(packet.getPayload()),
                this.getThing().getUID().getId());

        if (eep.isValid()) {

            receivingEEPType.GetChannelIds().stream().filter(id -> getLinkedChannels().containsKey(id)).forEach(id -> {
                if (id == null) {
                    return;
                }

                Channel channel = getLinkedChannels().get(id);

                Configuration config = channel.getConfiguration();
                switch (channel.getKind()) {
                    case STATE:
                        State currentState = channelState.get(id);
                        State result = eep.convertToState(id, config, currentState);

                        if (result != null && result != UnDefType.UNDEF) {
                            updateState(id, result);
                            channelState.put(id, result);
                        }
                        break;
                    case TRIGGER:
                        String lastEvent = lastEvents.get(id);
                        String event = eep.convertToEvent(id, lastEvent, config);
                        if (event != null) {
                            triggerChannel(channel.getUID(), event);
                            lastEvents.put(id, event);
                        }
                        break;
                }
            });
        }
    }
}
