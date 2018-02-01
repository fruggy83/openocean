/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.D2_01;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.CHANNEL_GENERAL_SWITCHING;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class D2_01_09 extends D2_01 {

    public D2_01_09() {
        super();
    }

    public D2_01_09(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {
        if (channelId.equals(CHANNEL_GENERAL_SWITCHING)) {
            setSwitchingData((OnOffType) command, 0x1e);
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {

        switch (channelId) {
            case CHANNEL_GENERAL_SWITCHING:
                if (getCMD() == CMD_ACTUATOR_STATUS_RESPONE) {
                    return (bytes[bytes.length - 1] & outputValueMask) > 0 ? OnOffType.ON : OnOffType.OFF;
                }

                break;
        }

        return UnDefType.UNDEF;
    }
}
