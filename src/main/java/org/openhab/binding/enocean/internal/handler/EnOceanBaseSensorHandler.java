/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.enocean.internal.handler;

import static java.util.Collections.unmodifiableCollection;
import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;

import org.openhab.binding.enocean.internal.config.EnOceanBaseConfig;
import org.openhab.binding.enocean.internal.eep.EEP;
import org.openhab.binding.enocean.internal.eep.EEPFactory;
import org.openhab.binding.enocean.internal.eep.EEPType;
import org.openhab.binding.enocean.internal.messages.BasePacket;
import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.binding.enocean.internal.messages.ERP1Message.RORG;
import org.openhab.binding.enocean.internal.transceiver.PacketListener;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.link.ItemChannelLinkRegistry;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.openhab.core.util.HexUtils;

/**
 *
 * @author Daniel Weber - Initial contribution
 *         This class defines base functionality for receiving eep messages.
 */
public class EnOceanBaseSensorHandler extends EnOceanBaseThingHandler implements PacketListener {

    // List of all thing types which support receiving of eep messages
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = new HashSet<>(
            Arrays.asList(THING_TYPE_ROOMOPERATINGPANEL, THING_TYPE_MECHANICALHANDLE, THING_TYPE_CONTACT,
                    THING_TYPE_TEMPERATURESENSOR, THING_TYPE_TEMPERATUREHUMIDITYSENSOR, THING_TYPE_ROCKERSWITCH,
                    THING_TYPE_OCCUPANCYSENSOR, THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR, THING_TYPE_LIGHTSENSOR,
                    THING_TYPE_PUSHBUTTON, THING_TYPE_AUTOMATEDMETERSENSOR, THING_TYPE_ENVIRONMENTALSENSOR,
                    THING_TYPE_MULTFUNCTIONSMOKEDETECTOR));

    protected Hashtable<RORG, EEPType> receivingEEPTypes = null;

    protected ScheduledFuture<?> retryFuture = null;

    public EnOceanBaseSensorHandler(Thing thing, ItemChannelLinkRegistry itemChannelLinkRegistry) {
        super(thing, itemChannelLinkRegistry);
    }

    @Override
    void initializeConfig() {
        config = getConfigAs(EnOceanBaseConfig.class);
    }

    @Override
    Collection<EEPType> getEEPTypes() {
        if (receivingEEPTypes == null) {
            return Collections.emptyList();
        }

        return unmodifiableCollection(receivingEEPTypes.values());
    }

    @Override
    boolean validateConfig() {
        receivingEEPTypes = null;

        try {
            if (config.receivingEEPId != null && !config.receivingEEPId.isEmpty()) {
                receivingEEPTypes = new Hashtable<>();

                for (String receivingEEP : config.receivingEEPId) {
                    if (receivingEEP == null) {
                        continue;
                    }

                    EEPType receivingEEPType = EEPType.getType(receivingEEP);
                    if (receivingEEPTypes.containsKey(receivingEEPType.getRORG())) {
                        configurationErrorDescription = "Receiving more than one EEP of the same RORG is not supported";
                        return false;
                    }

                    receivingEEPTypes.put(receivingEEPType.getRORG(), receivingEEPType);
                }
            } else {
                receivingEEPTypes = null;
            }
        } catch (Exception e) {
            configurationErrorDescription = "Receiving EEP is not supported";
            return false;
        }

        updateChannels();

        if (receivingEEPTypes != null) {
            if (!validateEnoceanId(config.enoceanId)) {
                configurationErrorDescription = "EnOceanId is not a valid EnOceanId";
                return false;
            }

            if (!config.enoceanId.equals(EMPTYENOCEANID)) {
                getBridgeHandler().addPacketListener(this);
            }
        }

        return true;
    }

    @Override
    public long getSenderIdToListenTo() {
        return Long.parseLong(config.enoceanId, 16);
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
        // sensor things cannot send any messages, hence they are not allowed to handle any command
        // The only possible command would be "Refresh"
    }

    protected Predicate<Channel> channelFilter(EEPType eepType, byte[] senderId) {
        return c -> {
            boolean result = eepType.GetSupportedChannels().containsKey(c.getUID().getId());
            return (isLinked(c.getUID()) || c.getKind() == ChannelKind.TRIGGER) && result;
        };
    }

    @Override
    public void packetReceived(BasePacket packet) {

        if (receivingEEPTypes == null) {
            return;
        }

        try {

            ERP1Message msg = (ERP1Message) packet;
            EEPType receivingEEPType = receivingEEPTypes.get(msg.getRORG());
            if (receivingEEPType == null) {
                return;
            }

            EEP eep = EEPFactory.buildEEP(receivingEEPType, (ERP1Message) packet);
            logger.debug("ESP Packet payload {} for {} received", HexUtils.bytesToHex(packet.getPayload()),
                    HexUtils.bytesToHex(msg.getSenderId()));

            if (eep.isValid()) {
                byte[] senderId = msg.getSenderId();

                if (retryFuture != null && !retryFuture.isDone()) {
                    retryFuture.cancel(false);
                }

                // try to interpret received message for all linked or trigger channels
                getThing().getChannels().stream().filter(channelFilter(receivingEEPType, senderId))
                        .sorted((c1, c2) -> c1.getKind().compareTo(c2.getKind())) // handle state channels first
                        .forEachOrdered(channel -> {
                            ChannelTypeUID channelTypeUID = channel.getChannelTypeUID();
                            String channelTypeId = (channelTypeUID != null) ? channelTypeUID.getId() : "";

                            String channelId = channel.getUID().getId();
                            Configuration channelConfig = channel.getConfiguration();

                            switch (channel.getKind()) {
                                case STATE:
                                    State result = eep.convertToState(channelId, channelTypeId, channelConfig,
                                            id -> getCurrentState(id));

                                    // if message can be interpreted (result != UnDefType.UNDEF) => update item state
                                    if (result != null && result != UnDefType.UNDEF) {
                                        updateState(channelId, result);
                                    }
                                    break;
                                case TRIGGER:
                                    String lastEvent = lastEvents.get(channelId);
                                    String event = eep.convertToEvent(channelId, channelTypeId, lastEvent,
                                            channelConfig);
                                    if (event != null) {
                                        triggerChannel(channel.getUID(), event);
                                        lastEvents.put(channelId, event);
                                    }
                                    break;
                            }
                        });
            }
        } catch (Exception e) {
            logger.warn("Exception while receiving telegram!", e);
        }
    }
}
