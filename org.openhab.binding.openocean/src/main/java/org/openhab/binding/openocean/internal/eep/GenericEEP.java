/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.library.types.RewindFastforwardType;
import org.eclipse.smarthome.core.library.types.StringListType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.transform.actions.Transformation;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.config.OpenOceanChannelTransformationConfig;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class GenericEEP extends EEP {

    final Set<String> supportedChannelIds = Collections.unmodifiableSet(new HashSet<String>() {

        private static final long serialVersionUID = 1L;

        {
            add(CHANNEL_GENERIC_LIGHT_SWITCHING);
            add(CHANNEL_GENERIC_GENERAL_SWITCHING);
            add(CHANNEL_GENERIC_DIMMER);
            add(CHANNEL_GENERIC_ROLLERSHUTTER);
            add(CHANNEL_TEMPERATURE);
            add(CHANNEL_SETPOINT);
        }
    });

    final List<Class<? extends State>> supportedStates = Collections
            .unmodifiableList(new LinkedList<Class<? extends State>>() {

                private static final long serialVersionUID = 1L;

                {
                    add(DateTimeType.class);
                    add(DecimalType.class);
                    add(HSBType.class);
                    add(OnOffType.class);
                    add(OpenClosedType.class);
                    add(PercentType.class);
                    add(PlayPauseType.class);
                    add(PointType.class);
                    add(RewindFastforwardType.class);
                    add(StringListType.class);
                    add(StringType.class);
                    add(UpDownType.class);
                }
            });

    EEPType eepType = null;

    public GenericEEP() {
        super();
    }

    public GenericEEP(ERP1Message packet) {

        switch (packet.getRORG()) {
            case _1BS:
                eepType = EEPType.Generic1BS;
            case _4BS:
                eepType = EEPType.Generic4BS;
            case RPS:
                break;
            case Unknown:
                break;
            case VLD:
                break;
            default:
                break;
        }

        setData(packet.getPayload(RORGLength, packet.getRORG().getDataLength()));
        setSenderId(packet.getPayload(RORGLength + packet.getRORG().getDataLength(), SenderIdLength));
        setStatus(packet.getPayload(RORGLength + packet.getRORG().getDataLength() + SenderIdLength, 1)[0]);
    }

    @Override
    public Set<String> getSupportedChannels() {
        return supportedChannelIds;
    }

    @Override
    protected EEPType getEEPType() {
        return eepType;
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {
        if (config != null) {
            OpenOceanChannelTransformationConfig transformationInfo = config
                    .as(OpenOceanChannelTransformationConfig.class);
            String c = Transformation.transform(transformationInfo.transformationType,
                    transformationInfo.transformationFuntion, command.toString());

            if (c != command.toString()) {
                try {
                    setData(Helper.hexStringToBytes(c));
                } catch (Exception e) {
                    logger.debug("Command {} could not transformed", command.toString());
                }
            }
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {
        if (config != null) {

            String payload = Helper.bytesToHexString(getERP1Message().getPayload());
            OpenOceanChannelTransformationConfig transformationInfo = config
                    .as(OpenOceanChannelTransformationConfig.class);
            String c = Transformation.transform(transformationInfo.transformationType,
                    transformationInfo.transformationFuntion, channelId + "|" + payload);

            if (c != null && !c.isEmpty()) {
                String[] parts = c.split("\\|");

                Class<? extends State> state = supportedStates.stream().filter(s -> s.getName() == parts[0]).findFirst()
                        .get();

                if (state != null) {
                    if (state.isEnum()) {

                        for (State s : state.getEnumConstants()) {
                            if (s.toString().equalsIgnoreCase(parts[1])) {
                                return s;
                            }
                        }
                    } else {
                        try {
                            return state.getConstructor(String.class).newInstance(parts[1]);
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                            return UnDefType.UNDEF;
                        }
                    }
                }
            }
        }

        return UnDefType.UNDEF;
    }

}
