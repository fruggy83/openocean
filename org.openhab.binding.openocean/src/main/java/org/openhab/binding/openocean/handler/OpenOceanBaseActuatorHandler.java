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
    boolean initializeIdForSending() {

        OpenOceanActuatorConfig cfg = getConfigAs(OpenOceanActuatorConfig.class);

        int offset = cfg.offsetId;
        if (offset == 0) {
            Configuration config = editConfiguration();

            OpenOceanBridgeHandler bridgeHandler = getBridgeHandler();
            if (bridgeHandler != null) {
                offset = bridgeHandler.getNextId();
                if (offset == -1) {
                    return false;
                }
                config.put(OFFSETID, offset);
                updateConfiguration(config);
            } else {
                return false;
            }
        }

        // super.sendingId = Helper.addOffsetToBaseId(offset, getBridgeHandler().getBaseId());

        return true;
    }

}
