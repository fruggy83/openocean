package org.openhab.binding.openocean.messages;

public class CCMessage extends ESP3Packet {

    public CCMessage(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.COMMON_COMMAND, payload);

    }

    @Override
    public int[] getSenderId() {
        return null;
    }

}
