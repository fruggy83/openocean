package org.openhab.binding.openocean.transceiver;

import org.openhab.binding.openocean.messages.ESP3Packet;

public interface ESP3PacketListener {

    public void espPacketReceived(ESP3Packet packet);

    public long getSenderIdToListenTo();
}
