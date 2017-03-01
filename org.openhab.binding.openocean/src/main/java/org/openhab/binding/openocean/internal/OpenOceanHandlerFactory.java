/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;
import org.openhab.binding.openocean.handler.OpenOceanBaseActuatorHandler;
import org.openhab.binding.openocean.handler.OpenOceanBaseSensorHandler;
import org.openhab.binding.openocean.handler.OpenOceanBaseThingHandler;
import org.openhab.binding.openocean.handler.OpenOceanBridgeHandler;
import org.openhab.binding.openocean.handler.OpenOceanSwitchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The {@link OpenOceanHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanHandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(OpenOceanHandlerFactory.class);

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Sets
            .union(OpenOceanBridgeHandler.SUPPORTED_THING_TYPES, OpenOceanSwitchHandler.SUPPORTED_THING_TYPES);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    public Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID,
            ThingUID bridgeUID) {

        if (OpenOceanBridgeHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            return super.createThing(thingTypeUID, configuration, thingUID, null);
        }
        if (OpenOceanBaseThingHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            ThingUID thingId = createthingUID(thingTypeUID, configuration, thingUID, bridgeUID);
            return super.createThing(thingTypeUID, configuration, thingId, bridgeUID);
        }

        throw new IllegalArgumentException("The thing type " + thingTypeUID + " is not supported by the binding.");
    }

    protected ThingUID createthingUID(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID,
            ThingUID bridgeUID) {

        String id = null;
        if (OpenOceanBaseActuatorHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            OpenOceanActuatorConfig config = configuration.as(OpenOceanActuatorConfig.class);
            id = thingUID.getId() + "_" + Integer.toString(config.channel);
        } else if (OpenOceanBaseSensorHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            id = thingUID.getId();
        } else {
            throw new IllegalArgumentException("The thing type " + thingTypeUID + " is not supported by the binding.");
        }

        return new ThingUID(thingTypeUID, id, bridgeUID.getId());
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_BRIDGE)) {
            return new OpenOceanBridgeHandler((Bridge) thing);
        } else if (thingTypeUID.equals(THING_TYPE_SWITCHINGACTUATOR)) {
            return new OpenOceanSwitchHandler(thing);
        }

        return null;
    }
}
