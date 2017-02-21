package org.openhab.binding.openocean.transceiver;

import java.security.InvalidParameterException;

public class ESP3Packet {

    protected static final int ENOCEAN_HEADER_LENGTH = 4;
    protected static final int ENOCEAN_SYNC_BYTE_LENGTH = 1;
    protected static final int ENOCEAN_CRC3_HEADER_LENGTH = 1;
    protected static final int ENOCEAN_CRC8_DATA_LENGTH = 1;

    private ESPPacketType packetType;
    private int[] data;
    private int[] optionalData = null;

    public enum ESPPacketType {
        RADIO_ERP1(0x01),
        RESPONSE(0x02),
        RADIO_SUB_TEL(0x03),
        EVENT(0x04),
        COMMON_COMMAND(0x05),
        SMART_ACK_COMMAND(0x06),
        REMOTE_MAN_COMMAND(0x07),
        RADIO_MESSAGE(0x09),
        RADIO_ERP2(0x0A);

        private int value;

        private ESPPacketType(int value) {
            this.value = value;
        }

        public static boolean hasValue(int value) {
            for (ESPPacketType p : ESPPacketType.values()) {
                if (p.value == value) {
                    return true;
                }
            }

            return false;
        }

        public static ESPPacketType getPacketType(int packetType) {
            for (ESPPacketType p : ESPPacketType.values()) {
                if (p.value == packetType) {
                    return p;
                }
            }

            throw new InvalidParameterException("Unknown packetType value");
        }

    }

    public ESP3Packet(int dataLength, int optionalDataLength, int packetType, int[] payload) {

        if (!ESPPacketType.hasValue(packetType)) {
            throw new InvalidParameterException("Packet type is unknown");
        }

        if (dataLength + optionalDataLength > data.length) {
            throw new InvalidParameterException("data length does not match provided lengths");
        }

        this.packetType = ESPPacketType.getPacketType(packetType);
        this.data = new int[dataLength];
        System.arraycopy(payload, 0, this.data, 0, dataLength);
        if (optionalDataLength > 0) {
            this.optionalData = new int[optionalDataLength];
            System.arraycopy(payload, dataLength, optionalDataLength, 0, optionalDataLength);
        } else {
            this.optionalData = new int[0];
        }
    }

    public ESP3Packet(int dataLength, int optionalDataLength, ESPPacketType packetType, int[] payload) {

        this(dataLength, optionalDataLength, packetType.value, payload);
    }

    public ESPPacketType getPacketType() {
        return this.packetType;
    }

    public byte[] serialize()
    {
        byte[] result = new byte[data.length + optionalData.length + ENOCEAN_HEADER_LENGTH + ENOCEAN_SYNC_BYTE_LENGTH + ENOCEAN_CRC3_HEADER_LENGTH + ENOCEAN_CRC8_DATA_LENGTH];

        result[0] =

        return result;
    }
}
