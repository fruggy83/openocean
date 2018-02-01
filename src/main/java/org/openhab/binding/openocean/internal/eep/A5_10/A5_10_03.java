/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.A5_10;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.eep._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_10_03 extends _4BSMessage {

    public A5_10_03(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {
        if (!isValid()) {
            return UnDefType.UNDEF;
        }

        switch (channelId) {
            case CHANNEL_TEMPERATURE:
                double temp = (getDB_1() - 255) / -6.375;
                return new DecimalType(temp);

            case CHANNEL_SETPOINT:
                return new DecimalType(getDB_2());
        }

        return UnDefType.UNDEF;
    }

}
