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
        RADIO_ERP1((byte) 0x01),
        RESPONSE((byte) 0x02),
        RADIO_SUB_TEL((byte) 0x03),
        EVENT((byte) 0x04),
        COMMON_COMMAND((byte) 0x05),
        SMART_ACK_COMMAND((byte) 0x06),
        REMOTE_MAN_COMMAND((byte) 0x07),
        RADIO_MESSAGE((byte) 0x09),
        RADIO_ERP2((byte) 0x0A);

        private byte value;

        private ESPPacketType(byte value) {
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

    public byte[] serialize() {
        byte[] result = new byte[ENOCEAN_SYNC_BYTE_LENGTH + ENOCEAN_HEADER_LENGTH + ENOCEAN_CRC3_HEADER_LENGTH
                + data.length + optionalData.length + ENOCEAN_CRC8_DATA_LENGTH];

        result[0] = Helper.ENOCEAN_SYNC_BYTE;
        result[1] = (byte) ((data.length >> 8) & 0xff);
        result[2] = (byte) (data.length & 0xff);
        result[3] = (byte) (optionalData.length & 0xff);
        result[4] = packetType.value;
        result[5] = Helper.calcCRC8(result, ENOCEAN_SYNC_BYTE_LENGTH, ENOCEAN_HEADER_LENGTH);

        return result;
    }
}
