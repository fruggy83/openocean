/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.messages;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.openhab.binding.openocean.internal.eep.UTEResponse;
import org.openhab.binding.openocean.internal.eep._1BSMessage;
import org.openhab.binding.openocean.internal.eep._4BSMessage;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class ERP1Message extends ESP3Packet {

    public enum RORG {
        Unknown(0x00, 0),
        RPS(0xF6, 1),
        _1BS(0xD5, 1),
        _4BS(0xA5, 4),
        VLD(0xD2, -1),
        // ADT(0xA6, -1),
        UTE(0xD4, -1);

        private int value;
        private int dataLength;

        RORG(int value, int dataLength) {
            this.value = value;
            this.dataLength = dataLength;
        }

        public int getValue() {
            return this.value;
        }

        public int getDataLength() {
            return dataLength;
        }

        public static RORG getRORG(int value) {
            for (RORG t : RORG.values()) {
                if (t.value == value) {
                    return t;
                }
            }

            throw new InvalidParameterException("Unknown choice");
        }
    }

    protected RORG rorg;

    int[] senderId;
    boolean teachIn;

    public ERP1Message() {
        super.setPacketType(ESPPacketType.RADIO_ERP1);
    }

    public ERP1Message(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.RADIO_ERP1, payload);

        teachIn = false;
        senderId = new int[0];
        try {
            rorg = RORG.getRORG(payload[0]);

            switch (rorg) {
                case RPS: // treat each RPS message as a teach in message
                case _1BS:
                    if (dataLength >= 6) {
                        senderId = Arrays.copyOfRange(payload, 2, 6);
                        teachIn = rorg == RORG.RPS || ((_1BSMessage.TeachInBit & payload[1]) == 0);
                    }
                    break;
                case _4BS:
                    if (dataLength >= 9) {
                        senderId = Arrays.copyOfRange(payload, 5, 9);
                        teachIn = (_4BSMessage.TeachInBit & payload[4]) == 0;
                    }
                    break;
                case VLD:
                    teachIn = false;
                    senderId = Arrays.copyOfRange(payload, dataLength - 5, dataLength - 1);
                    break;
                case UTE:
                    if (dataLength >= 6) {
                        teachIn = (payload[1] & UTEResponse.TeachIn_MASK) == 0
                                || (payload[1] & UTEResponse.TeachIn_MASK) == UTEResponse.TeachIn_NotSpecified;
                        senderId = Arrays.copyOfRange(payload, dataLength - 5, dataLength - 1);
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            rorg = RORG.Unknown;
            senderId = new int[0];
        }
    }

    public RORG getRORG() {
        return rorg;
    }

    public final int[] getSenderId() {
        return senderId;
    }

    public boolean getIsTeachIn() {
        return teachIn;
    }
}
