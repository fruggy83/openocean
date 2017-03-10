package org.openhab.binding.openocean.handler;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

public abstract class OpenOceanBaseSensorHandler extends OpenOceanBaseThingHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.emptySet();

    protected String enoceanId;

    public OpenOceanBaseSensorHandler(Thing thing) {
        super(thing);
    }

    @Override
    boolean validateConfig() {
        enoceanId = thing.getUID().getId();
        return validateEnoceanId(enoceanId);
    }

    @Override
    boolean initializeIdForSending() {
        return true;
    }

    @Override
    public long getSenderIdToListenTo() {
        return Long.parseLong(enoceanId, 16);
    }
}
