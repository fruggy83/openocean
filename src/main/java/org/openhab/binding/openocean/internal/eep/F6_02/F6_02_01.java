/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.F6_02;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.CommonTriggerEvents;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.openocean.internal.eep.Base._RPSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class F6_02_01 extends _RPSMessage {

    final int AI = 0;
    final int A0 = 1;
    final int BI = 2;
    final int B0 = 3;
    final int PRESSED = 16;

    int secondByte = -1;

    public F6_02_01() {
        super();
    }

    public F6_02_01(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {

        if (command instanceof OnOffType) {
            setStatus(_RPSMessage.T21Flag | _RPSMessage.NUFlag);

            switch (channelId) {
                case CHANNEL_GENERALSWITCH_CHANNELA:
                    if ((OnOffType) command == OnOffType.ON) {
                        setData((A0 << 5) | PRESSED);
                        secondByte = (A0 << 5);
                    } else {
                        setData((AI << 5) | PRESSED);
                        secondByte = (AI << 5);
                    }
                    break;

                case CHANNEL_GENERALSWITCH_CHANNELB:
                    if ((OnOffType) command == OnOffType.ON) {
                        setData((B0 << 5) | PRESSED);
                        secondByte = (B0 << 5);
                    } else {
                        setData((BI << 5) | PRESSED);
                        secondByte = (BI << 5);
                    }
                    break;
            }
        }
    }

    @Override
    protected String convertToEventImpl(String channelId, String lastEvent, Configuration config) {
        if (!isValid()) {
            return null;
        }

        if (t21 && nu) {

            switch (channelId) {
                case CHANNEL_ROCKERSWITCH_CHANNELA:
                    if ((bytes[0] >> 5) == A0) {
                        return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR1_PRESSED
                                : CommonTriggerEvents.DIR1_RELEASED;
                    } else if ((bytes[0] >> 5) == AI) {
                        return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR2_PRESSED
                                : CommonTriggerEvents.DIR2_RELEASED;
                    }
                    return null;

                case CHANNEL_ROCKERSWITCH_CHANNELB:
                    if ((bytes[0] >> 5) == B0) {
                        return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR1_PRESSED
                                : CommonTriggerEvents.DIR1_RELEASED;
                    } else if ((bytes[0] >> 5) == BI) {
                        return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR2_PRESSED
                                : CommonTriggerEvents.DIR2_RELEASED;
                    }
                    return null;
            }
        } else if (t21 && !nu) {
            if (lastEvent != null && lastEvent.equals(CommonTriggerEvents.DIR1_PRESSED)) {
                return CommonTriggerEvents.DIR1_RELEASED;
            } else if (lastEvent != null && lastEvent.equals(CommonTriggerEvents.DIR2_PRESSED)) {
                return CommonTriggerEvents.DIR2_RELEASED;
            }
        }

        return null;
    }

    @Override
    public boolean PrepareNextMessage() {
        if (secondByte != -1) {
            setData(secondByte);
            secondByte = -1;
            return true;
        }

        return false;
    }

}
