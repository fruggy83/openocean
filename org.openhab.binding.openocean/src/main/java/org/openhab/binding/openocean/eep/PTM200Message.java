package org.openhab.binding.openocean.eep;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.binding.openocean.messages._RPSMessage;

public class PTM200Message extends _RPSMessage implements EEP {

    protected boolean _isValid = false;
    static final int On = 0x70;
    static final int Off = 0x50;
    protected boolean _on = false;

    public PTM200Message(_RPSMessage message) {
        this(message.getData().length, 0, message.getData());
    }

    public PTM200Message(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (payload != null && payload.length > 1) {
            _isValid = payload[1] == On || payload[1] == Off;
            _on = payload[1] == On;
        }
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    public boolean isOn() {
        return _isValid && _on;
    }

    public boolean isOff() {
        return _isValid && !_on;
    }

    public OnOffType getOnOff() {
        if (isOn()) {
            return OnOffType.ON;
        }

        return OnOffType.OFF;
    }
}
