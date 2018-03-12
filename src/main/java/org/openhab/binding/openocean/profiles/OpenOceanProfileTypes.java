/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.profiles;

import org.eclipse.smarthome.core.library.CoreItemFactory;
import org.eclipse.smarthome.core.thing.DefaultSystemChannelTypeProvider;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeBuilder;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.profiles.TriggerProfileType;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanProfileTypes {

    public static final ProfileTypeUID RockerSwitchToPlayPause = new ProfileTypeUID(ProfileTypeUID.SYSTEM_SCOPE,
            "rockerswitch-to-play-pause");

    public static final TriggerProfileType RockerSwitchToPlayPauseType = ProfileTypeBuilder
            .newTrigger(RockerSwitchToPlayPause, "Rocker switch to Play/Pause")
            .withSupportedItemTypes(CoreItemFactory.PLAYER)
            .withSupportedChannelTypeUIDs(DefaultSystemChannelTypeProvider.SYSTEM_RAWROCKER.getUID()).build();
}
