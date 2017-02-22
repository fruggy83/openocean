package org.openhab.binding.openocean.messages;

import org.openhab.binding.openocean.transceiver.ESP3Packet;

public abstract class EnoceanMessage {

    protected ESP3Packet packet;

    public EnoceanMessage(ESP3Packet packet) {
        this.packet = packet;
    }

    public int[] getData() {
        return packet.getData();
    }

    public int[] getData(int offset, int length) {
        return packet.getData(offset, length);
    }

    public int[] getOptionalData() {
        return packet.getOptionalData();
    }

    public int[] getOptionalData(int offset, int length) {
        return packet.getOptionalData(offset, length);
    }
}
