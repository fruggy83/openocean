package org.openhab.binding.openocean.messages;

public class _4BSMessage extends ESP3Packet {

    protected int[] senderId;
    protected int[] bytes;

    public _4BSMessage(int byte0, int byte1, int byte2, int byte3, int[] senderId) {
        this(10, 0, new int[] { ESP3PacketFactory.A5, byte3, byte2, byte1, byte0, senderId[0], senderId[1], senderId[2],
                senderId[3], 0x00 });

    }

    public _4BSMessage(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.RADIO_ERP1, payload);

        if (payload != null && payload.length > 9) {
            senderId = new int[4];
            System.arraycopy(payload, 5, senderId, 0, 4);

            bytes = new int[4];
            bytes[3] = payload[1] & 0xff;
            bytes[2] = payload[2] & 0xff;
            bytes[1] = payload[3] & 0xff;
            bytes[0] = payload[4] & 0xff;
        }
    }

    @Override
    public int[] getSenderId() {
        return senderId.clone();
    }

    public int getByte(int num) {
        if (num < 0 || num > 3) {
            throw new IllegalArgumentException("num must be between 0 and 3");
        }

        return bytes[num];
    }
}
