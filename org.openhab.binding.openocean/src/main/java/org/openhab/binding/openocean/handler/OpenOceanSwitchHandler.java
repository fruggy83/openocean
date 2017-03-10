package org.openhab.binding.openocean.handler;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.openocean.eep.EEPFactory;
import org.openhab.binding.openocean.eep.PTM200Message;
import org.openhab.binding.openocean.messages.ESP3Packet;
import org.openhab.binding.openocean.messages._RPSMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenOceanSwitchHandler extends OpenOceanBaseActuatorHandler {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_SWITCHINGACTUATOR);

    private Logger logger = LoggerFactory.getLogger(OpenOceanSwitchHandler.class);

    public OpenOceanSwitchHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (channelUID.getId().equals(SWITCHING)) {
            if (command instanceof OnOffType) {
                getBridgeHandler()
                        .sendMessage(EEPFactory.createA5_38_08_Switching((OnOffType) command, false, sendingId), null);
            }
        }

    }

    @Override
    public void espPacketReceived(ESP3Packet packet) {
        if (packet instanceof _RPSMessage) {
            PTM200Message m = new PTM200Message((_RPSMessage) packet);
            if (m.isValid()) {
                updateState(SWITCHING, m.getOnOff());
            }
        }
    }
}
