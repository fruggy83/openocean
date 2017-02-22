package org.openhab.binding.openocean.transceiver;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.openocean.OpenOceanBindingConstants;
import org.openhab.binding.openocean.transceiver.ESP3Packet.ESPPacketType;

public class ESP3PacketFactory {

    public final static ESP3Packet CO_RD_VERSION = new ESP3Packet(1, 0, ESPPacketType.COMMON_COMMAND, new int[] { 3 });
    public final static ESP3Packet CO_RD_IDBASE = new ESP3Packet(1, 0, ESPPacketType.COMMON_COMMAND, new int[] { 8 });
    public final static ESP3Packet CO_RD_REPEATER = new ESP3Packet(1, 0, ESPPacketType.COMMON_COMMAND,
            new int[] { 10 });

    public static ESP3Packet CO_WR_REPEATER(StringType level) {
        switch (level.toString()) {
            case OpenOceanBindingConstants.REPEATERMODE_OFF:
                return new ESP3Packet(3, 0, ESPPacketType.COMMON_COMMAND, new int[] { 9, 0, 0 });
            case OpenOceanBindingConstants.REPEATERMODE_LEVEL_1:
                return new ESP3Packet(3, 0, ESPPacketType.COMMON_COMMAND, new int[] { 9, 1, 1 });
            default:
                return new ESP3Packet(3, 0, ESPPacketType.COMMON_COMMAND, new int[] { 9, 1, 2 });
        }
    }
}
