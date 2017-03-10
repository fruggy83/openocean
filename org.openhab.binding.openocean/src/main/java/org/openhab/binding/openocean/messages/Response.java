package org.openhab.binding.openocean.messages;

import java.security.InvalidParameterException;

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

    public Response(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.RESPONSE, payload);

        try {
            responseType = ResponseType.getResponsetype(data[0]);
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

    @Override
    public int[] getSenderId() {
        return null;
    }
}
