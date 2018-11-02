package org.openhab.binding.openocean.internal.eep.A5_11;

import org.openhab.binding.openocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.CHANNEL_DIMMER;
import static org.openhab.binding.openocean.OpenOceanBindingConstants.CHANNEL_LIGHT_SWITCHING;

/**
 *
 * @author Vincent Bakker - Initial contribution
 */

public class A5_11_04 extends _4BSMessage {

    public enum Error {
        NO_ERROR_PRESENT,
        LAMP_FAILURE,
        INTERNAL_FAILURE,
        FAILURE_ON_THE_EXTERNAL_PERIPHERY;
    }

    public enum ParameterMode {
        EIGHT_BIT_DIMMER_VALUE_AND_LAMP_OPERATING_HOURS,
        RGB_VALUE,
        ENERGY_METERING_VALUE,
        NOT_USED
    }

    private static Logger logger = LoggerFactory.getLogger(A5_11_04.class);

    public A5_11_04(ERP1Message packet) {
        super(packet);
    }

    protected boolean isErrorState() {
        byte db0 = getDB_0();

        int state = (db0 >> 4) & 0x03;

        if (state != 0) {
            // TODO: display error state on thing
            logger.warn("Received error {}: {}", state, Error.values()[state]);
            return true;
        } else {
            return false;
        }
    }

    protected ParameterMode getParameterMode() {
        int pm = (getDB_0() >> 1) & 0x03;
        return ParameterMode.values()[pm];
    }

    protected State getLightingStatus() {
        byte db0 = getDB_0();
        boolean lightOn = getBit(db0, 0);

        return lightOn ? OnOffType.ON : OnOffType.OFF;
//        return UnDefType.UNDEF;
    }

    protected State getDimmerStatus () {
        if (getParameterMode() == ParameterMode.EIGHT_BIT_DIMMER_VALUE_AND_LAMP_OPERATING_HOURS) {
            return new PercentType(getDB_3Value()*100/255);
        }
        return UnDefType.UNDEF;
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {
        if (isErrorState()) {
            return UnDefType.UNDEF;
        }

        switch (channelId) {
            case CHANNEL_LIGHT_SWITCHING:
                return getLightingStatus();
            case CHANNEL_DIMMER:
                return getDimmerStatus();
        }

        return UnDefType.UNDEF;
    }


}
