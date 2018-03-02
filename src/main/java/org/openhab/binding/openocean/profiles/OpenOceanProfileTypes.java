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
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.profiles.TriggerProfileType;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanProfileTypes {

    @NonNull
    public static final TriggerProfileType RockerSwitchToOnOffType = new TriggerProfileType() {

        @Override
        @NonNull
        public Collection<@NonNull String> getSupportedItemTypes() {
            return Collections.singleton(SWITCH);
        }

        @Override
        public String getLabel() {
            return "Rockerswitch to OnOff";
        }

        @Override
        public @NonNull ProfileTypeUID getUID() {
            return RockerSwitchEventsToOnOffProfileTypeUID;
        }

        @Override
        @NonNull
        public Collection<@NonNull ChannelTypeUID> getSupportedChannelTypeUIDs() {
            return ImmutableSet.of(CHANNEL_TYPE_ROCKERSWITCH);
        }

    };
}
