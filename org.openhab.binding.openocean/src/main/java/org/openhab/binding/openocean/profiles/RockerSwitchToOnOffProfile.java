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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.profiles.ProfileCallback;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.profiles.TriggerProfile;
import org.eclipse.smarthome.core.types.State;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class RockerSwitchToOnOffProfile implements TriggerProfile {

    private final ProfileCallback callback;

    RockerSwitchToOnOffProfile(ProfileCallback callback) {
        this.callback = callback;
    }

    @Override
    public ProfileTypeUID getProfileTypeUID() {
        return RockerSwitchEventsToOnOffProfileTypeUID;
    }

    @Override
    public void onStateUpdateFromItem(State state) {
        // nothing to do here => channel is readonly

    }

    @Override
    public void onTriggerFromHandler(String event) {
        if (event.equalsIgnoreCase(UP_PRESSED)) {
            callback.sendCommand(OnOffType.ON);
        } else if (event.equalsIgnoreCase(DOWN_PRESSED)) {
            callback.sendCommand(OnOffType.OFF);
        }
    }

}
