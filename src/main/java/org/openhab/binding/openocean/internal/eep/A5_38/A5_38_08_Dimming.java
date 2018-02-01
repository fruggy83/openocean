/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.A5_38;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.CHANNEL_DIMMER;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.eep._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_38_08_Dimming extends _4BSMessage {

    static final int CommandId = 0x02;
    static final int SwitchOff = 0x00;
    static final int SwitchOn = 0x01;

    public A5_38_08_Dimming() {
        super();
    }

    public A5_38_08_Dimming(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(Command outputCommand, String channelId, State currentState, Configuration config) {

        int teachInBit = TeachInBit; // (getIsTeachIn() ? Zero : TeachInBit);

        if (outputCommand instanceof DecimalType) {
            setData(CommandId, ((DecimalType) outputCommand).byteValue(), Zero, (teachInBit | SwitchOn));
        } else if ((OnOffType) outputCommand == OnOffType.ON) {
            setData(CommandId, 0x64, Zero, (teachInBit | SwitchOn));
        } else {
            setData(CommandId, Zero, Zero, (teachInBit | SwitchOff));
        }
    }

    @Override
    public State convertToStateImpl(String channelId, State currentState, Configuration config) {
        if (!isValid()) {
            return UnDefType.UNDEF;
        }

        if (channelId == CHANNEL_DIMMER) {
            if (getDB_0() == (TeachInBit | SwitchOff)) {
                return new PercentType(0);
            } else {
                return new PercentType(getDB_2());
            }
        }

        return UnDefType.UNDEF;
    }
}
