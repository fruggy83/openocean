/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.profiles;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.profiles.Profile;
import org.eclipse.smarthome.core.thing.profiles.ProfileAdvisor;
import org.eclipse.smarthome.core.thing.profiles.ProfileCallback;
import org.eclipse.smarthome.core.thing.profiles.ProfileContext;
import org.eclipse.smarthome.core.thing.profiles.ProfileFactory;
import org.eclipse.smarthome.core.thing.profiles.ProfileType;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeProvider;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelKind;
import org.eclipse.smarthome.core.thing.type.ChannelType;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
@Component(service = ProfileFactory.class)
public class OpenOceanProfileFactory implements ProfileFactory, ProfileAdvisor, ProfileTypeProvider {

    @Override
    public @Nullable Profile createProfile(ProfileTypeUID profileTypeUID, ProfileCallback callback,
            ProfileContext profileContext) {

        if (profileTypeUID.equals(RockerSwitchEventsToOnOffProfileTypeUID)) {
            return new RockerSwitchToOnOffProfile(callback);
        }

        return null;

    }

    @Override
    public Collection<@NonNull ProfileTypeUID> getSupportedProfileTypeUIDs() {
        return SUPPORTED_PROFILETYPES_UIDS;
    }

    @SuppressWarnings("null")
    @Override
    public @Nullable ProfileTypeUID getSuggestedProfileTypeUID(Channel channel, @Nullable String itemType) {

        if (channel.getKind() != ChannelKind.TRIGGER) {
            return null;
        }

        if (channel.getChannelTypeUID() != null && channel.getChannelTypeUID().equals(CHANNEL_TYPE_ROCKERSWITCH)) {
            return RockerSwitchEventsToOnOffProfileTypeUID;
        }

        return null;
    }

    @Override
    public Collection<@NonNull ProfileType> getProfileTypes(@Nullable Locale locale) {
        return ImmutableSet.of(OpenOceanProfileTypes.RockerSwitchToOnOffType);
    }

    @Override
    public @Nullable ProfileTypeUID getSuggestedProfileTypeUID(ChannelType channelType, @Nullable String itemType) {
        if (channelType.getUID().equals(CHANNEL_TYPE_ROCKERSWITCH)) {
            return RockerSwitchEventsToOnOffProfileTypeUID;
        }

        return null;
    }

}
