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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class A5_10 extends _4BSMessage {

    public A5_10(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {
        if (!isValid() || !this.getSupportedChannels().contains(channelId)) {
            return UnDefType.UNDEF;
        }

        switch (channelId) {
            case CHANNEL_FANSPEEDSTAGE:
                if (getDB_3() > 209) {
                    return new StringType("-1");
                } else if (getDB_3() > 189) {
                    return new StringType("0");
                } else if (getDB_3() > 164) {
                    return new StringType("1");
                } else if (getDB_3() > 144) {
                    return new StringType("2");
                } else {
                    return new StringType("3");
                }

            case CHANNEL_SETPOINT:
                return new DecimalType(getDB_2());

            case CHANNEL_TEMPERATURE:
                double temp = (getDB_1() - 255) / -6.375;
                return new DecimalType(temp);

            case CHANNEL_OCCUPANCY:
                return getBit(getDB_0(), 0) ? OnOffType.OFF : OnOffType.ON;
        }

        return UnDefType.UNDEF;
    }
}
