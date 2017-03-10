package org.openhab.binding.openocean.messages;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.openocean.OpenOceanBindingConstants;
import org.openhab.binding.openocean.messages.ESP3Packet.ESPPacketType;

public class ESP3PacketFactory {

    public final static ESP3Packet CO_RD_VERSION = new CCMessage(1, 0, new int[] { 3 });
    public final static ESP3Packet CO_RD_IDBASE = new CCMessage(1, 0, new int[] { 8 });
    public final static ESP3Packet CO_RD_REPEATER = new CCMessage(1, 0, new int[] { 10 });

    public final static int F6 = 0xf6;
    public final static int D5 = 0xd5;
    public final static int A5 = 0xa5;
    public final static int D2 = 0xd2;

    public static ESP3Packet CO_WR_REPEATER(StringType level) {
        switch (level.toString()) {
            case OpenOceanBindingConstants.REPEATERMODE_OFF:
                return new CCMessage(3, 0, new int[] { 9, 0, 0 });
            case OpenOceanBindingConstants.REPEATERMODE_LEVEL_1:
                return new CCMessage(3, 0, new int[] { 9, 1, 1 });
            default:
                return new CCMessage(3, 0, new int[] { 9, 1, 2 });
        }
    }

    public static ESP3Packet BuildPacket(int dataLength, int optionalDataLength, int packetType, int[] payload) {
        ESPPacketType type = ESPPacketType.getPacketType(packetType);

        switch (type) {
            case RESPONSE:
                return new Response(dataLength, optionalDataLength, payload);
            case RADIO_ERP1:
                if (payload[0] == A5) {
                    return new _4BSMessage(dataLength, optionalDataLength, payload);
                }
                if ((payload[0] & 0xff) == F6) {
                    return new _RPSMessage(dataLength, optionalDataLength, payload);
                }
                break;
        }

        return null;
    }
}
