/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.F6_10;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.eep.Base._RPSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class F6_10_00 extends _RPSMessage {

    final int AI = 0;
    final int A0 = 1;
    final int BI = 2;
    final int B0 = 3;
    final int PRESSED = 16;

    public F6_10_00() {
        super();
    }

    public F6_10_00(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {

        if (!isValid() || !getBit(bytes[0], 7) || !getBit(bytes[0], 6)) {
            return UnDefType.UNDEF;
        }

        // todo localization
        switch (channelId) {
            case CHANNEL_WINDOWHANDLESTATE:
                if (getBit(bytes[0], 4)) {
                    return getBit(bytes[0], 5) ? new StringType("CLOSED") : new StringType("TILTED");
                } else {
                    return new StringType("OPEN");
                }

            case CHANNEL_CONTACT:
                if (getBit(bytes[0], 4)) {
                    return getBit(bytes[0], 5) ? OpenClosedType.CLOSED : OpenClosedType.OPEN;
                } else {
                    return OpenClosedType.OPEN;
                }
        }

        return UnDefType.UNDEF;
    }
}
