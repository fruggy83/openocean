/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep.Base;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class PTM200Message extends _RPSMessage {

    static final byte On = 0x70;
    static final byte Off = 0x50;
    static final byte Up = 0x70;
    static final byte Down = 0x50;
    static final byte Open = (byte) 0xE0;
    static final byte Closed = (byte) 0xF0;

    public PTM200Message() {
        super();
    }

    public PTM200Message(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(String channelId, String channelTypeId, Command command, State currentState, Configuration config) {

    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, State currentState, Configuration config) {
        if (!isValid()) {
            return UnDefType.UNDEF;
        }

        switch (channelId) {
            case CHANNEL_GENERAL_SWITCHING:
                return bytes[0] == On ? OnOffType.ON : OnOffType.OFF;
            case CHANNEL_ROLLERSHUTTER:
                return bytes[0] == Up ? PercentType.ZERO : (bytes[0] == Down ? PercentType.HUNDRED : UnDefType.UNDEF);
            case CHANNEL_CONTACT:
                return bytes[0] == Open ? OpenClosedType.OPEN
                        : (bytes[0] == Closed ? OpenClosedType.CLOSED : UnDefType.UNDEF);
        }

        return UnDefType.UNDEF;
    }
}
