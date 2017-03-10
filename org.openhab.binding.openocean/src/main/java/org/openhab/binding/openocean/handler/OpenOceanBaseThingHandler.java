package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.openocean.config.OpenOceanActuatorConfig;
import org.openhab.binding.openocean.internal.OpenOceanConfigStatusMessage;
import org.openhab.binding.openocean.transceiver.ESP3PacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OpenOceanBaseThingHandler extends ConfigStatusThingHandler implements ESP3PacketListener {

    private OpenOceanBridgeHandler gateway = null;
    private Logger logger = LoggerFactory.getLogger(OpenOceanBaseThingHandler.class);

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_SWITCHINGACTUATOR);

    protected int[] sendingId;

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
                if (validateConfig() && initializeIdForSending()) {
                    updateStatus(ThingStatus.ONLINE);
                    getBridgeHandler().addPacketListener(this);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                }
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, "A bridge is required");
        }
    }

    protected boolean validateEnoceanId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        } else {

            if (id.length() != 8) {
                return false;
            }

            try {
                Integer.parseUnsignedInt(id, 16);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    abstract boolean validateConfig();

    abstract boolean initializeIdForSending();

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

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            initializeThing(bridgeStatusInfo.getStatus());
        }
    }
}
