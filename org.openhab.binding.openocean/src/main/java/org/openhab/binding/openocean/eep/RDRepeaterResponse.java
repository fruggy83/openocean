package org.openhab.binding.openocean.eep;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.openocean.messages.Response;

public class RDRepeaterResponse extends Response implements EEP {

    protected String repeaterLevel;
    protected boolean _isValid = false;

    public RDRepeaterResponse(Response response) {
        this(response.getData().length, 0, response.getData());
    }

    public RDRepeaterResponse(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (data.length < 3) {
            return;
        }

        if (data[1] == 0) {
            repeaterLevel = REPEATERMODE_OFF;
        } else if (data[1] == 1 || data[1] == 2) {
            switch (data[2]) {
                case 1:
                    repeaterLevel = REPEATERMODE_LEVEL_1;
                    break;
                case 2:
                    repeaterLevel = REPEATERMODE_LEVEL_2;
                    break;
                case 0:
                    repeaterLevel = REPEATERMODE_OFF;
                    break;
                default:
                    return;
            }

            _isValid = true;
        }
    }

    public StringType getRepeaterLevel() {
        return StringType.valueOf(repeaterLevel);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

}
