/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.ChannelId2ChannelDescription;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelKind;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.ChannelDescription;
import org.openhab.binding.openocean.internal.eep.EEPType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class OpenOceanBaseThingHandler extends ConfigStatusThingHandler {

    private OpenOceanBridgeHandler gateway = null;
    protected Logger logger = LoggerFactory.getLogger(OpenOceanBaseThingHandler.class);

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.union(
            OpenOceanBaseSensorHandler.SUPPORTED_THING_TYPES, OpenOceanBaseActuatorHandler.SUPPORTED_THING_TYPES);

    protected String enoceanId;

    protected String configurationErrorDescription;

    private Hashtable<String, Channel> linkedChannels = null;

    protected Hashtable<String, State> channelState = null;

    public OpenOceanBaseThingHandler(Thing thing) {
        super(thing);
    }

    @SuppressWarnings("null")
    @Override
    public void initialize() {
        logger.debug("Initializing open ocean base thing handler.");
        initializeThing((getBridge() == null) ? null : getBridge().getStatus());
    }

    private void initializeThing(ThingStatus bridgeStatus) {
        logger.debug("initializeThing thing {} bridge status {}", getThing().getUID(), bridgeStatus);

        if (getBridgeHandler() != null) {
            if (bridgeStatus == ThingStatus.ONLINE) {
                if (validateConfig()) {
                    updateStatus(ThingStatus.ONLINE);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                            configurationErrorDescription);
                }
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, "A bridge is required");
        }
    }

    protected boolean validateEnoceanId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        } else {

            if (id.length() != 8) {
                return false;
            }

            try {
                Integer.parseUnsignedInt(id, 16);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    abstract boolean validateConfig();

    protected void updateChannels(EEPType eep, boolean removeUnsupportedChannels) {

        List<Channel> channelList = new LinkedList<Channel>(this.getThing().getChannels());
        boolean channelListChanged = false;

        if (removeUnsupportedChannels) {
            channelListChanged = channelList.removeIf(channel -> !eep.GetChannelIds().stream()
                    .anyMatch(supportedId -> supportedId.equals(channel.getUID().getId())));
        }

        for (String id : eep.GetChannelIds()) {
            if (channelList.stream().anyMatch(channel -> id.equals(channel.getUID().getId()))) {
                continue;
            }

            ChannelDescription t = ChannelId2ChannelDescription.get(id);
            Channel channel = ChannelBuilder.create(new ChannelUID(this.getThing().getUID(), id), t.ItemType)
                    .withConfiguration(eep.getChannelConfig(id)).withType(t.ChannelTypeUID)
                    .withKind(t.ItemType == null ? ChannelKind.TRIGGER : ChannelKind.STATE).withLabel(t.Label).build();

            channelList.add(channel);
            channelListChanged = true;
        }

        if (channelListChanged) {
            ThingBuilder thingBuilder = editThing();
            thingBuilder.withChannels(channelList);
            updateThing(thingBuilder.build());
        }
    }

    protected Hashtable<String, Channel> getLinkedChannels() {
        if (linkedChannels != null) {
            return linkedChannels;
        }

        linkedChannels = new Hashtable<>();
        channelState = new Hashtable<>();

        for (Channel c : this.getThing().getChannels()) {
            if (isLinked(c.getUID().getId())) {
                {
                    linkedChannels.put(c.getUID().getId(), c);
                    channelState.put(c.getUID().getId(), UnDefType.UNDEF);
                }
            } else if (c.getKind() == ChannelKind.TRIGGER) {
                linkedChannels.put(c.getUID().getId(), c);
            }
        }

        return linkedChannels;
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        super.channelLinked(channelUID);

        if (linkedChannels == null) {
            return;
        }

        linkedChannels.putIfAbsent(channelUID.getId(), thing.getChannel(channelUID.getId()));
        channelState.putIfAbsent(channelUID.getId(), UnDefType.UNDEF);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        super.channelUnlinked(channelUID);

        if (linkedChannels == null) {
            return;
        }

        linkedChannels.remove(channelUID.getId());
        channelState.remove(channelUID.getId());
    }

    protected synchronized OpenOceanBridgeHandler getBridgeHandler() {
        if (this.gateway == null) {
            Bridge bridge = getBridge();
            if (bridge == null) {
                return null;
            }
            ThingHandler handler = bridge.getHandler();
            if (handler instanceof OpenOceanBridgeHandler) {
                this.gateway = (OpenOceanBridgeHandler) handler;
            } else {
                return null;
            }
        }
        return this.gateway;
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        // TODO
        // Collection<ConfigStatusMessage> configStatusMessages;

        // The senderId must be provided
        /*
         * final OpenOceanActuatorConfig config = getConfigAs(OpenOceanActuatorConfig.class);
         * final String senderId = config.senderIdOffset;
         * if (senderId == null || senderId.isEmpty()) {
         * configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(SENDERID)
         * .withMessageKeySuffix(OpenOceanConfigStatusMessage.SENDERID_MISSING.getMessageKey())
         * .withArguments(SENDERID).build());
         * } else {
         * try {
         * Integer.parseUnsignedInt(senderId, 16);
         * } catch (Exception e) {
         * configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(SENDERID)
         * .withMessageKeySuffix(OpenOceanConfigStatusMessage.SENDERID_MALFORMED.getMessageKey())
         * .withArguments(SENDERID).build());
         * }
         * configStatusMessages = Collections.emptyList();
         * }
         *
         * return configStatusMessages;
         */

        return Collections.emptyList();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            initializeThing(bridgeStatusInfo.getStatus());
        }
    }
}
