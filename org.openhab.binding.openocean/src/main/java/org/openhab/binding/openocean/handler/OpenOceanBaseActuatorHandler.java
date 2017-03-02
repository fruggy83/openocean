package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;

public abstract class OpenOceanBaseActuatorHandler extends OpenOceanBaseThingHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_SWITCHINGACTUATOR);

    public OpenOceanBaseActuatorHandler(Thing thing) {
        super(thing);
    }

    @Override
    boolean validateConfig() {
        String[] tokens = thing.getUID().getId().split(ACTUATOR_ID_SPLITTER);
        if (tokens.length != 2) {
            return false;
        }

        try {
            Integer.parseInt(tokens[1]);
        } catch (Exception e) {
            return false;
        }

        return validateThingId(tokens[0]);
    }

    @Override
    boolean initializeIdForSending() {
        OpenOceanActuatorConfig cfg = getConfigAs(OpenOceanActuatorConfig.class);
        String senderId = cfg.senderId;

        if (senderId.isEmpty()) {
            Configuration config = editConfiguration();
            OpenOceanBridgeHandler bridgeHandler = getBridgeHandler();
            if (bridgeHandler != null) {
                senderId = bridgeHandler.getNextId();
                if (offset == -1) {
                    return false;
                }
                config.put(SENDERID, senderId);
                updateConfiguration(config);
            }
        } else {
            return validateThingId(senderId);
        }

        // super.sendingId = Helper.addOffsetToBaseId(offset, getBridgeHandler().getBaseId());

        return true;
    }

}
