/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.D2_01;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.openocean.internal.eep.Base._VLDMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class D2_01 extends _VLDMessage {

    protected int cmdMask = 0x0f;
    protected int outputValueMask = 0x7f;

    protected int CMD_ACTUATOR_SET_STATUS = 0x01;
    protected int CMD_ACTUATOR_STATUS_QUERY = 0x03;
    protected int CMD_ACTUATOR_STATUS_RESPONE = 0x04;
    protected int CMD_ACTUATOR_MEASUREMENT_QUERY = 0x06;
    protected int CMD_ACTUATOR_MEASUREMENT_RESPONE = 0x07;

    public D2_01() {
        super();
    }

    public D2_01(ERP1Message packet) {
        super(packet);
    }

    protected int getCMD() {
        return bytes[0] & cmdMask;
    }

    protected void setSwitchingData(OnOffType command, int outputChannel) {
        if (command == OnOffType.ON) {
            setData(CMD_ACTUATOR_SET_STATUS, outputChannel, 0x01);
        } else {
            setData(CMD_ACTUATOR_SET_STATUS, outputChannel, 0x00);
        }
    }

    protected void setSwitchingQueryData(int outputChannel) {
        setData(CMD_ACTUATOR_STATUS_QUERY, outputChannel);
    }

    protected State getSwitchingData() {
        if (getCMD() == CMD_ACTUATOR_STATUS_RESPONE) {
            return (bytes[bytes.length - 1] & outputValueMask) > 0 ? OnOffType.ON : OnOffType.OFF;
        }

        return UnDefType.UNDEF;
    }

    protected void setEnergyMeasurementQueryData(int outputChannel) {
        setData(CMD_ACTUATOR_MEASUREMENT_QUERY, outputChannel);
    }

    protected void setPowerMeasurementQueryData(int outputChannel) {
        setData(CMD_ACTUATOR_MEASUREMENT_QUERY, 0x20 + outputChannel);
    }

    protected State getEnergyMeasurementData() {
        if (getCMD() == CMD_ACTUATOR_MEASUREMENT_RESPONE) {
            float factor = 1;

            switch (bytes[1] >> 5) {
                case 0:
                    factor /= 3600.0;
                    break;
                case 1:
                    factor /= 1000;
                    break;
                case 2:
                    factor = 1;
                    break;
                default:
                    return UnDefType.UNDEF;
            }

            return new DecimalType(
                    Long.parseLong(Helper.bytesToHexString(bytes[2], bytes[3], bytes[4], bytes[5]), 16) * factor);
        }

        return UnDefType.UNDEF;
    }

    protected State getPowerMeasurementData() {
        if (getCMD() == CMD_ACTUATOR_MEASUREMENT_RESPONE) {
            float factor = 1;

            switch (bytes[1] >> 5) {
                case 3:
                    factor = 1;
                    break;
                case 4:
                    factor /= 1000;
                    break;
                default:
                    return UnDefType.UNDEF;
            }

            return new DecimalType(
                    Long.parseLong(Helper.bytesToHexString(bytes[2], bytes[3], bytes[4], bytes[5]), 16) * factor);
        }

        return UnDefType.UNDEF;
    }

    @Override
    public void addConfigPropertiesTo(DiscoveryResultBuilder discoveredThingResultBuilder) {
        discoveredThingResultBuilder.withProperty(PARAMETER_EEPID, getEEPType().getId());
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {
        if (!getEEPType().GetChannelIds().contains(channelId)) {
            return;
        }

        if (channelId.equals(CHANNEL_GENERAL_SWITCHING)) {
            if (command == RefreshType.REFRESH) {
                setSwitchingQueryData(0x1e);
            } else {
                setSwitchingData((OnOffType) command, 0x1e);
            }
        } else if (channelId.equals(CHANNEL_INSTANTPOWER) && command == RefreshType.REFRESH) {
            setPowerMeasurementQueryData(0x1e);
        } else if (channelId.equals(CHANNEL_TOTALUSAGE) && command == RefreshType.REFRESH) {
            setEnergyMeasurementQueryData(0x1e);
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, State currentState, Configuration config) {

        switch (channelId) {
            case CHANNEL_GENERAL_SWITCHING:
                return getSwitchingData();
            case CHANNEL_INSTANTPOWER:
                return getPowerMeasurementData();
            case CHANNEL_TOTALUSAGE:
                return getEnergyMeasurementData();
        }

        return UnDefType.UNDEF;
    }

}
