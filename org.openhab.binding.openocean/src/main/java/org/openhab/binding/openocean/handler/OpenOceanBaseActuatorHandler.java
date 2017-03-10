package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;
import org.openhab.binding.openocean.transceiver.Helper;

public abstract class OpenOceanBaseActuatorHandler extends OpenOceanBaseThingHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_SWITCHINGACTUATOR);

    protected String enoceanId;

    public OpenOceanBaseActuatorHandler(Thing thing) {
        super(thing);
    }

    boolean validateSenderId(String senderId) {
        if (senderId == null || senderId.isEmpty()) {
            return true;
        }

        try {
            long s = Long.parseLong(senderId, 16);
            long b = Long.parseLong(Helper.bytesToHexString(getBridgeHandler().getBaseId()), 16);

            return (s - b) > 0 && (s - b) < 128;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    boolean validateConfig() {
        enoceanId = thing.getUID().getId();
        OpenOceanActuatorConfig config = thing.getConfiguration().as(OpenOceanActuatorConfig.class);
        return validateEnoceanId(enoceanId) && validateSenderId(config.senderId);
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
                if (senderId.isEmpty()) {
                    return false;
                }
                config.put(SENDERID, senderId);
                updateConfiguration(config);
                sendingId = Helper.hexStringToBytes(cfg.senderId);
                return true;
            }
        } else if (validateEnoceanId(senderId)) {
            sendingId = Helper.hexStringToBytes(cfg.senderId);
            return true;
        }

        return false;
    }

    @Override
    public long getSenderIdToListenTo() {
        return Long.parseLong(enoceanId, 16);
    }

}
