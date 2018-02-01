/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.discovery;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceCallback;
import org.eclipse.smarthome.config.discovery.ExtendedDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.openocean.handler.OpenOceanBridgeHandler;
import org.openhab.binding.openocean.internal.eep.EEP;
import org.openhab.binding.openocean.internal.eep.EEPFactory;
import org.openhab.binding.openocean.internal.eep.UTEResponse;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ERP1Message.RORG;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.transceiver.ESP3PacketListener;
import org.openhab.binding.openocean.internal.transceiver.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OpenOceanDeviceDiscoveryService} is used to discover Enocean devices and to accept teach in requests.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanDeviceDiscoveryService extends AbstractDiscoveryService
        implements ESP3PacketListener, ExtendedDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(OpenOceanDeviceDiscoveryService.class);

    private OpenOceanBridgeHandler bridgeHandler;
    DiscoveryServiceCallback discoveryServiceCallback;

    public OpenOceanDeviceDiscoveryService() {
        super(null, 60, false);

    }

    public OpenOceanDeviceDiscoveryService(OpenOceanBridgeHandler bridgeHandler) {
        super(null, 60, false);
        this.bridgeHandler = bridgeHandler;
    }

    /**
     * Called on component activation.
     */
    public void activate() {
        super.activate(null);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    protected void startScan() {
        logger.debug("Starting EnOcean discovery and accepting teach in requests");
        bridgeHandler.startDiscovery(this);
    }

    @Override
    public synchronized void stopScan() {
        logger.debug("Stopping EnOcean discovery scan");
        bridgeHandler.stopDiscovery();
        super.stopScan();
    }

    @Override
    public Set<@NonNull ThingTypeUID> getSupportedThingTypes() {
        return SUPPORTED_DEVICE_THING_TYPES_UIDS;
    }

    @Override
    public void espPacketReceived(ESP3Packet packet) {
        ERP1Message msg = (ERP1Message) packet;

        EEP eep = EEPFactory.buildEEPFromTeachInERP1(msg);
        if (eep == null) {
            return;
        }

        int[] id = eep.getSenderId();
        ThingTypeUID thingTypeUID = eep.getThingTypeUID();
        ThingUID thingUID = new ThingUID(thingTypeUID, bridgeHandler.getThing().getUID(), Helper.bytesToHexString(id));

        if (discoveryServiceCallback.getExistingThing(thingUID) == null) {

            int deviceId = 0;

            // if ute => send response if needed
            if (msg.getRORG() == RORG.UTE && (msg.getPayload(1, 1)[0] & UTEResponse.ResponseNeeded_MASK) == 0) {

                // get new sender Id
                deviceId = bridgeHandler.getNextDeviceId(Helper.bytesToHexString(id));
                if (deviceId > 0) {
                    int[] newSenderId = bridgeHandler.getBaseId();
                    newSenderId[3] += deviceId;

                    // send response
                    EEP response = EEPFactory.buildResponseEEPFromTeachInERP1(msg, newSenderId);
                    bridgeHandler.sendMessage(response.getERP1Message(), null);
                    logger.info("Send teach in response for {}", Helper.bytesToHexString(id));
                }

            }
            DiscoveryResultBuilder discoveryResultBuilder = DiscoveryResultBuilder.create(thingUID)
                    .withBridge(bridgeHandler.getThing().getUID());

            eep.addConfigPropertiesTo(discoveryResultBuilder);

            if (deviceId > 0) {
                // advance config with new device id
                discoveryResultBuilder.withProperty(PARAMETER_SENDERIDOFFSET, deviceId);
            }

            thingDiscovered(discoveryResultBuilder.build());

            // As we only support sensors to be teached in, we do not need to send a teach in response => 4bs
            // bidirectional teach in proc is not supported yet
            // this is true except for UTE teach in => we always have to send a response here

        } else {
            logger.debug("Ignoring already known Enocean thing {}", thingUID);
        }
    }

    @Override
    public long getSenderIdToListenTo() {
        // we just want teach in msg, so return zero here
        return 0;
    }

    @Override
    public void setDiscoveryServiceCallback(DiscoveryServiceCallback discoveryServiceCallback) {
        this.discoveryServiceCallback = discoveryServiceCallback;
    }
}
