package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.SENDERID;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;
import org.openhab.binding.openocean.config.OpenOceanBaseConfig;
import org.openhab.binding.openocean.internal.OpenOceanConfigStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OpenOceanBaseThingHandler extends ConfigStatusThingHandler {

    private OpenOceanBridgeHandler gateway = null;
    private Logger logger = LoggerFactory.getLogger(OpenOceanBaseThingHandler.class);

    protected int[] thingId;

    public OpenOceanBaseThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing open ocean base thing handler.");
        initializeThing((getBridge() == null) ? null : getBridge().getStatus());
    }

    private void initializeThing(ThingStatus bridgeStatus) {
        logger.debug("initializeThing thing {} bridge status {}", getThing().getUID(), bridgeStatus);

        if (getBridgeHandler() != null) {
            if (bridgeStatus == ThingStatus.ONLINE) {
                if (validateConfig() && prepareThing()) {
                    updateStatus(ThingStatus.ONLINE);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                }
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    private boolean validateConfig() {
        final OpenOceanBaseConfig config = getConfigAs(OpenOceanBaseConfig.class);

        if (config.senderId == null || config.senderId.isEmpty()) {
            return false;
        } else {
            try {
                Integer.parseUnsignedInt(config.senderId, 16);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    abstract boolean prepareThing();

    protected synchronized OpenOceanBridgeHandler getBridgeHandler() {
        if (this.gateway == null) {
            Bridge bridge = getBridge();
            if (bridge == null) {
                return null;
            }
            ThingHandler handler = bridge.getHandler();
            if (handler instanceof OpenOceanBridgeHandler) {
                this.gateway = (OpenOceanBridgeHandler) handler;
            } else {
                return null;
            }
        }
        return this.gateway;
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        Collection<ConfigStatusMessage> configStatusMessages;

        // The senderId must be provided
        final OpenOceanActuatorConfig config = getConfigAs(OpenOceanActuatorConfig.class);
        final String senderId = config.senderId;
        if (senderId == null || senderId.isEmpty()) {
            configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(SENDERID)
                    .withMessageKeySuffix(OpenOceanConfigStatusMessage.SENDERID_MISSING.getMessageKey())
                    .withArguments(SENDERID).build());
        } else {
            try {
                Integer.parseUnsignedInt(senderId, 16);
            } catch (Exception e) {
                configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(SENDERID)
                        .withMessageKeySuffix(OpenOceanConfigStatusMessage.SENDERID_MALFORMED.getMessageKey())
                        .withArguments(SENDERID).build());
            }
            configStatusMessages = Collections.emptyList();
        }

        return configStatusMessages;
    }
}
