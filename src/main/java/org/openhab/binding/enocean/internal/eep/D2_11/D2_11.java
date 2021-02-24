/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.enocean.internal.eep.D2_11;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.function.Function;

import org.openhab.binding.enocean.internal.eep.Base._VLDMessage;
import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class D2_11 extends _VLDMessage {

    protected static final byte FANSPEEDSTAGE_NOTAVAILABLE = 7;
    protected static final byte OCCUPANCY_NOTAVAILABLE = 0;

    protected enum MID {
        UNKNOWN((byte) -1),
        ID0((byte) 0), // sensor => gateway, after press
        ID1((byte) 1), // gateway => sensor, replay
        ID2((byte) 2); // sensor => gateway, transmits current data

        private byte value;

        MID(byte value) {
            this.value = value;
        }

        static MID getMID(byte value) {
            for (MID t : MID.values()) {
                if (t.value == value) {
                    return t;
                }
            }
            return MID.UNKNOWN;
        }
    }

    public D2_11() {
        super();
    }

    public D2_11(ERP1Message packet) {
        super(packet);
    }

    protected MID getMessageType() {
        byte mid = (byte) (bytes[0] & 0b1111);
        return MID.getMID(mid);
    }

    protected State getSetpointType() {
        if (getMessageType() != MID.ID1 && bytes.length > 0) {
            return OnOffType.from(getBit(bytes[0], 7));
        }

        return UnDefType.UNDEF;
    }

    protected State getTemperatureData() {
        if (getMessageType() == MID.ID2 && bytes.length > 1) {
            return new QuantityType<>((((bytes[1] & 0xFF) * 40.0) / 255.0), SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected State getTemperatureCorrection() {
        if (getMessageType() == MID.ID2 && bytes.length > 3) {
            int range = Integer.parseInt(getValidTemperatureCorrection().toString());
            double min = range * -1;
            double max = range;

            // round to half
            double result = min + (((double) (bytes[3] & 0xFF)) * (max - min) / 255.0);
            return new QuantityType<>(result, SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected byte getTemperatureCorrectionOverride(State validTempCorr, State tempCorr) {
        if (validTempCorr != UnDefType.UNDEF && tempCorr != UnDefType.UNDEF && tempCorr instanceof QuantityType<?>
                && validTempCorr instanceof DecimalType) {
            int range = ((DecimalType) validTempCorr).intValue();
            double min = range * -1;
            double max = range;

            double current = Math.min(max, Math.max(min, ((QuantityType<?>) tempCorr).doubleValue()));

            return (byte) (((int) ((current - min) * 255 / (max - min))) & 0xFF);
        }

        return 0;
    }

    protected State getTemperatureSetpoint() {
        if (getMessageType() == MID.ID2 && bytes.length > 4) {
            return new QuantityType<>(bytes[4] & 0xFF, SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected State getValidTemperatureCorrection() {
        if (getMessageType() == MID.ID2 && bytes.length > 5) {
            return new DecimalType((bytes[5] & 0xF0) >>> 4);
        }

        return UnDefType.UNDEF;
    }

    protected State getFanSpeed() {
        if (getMessageType() == MID.ID2 && bytes.length > 5) {
            // subtract 1 as AUTO mode is modeled as -1 not 0
            return new DecimalType(((bytes[5] & 0x0F) >> 1) - 1);
        }

        return UnDefType.UNDEF;
    }

    protected State getOccupancy() {
        if (getMessageType() == MID.ID2 && bytes.length > 5) {
            return OnOffType.from(getBit(bytes[5], 7));
        }

        return UnDefType.UNDEF;
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId,
            Function<String, State> getCurrentStateFunc, Configuration config) {

        switch (channelId) {
            case CHANNEL_SETPOINTTYPE:
                return getSetpointType();
            case CHANNEL_TEMPERATURE:
                return getTemperatureData();
            case CHANNEL_TEMPERATURECORRECTION:
                return getTemperatureCorrection();
            case CHANNEL_TEMPERATURE_SETPOINT:
                return getTemperatureSetpoint();
            case CHANNEL_VALIDTEMPERATURECORRECTION:
                return getValidTemperatureCorrection();
            case CHANNEL_FANSPEEDSTAGE:
                return getFanSpeed();
            case CHANNEL_OCCUPANCYOVERWRITABLE:
                return getOccupancy();
        }

        return UnDefType.UNDEF;
    }

    protected byte getFanSpeedOverride(State currentState) {
        if (currentState instanceof StringType) {
            return (byte) (((StringType) currentState).as(DecimalType.class).byteValue() + 1);
        }

        return FANSPEEDSTAGE_NOTAVAILABLE;
    }

    protected abstract byte getOccupancyOverride(State currentState);

    @Override
    protected void convertFromCommandImpl(String channelId, String channelTypeId, Command command,
            Function<String, State> getCurrentStateFunc, Configuration config) {

        if (VIRTUALCHANNEL_SEND_COMMAND.equals(channelId)) {
            byte db_3 = MID.ID1.value;
            if (getCurrentStateFunc.apply(CHANNEL_HEATINGSTATE) == OnOffType.ON) {
                db_3 += 0b01000000;
            }
            if (getCurrentStateFunc.apply(CHANNEL_COOLINGSTATE) == OnOffType.ON) {
                db_3 += 0b00100000;
            }
            if (getCurrentStateFunc.apply(CHANNEL_WINDOWSTATE) == OnOffType.ON) {
                db_3 += 0b00010000;
            }

            byte db_0 = 0;
            byte db_2 = 0;
            byte db_1 = 0;

            State validTempCorr = getCurrentStateFunc.apply(CHANNEL_VALIDTEMPERATURECORRECTION);
            State tempCorr = getCurrentStateFunc.apply(CHANNEL_TEMPERATURECORRECTION);
            State currentBaseSetpoint = getCurrentStateFunc.apply(CHANNEL_TEMPERATURE_SETPOINT);
            if (validTempCorr != UnDefType.UNDEF && validTempCorr instanceof DecimalType) {
                db_0 = (byte) (((DecimalType) validTempCorr).byteValue() << 4);
                db_2 = getTemperatureCorrectionOverride(validTempCorr, tempCorr);
            }
            if (currentBaseSetpoint != UnDefType.UNDEF && currentBaseSetpoint instanceof QuantityType<?>) {
                byte baseSetPoint = ((QuantityType<?>) currentBaseSetpoint).byteValue();
                db_1 = baseSetPoint;
            }

            db_0 += (getFanSpeedOverride(getCurrentStateFunc.apply(CHANNEL_FANSPEEDSTAGE)) << 1);
            db_0 += getOccupancyOverride(getCurrentStateFunc.apply(CHANNEL_OCCUPANCYOVERWRITABLE));

            setData(db_3, db_2, db_1, db_0);
        }
    }
}
