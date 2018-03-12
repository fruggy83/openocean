/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.A5_3F;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.config.OpenOceanChannelRollershutterConfig;
import org.openhab.binding.openocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ERP1Message.RORG;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_3F_7F_EltakoFSB extends _4BSMessage {

    static final int Stop = 0x00;
    static final int MoveUp = 0x01;
    static final int MoveDown = 0x02;

    static final int Up = 0x70;
    static final int Down = 0x50;

    public A5_3F_7F_EltakoFSB() {
        super();
    }

    public A5_3F_7F_EltakoFSB(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {
        int teachInBit = TeachInBit; // (getIsTeachIn() ? Zero : TeachInBit);
        int shutTime = 255;
        if (config != null) {
            shutTime = config.as(OpenOceanChannelRollershutterConfig.class).shutTime;
        }

        if (command instanceof PercentType) {
            PercentType target = (PercentType) command;
            if (target.intValue() == PercentType.ZERO.intValue()) {
                setData(Zero, shutTime, MoveUp, teachInBit); // => move completely up
            } else if (target.intValue() == PercentType.HUNDRED.intValue()) {
                setData(Zero, shutTime, MoveDown, teachInBit); // => move completely down
            } else if (currentState != null) {
                PercentType current = (PercentType) currentState.as(PercentType.class);
                if (config != null && current != null) {
                    if (current.intValue() != target.intValue()) {
                        int direction = current.intValue() > target.intValue() ? MoveUp : MoveDown;
                        int duration = Math.min(255, (Math.abs(current.intValue() - target.intValue()) * shutTime)
                                / PercentType.HUNDRED.intValue());

                        setData(Zero, duration, direction, teachInBit);
                    }
                }
            }

        } else if (command instanceof UpDownType) {
            if ((UpDownType) command == UpDownType.UP) {
                setData(Zero, shutTime, MoveUp, teachInBit); // => 0 percent
            } else if ((UpDownType) command == UpDownType.DOWN) {
                setData(Zero, shutTime, MoveDown, teachInBit); // => 100 percent
            }
        } else if (command instanceof StopMoveType) {
            if ((StopMoveType) command == StopMoveType.STOP) {
                setData(Zero, 0xFF, Stop, teachInBit);
            }
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {

        if (packet != null) {
            if (packet.getRORG() == RORG.RPS) {
                if (bytes[0] == Up) {
                    return PercentType.ZERO;
                } else if (bytes[0] == Down) {
                    return PercentType.HUNDRED;
                }

            } else if (packet.getRORG() == RORG._4BS && currentState != null) {
                int direction = getDB_1() == MoveUp ? -1 : 1;
                int duration = ((getDB_3() << 8) + getDB_2()) / 10; // => Time in DB3 and DB2 is given in ms

                PercentType current = (PercentType) currentState.as(PercentType.class);
                if (config != null && current != null) {
                    OpenOceanChannelRollershutterConfig c = config.as(OpenOceanChannelRollershutterConfig.class);
                    if (c.shutTime != -1) {
                        return new PercentType(Math.min(100, (Math.max(0, current.intValue()
                                + direction * ((duration * PercentType.HUNDRED.intValue()) / c.shutTime)))));
                    }
                }
            }
        }

        return UnDefType.UNDEF;

    }

    @Override
    protected int getDataLength() {
        if (packet == null || packet.getRORG() == RORG._4BS) {
            return super.getDataLength();
        }

        return packet.getRORG().getDataLength();
    }
}
