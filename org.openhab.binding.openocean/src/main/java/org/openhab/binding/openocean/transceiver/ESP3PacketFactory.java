package org.openhab.binding.openocean.transceiver;

import org.openhab.binding.openocean.transceiver.ESP3Packet.ESPPacketType;

public class ESP3PacketFactory {

    public final static ESP3Packet CO_RD_IDBASE = new ESP3Packet(1, 0, ESPPacketType.COMMON_COMMAND, new int[] { 8 });

}
