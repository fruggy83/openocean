package org.openhab.binding.openocean.eep;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.binding.openocean.messages.ESP3PacketFactory;
import org.openhab.binding.openocean.messages._4BSMessage;

public class EEPFactory {

    public static _4BSMessage createA5_38_08_Switching(OnOffType onOff, boolean teachIn, int[] senderId) {
        int teachInBit = (teachIn ? 0x00 : 0x08);

        if (onOff == OnOffType.ON) {
            return new _4BSMessage(10, 0, new int[] { ESP3PacketFactory.A5, 0x01, 0x00, 0x00, (teachInBit | 0x01),
                    senderId[0], senderId[1], senderId[2], senderId[3], 0x00 });
        } else {
            return new _4BSMessage(10, 0, new int[] { ESP3PacketFactory.A5, 0x01, 0x00, 0x00, (teachInBit | 0x00),
                    senderId[0], senderId[1], senderId[2], senderId[3], 0x00 });
        }
    }
}
