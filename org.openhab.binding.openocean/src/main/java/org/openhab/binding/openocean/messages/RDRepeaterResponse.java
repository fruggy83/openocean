package org.openhab.binding.openocean.messages;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.core.library.types.StringType;

public class RDRepeaterResponse extends Response {

    protected String repeaterLevel;

    public RDRepeaterResponse(Response packet) {
        super(packet.packet);

        int data[] = packet.getData();
        if (data[1] == 0) {
            repeaterLevel = REPEATERMODE_OFF;
        } else {
            switch (data[2]) {
                case 1:
                    repeaterLevel = REPEATERMODE_LEVEL_1;
                    break;
                case 2:
                    repeaterLevel = REPEATERMODE_LEVEL_2;
                    break;
                default:
                    repeaterLevel = REPEATERMODE_OFF;
                    break;
            }
        }
    }

    public StringType getRepeaterLevel() {
        return StringType.valueOf(repeaterLevel);
    }

}
