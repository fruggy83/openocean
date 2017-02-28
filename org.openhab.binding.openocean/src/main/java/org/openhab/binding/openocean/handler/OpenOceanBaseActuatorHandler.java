package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;

public class OpenOceanBaseActuatorHandler extends OpenOceanBaseThingHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_SWITCHINGACTUATOR);

    public OpenOceanBaseActuatorHandler(Thing thing) {
        super(thing);
    }

    private boolean checkId() {

        OpenOceanActuatorConfig cfg = getConfigAs(OpenOceanActuatorConfig.class);

        int offset = cfg.offsetId;
        if (offset == 0) {
            Configuration config = editConfiguration();
            config.put(OFFSETID, 1);
            updateConfiguration(config);
        }

        // thingId = Helper.addOffsetToBaseId(offset, gateway.getBaseId());

        return true;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }

    @Override
    boolean prepareThing() {
        return checkId();
    }

}
