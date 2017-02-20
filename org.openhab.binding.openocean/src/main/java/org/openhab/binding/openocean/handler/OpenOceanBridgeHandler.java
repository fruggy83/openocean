/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.openocean.internal.OpenOceanConfigStatusMessage;
import org.openhab.binding.openocean.transceiver.OpenOceanSerialTransceiver;
import org.openhab.binding.openocean.transceiver.OpenOceanTransceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OpenOceanBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBridgeHandler extends ConfigStatusBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(OpenOceanBridgeHandler.class);

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_BRIDGE);

    private OpenOceanTransceiver transceiver;

    public OpenOceanBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(BASE_ID)) {
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.

        transceiver = new OpenOceanSerialTransceiver((String) getThing().getConfiguration().get(PORT));
        transceiver.Initialize();

        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @Override
    public void dispose() {
        if (transceiver != null) {
            transceiver.ShutDown();
        }
        super.dispose();
    }

    @Override
    public void handleRemoval() {
        if (transceiver != null) {
            transceiver.ShutDown();
        }
        super.handleRemoval();
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {

        // The serial port must be provided
        final String bridgePort = (String) getThing().getConfiguration().get(PORT);
        Collection<ConfigStatusMessage> configStatusMessages;

        // Check whether an IP address is provided
        if (bridgePort == null || bridgePort.isEmpty()) {
            configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(PORT)
                    .withMessageKeySuffix(OpenOceanConfigStatusMessage.PORT_MISSING.getMessageKey()).withArguments(PORT)
                    .build());
        } else {
            configStatusMessages = Collections.emptyList();
        }

        return configStatusMessages;
    }
}
