/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.THING_TYPE_BRIDGE;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.openocean.handler.OpenOceanBaseActuatorHandler;
import org.openhab.binding.openocean.handler.OpenOceanBaseSensorHandler;
import org.openhab.binding.openocean.handler.OpenOceanBaseThingHandler;
import org.openhab.binding.openocean.handler.OpenOceanBridgeHandler;
import org.openhab.binding.openocean.internal.discovery.OpenOceanDeviceDiscoveryService;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Sets;

/**
 * The {@link OpenOceanHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Sets
            .union(OpenOceanBridgeHandler.SUPPORTED_THING_TYPES, OpenOceanBaseThingHandler.SUPPORTED_THING_TYPES);

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

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
            Thing thing = super.createThing(thingTypeUID, configuration, thingId, bridgeUID);

            return thing;
        }

        throw new IllegalArgumentException("The thing type " + thingTypeUID + " is not supported by the binding.");
    }

    protected ThingUID createthingUID(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID,
            ThingUID bridgeUID) {

        String id = null;
        if (OpenOceanBaseActuatorHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            id = thingUID.getId();
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
            OpenOceanBridgeHandler bridgeHandler = new OpenOceanBridgeHandler((Bridge) thing);
            registerDeviceDiscoveryService(bridgeHandler);
            return bridgeHandler;
        } else if (OpenOceanBaseActuatorHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            return new OpenOceanBaseActuatorHandler(thing);
        } else if (OpenOceanBaseSensorHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            return new OpenOceanBaseSensorHandler(thing);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (this.discoveryServiceRegs != null) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

    private void registerDeviceDiscoveryService(OpenOceanBridgeHandler handler) {
        OpenOceanDeviceDiscoveryService discoveryService = new OpenOceanDeviceDiscoveryService(handler);
        discoveryService.activate();
        this.discoveryServiceRegs.put(handler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }
}
