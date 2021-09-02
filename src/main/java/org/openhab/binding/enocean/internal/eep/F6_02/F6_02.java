/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.enocean.internal.eep.F6_02;

import org.openhab.binding.enocean.internal.config.EnOceanChannelRockerSwitchActionConfig;
import org.openhab.binding.enocean.internal.eep.Base._RPSMessage;
import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.CommonTriggerEvents;
import org.openhab.core.types.State;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class F6_02 extends _RPSMessage {

    final byte AI = 0;
    final byte A0 = 1;
    final byte BI = 2;
    final byte B0 = 3;
    final byte PRESSED = 16;
    final byte PRESSED_SEC = 1;

    final String DIR1 = "DIR1";
    final String DIR2 = "DIR2";
    final String ANYDIR = "*";
    final String NODIR = "-";

    int secondByte = -1;
    int secondStatus = -1;

    public F6_02() {
        super();
    }

    public F6_02(ERP1Message packet) {
        super(packet);
    }

    private String getChannelADir() {
        if ((bytes[0] >>> 5) == A0 && (bytes[0] & PRESSED) != 0) {
            return DIR1;
        } else if ((bytes[0] >>> 5) == AI && (bytes[0] & PRESSED) != 0) {
            return DIR2;
        } else {
            return NODIR;
        }
    }

    private String getChannelBDir() {
        if ((bytes[0] >>> 5) == B0 && (bytes[0] & PRESSED) != 0) {
            return DIR1;
        } else if ((bytes[0] >>> 5) == BI && (bytes[0] & PRESSED) != 0) {
            return DIR2;
        } else if (((bytes[0] & 0xf) >>> 1) == B0 && (bytes[0] & PRESSED_SEC) != 0) {
            return DIR1;
        } else if (((bytes[0] & 0xf) >>> 1) == BI && (bytes[0] & PRESSED_SEC) != 0) {
            return DIR2;
        } else {
            return NODIR;
        }
    }

    protected String getRockerSwitchAction(Configuration config) {
        EnOceanChannelRockerSwitchActionConfig conf = config.as(EnOceanChannelRockerSwitchActionConfig.class);
        String dirA = getChannelADir();
        String dirB = getChannelBDir();

        if (!(conf.channelAFilter.equals(ANYDIR) || conf.channelAFilter.equals(dirA))) {
            return null;
        } else if (!(conf.channelBFilter.equals(ANYDIR) || conf.channelBFilter.equals(dirB))) {
            return null;
        } else {
            return dirA + "|" + dirB;
        }
    }

    protected String getChannelEvent(byte dir1, byte dir2) {
        if ((bytes[0] >>> 5) == dir1) {
            return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR1_PRESSED : CommonTriggerEvents.DIR1_RELEASED;
        } else if ((bytes[0] >>> 5) == dir2) {
            return ((bytes[0] & PRESSED) != 0) ? CommonTriggerEvents.DIR2_PRESSED : CommonTriggerEvents.DIR2_RELEASED;
        } else if (((bytes[0] & 0xf) >>> 1) == dir1) {
            return ((bytes[0] & PRESSED_SEC) != 0) ? CommonTriggerEvents.DIR1_PRESSED
                    : CommonTriggerEvents.DIR1_RELEASED;
        } else if (((bytes[0] & 0xf) >>> 1) == dir2) {
            return ((bytes[0] & PRESSED_SEC) != 0) ? CommonTriggerEvents.DIR2_PRESSED
                    : CommonTriggerEvents.DIR2_RELEASED;
        } else {
            return null;
        }
    }

    protected State inverse(OnOffType currentState) {
        return currentState == OnOffType.ON ? OnOffType.OFF : OnOffType.ON;
    }

    protected State inverse(UpDownType currentState) {
        return currentState == UpDownType.UP ? UpDownType.DOWN : UpDownType.UP;
    }

    @Override
    protected boolean validateData(byte[] bytes) {
        return super.validateData(bytes) && !getBit(bytes[0], 7);
    }
}
