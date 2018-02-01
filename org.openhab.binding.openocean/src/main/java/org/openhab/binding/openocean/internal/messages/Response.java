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

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class Response extends ESP3Packet {

    public enum ResponseType {
        RET_OK(0x00),
        RET_ERROR(0x01),
        RET_NOT_SUPPORTED(0x02),
        RET_WRONG_PARAM(0x03),
        RET_OPERATION_DENIED(0x04),
        RET_LOCK_SET(0x05),
        RET_BUFFER_TO_SMALL(0x06),
        RET_NO_FREE_BUFFER(0x07);

        private int value;

        ResponseType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ResponseType getResponsetype(int value) {
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

    Response(int dataLength, int optionalDataLength, int[] payload) {
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
