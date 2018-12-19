/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep.A5_14;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 * Window/Door-Sensor with States Open/Closed/Tilt, Supply voltage monitor
 *
 * @author Dominik Krickl-Vorreiter - Initial contribution
 */
public class A5_14_09 extends A5_14 {
    public final byte CLOSED = (byte) 0x00;
    public final byte TILTED = (byte) 0x01;
    public final byte OPEN = (byte) 0x03;

    public A5_14_09(ERP1Message packet) {
        super(packet);
    }

    private State getWindowhandleState() {
        byte ct = (byte) ((getDB_0() & 0x06) >> 1);

        switch (ct) {
            case CLOSED:
                return new StringType("CLOSED");
            case OPEN:
                return new StringType("OPEN");
            case TILTED:
                return new StringType("TILTED");
        }

        return UnDefType.UNDEF;
    }

    private State getContact() {
        byte ct = (byte) ((getDB_0() & 0x06) >> 1);

        switch (ct) {
            case CLOSED:
                return OpenClosedType.CLOSED;
            case OPEN:
            case TILTED:
                return OpenClosedType.OPEN;
        }

        return UnDefType.UNDEF;
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, State currentState,
            Configuration config) {
        switch (channelId) {
            case CHANNEL_WINDOWHANDLESTATE:
                return getWindowhandleState();
            case CHANNEL_CONTACT:
                return getContact();
        }

        return super.convertToStateImpl(channelId, channelTypeId, currentState, config);
    }
}