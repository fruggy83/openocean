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

    public final int Closed = 0xF0; // 1111xxxx
    public final int Open1 = 0xE0; // 1110xxxx
    public final int Open2 = 0xC0; // 1100xxxx
    public final int Tilted = 0xD0; // 1101xxxx

    public F6_10_00() {
        super();
    }

    public F6_10_00(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {

        if (!isValid()) {
            return UnDefType.UNDEF;
        }

        int data = bytes[0] & 0xF0;

        // todo localization
        switch (channelId) {
            case CHANNEL_WINDOWHANDLESTATE:
                if (data == Closed) {
                    return new StringType("CLOSED");
                } else if (data == Tilted) {
                    return new StringType("TILTED");
                } else if (data == Open1 || data == Open2) {
                    return new StringType("OPEN");
                }

            case CHANNEL_CONTACT:
                if (data == Closed) {
                    return OpenClosedType.CLOSED;
                } else if (data == Tilted) {
                    return OpenClosedType.OPEN;
                } else if (data == Open1 || data == Open2) {
                    return OpenClosedType.OPEN;
                }
        }

        return UnDefType.UNDEF;
    }

    @Override
    protected boolean validateData(int[] bytes) {
        return super.validateData(bytes) && getBit(bytes[0], 7) && getBit(bytes[0], 6);
    }
}
