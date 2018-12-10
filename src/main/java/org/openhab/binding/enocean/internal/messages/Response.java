/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.messages;

import java.security.InvalidParameterException;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class Response extends ESP3Packet {

    public enum ResponseType {
        RET_OK((byte) 0x00),
        RET_ERROR((byte) 0x01),
        RET_NOT_SUPPORTED((byte) 0x02),
        RET_WRONG_PARAM((byte) 0x03),
        RET_OPERATION_DENIED((byte) 0x04),
        RET_LOCK_SET((byte) 0x05),
        RET_BUFFER_TO_SMALL((byte) 0x06),
        RET_NO_FREE_BUFFER((byte) 0x07),
        RET_FLASH_HW_ERROR((byte) 0x82),
        RET_BASEID_OUT_OF_RANGE((byte) 0x90),
        RET_BASEID_MAX_REACHED((byte) 0x91);

        private byte value;

        ResponseType(byte value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ResponseType getResponsetype(byte value) {
            for (ResponseType t : ResponseType.values()) {
                if (t.value == value) {
                    return t;
                }
            }

            throw new InvalidParameterException("Unknown response type");
        }

    }

    protected ResponseType responseType;
    protected boolean _isValid = false;

    Response(int dataLength, int optionalDataLength, byte[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.RESPONSE, payload);

        try {
            responseType = ResponseType.getResponsetype(payload[0]);
        } catch (Exception e) {
            responseType = ResponseType.RET_ERROR;
        }
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public boolean isOK() {
        return responseType == ResponseType.RET_OK;
    }

    public boolean isValid() {
        return _isValid;
    }
}
