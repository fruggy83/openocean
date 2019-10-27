/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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

import javax.measure.quantity.Temperature;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.eep.Base._VLDMessage;
import org.openhab.binding.enocean.internal.messages.ERP1Message;


/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class D2_11 extends _VLDMessage {

    protected final byte FANSPEEDSTAGE_NOTAVAILABLE = 7;
    protected final byte OCCUPANCY_NOTAVAILABLE = 0;

    protected enum MID {
        UNKNOWN((byte)-1),
        ID0((byte)0),           // sensor => gateway, after press
        ID1((byte)1),           // sensor => gateway, transmits current data
        ID2((byte)2);           // gateway => sensor

        private byte value;

        MID(byte value){
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

    protected State getSetpointType() {
        if(getMessageType() != MID.ID1 && bytes.length > 0) {
            return new DecimalType(getBit(bytes[0], 7) ? 1 : 0);
        }

        return UnDefType.UNDEF;
    }

    protected State getTemperatureData() {
        if(getMessageType() == MID.ID2 && bytes.length > 1) {
            return new QuantityType<>((((bytes[1] & 0xFF) * 40.0) / 255.0), SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected State getTemperatureCorrection() {
        if(getMessageType() == MID.ID2 && bytes.length > 3) {
            DecimalType range = (DecimalType)getValidTemperatureCorrection();
            double min = range.intValue() * -1;
            double max = range.intValue();

            // round to half
            double result = min + (((double)(bytes[3] & 0xFF)) * (max - min) / 255.0);
            return new QuantityType<>(result, SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected byte getTemperatureCorrectionOverride(State validTempCorr, State tempCorr) {
        if(validTempCorr != UnDefType.UNDEF && tempCorr != UnDefType.UNDEF && (tempCorr instanceof QuantityType<?>)) {
            DecimalType range = (DecimalType)validTempCorr;
            double min = range.intValue() * -1;
            double max = range.intValue();
            @SuppressWarnings("unchecked")
            double current = Math.min(max, Math.max(min, ((QuantityType<Temperature>)tempCorr).doubleValue()));

            return (byte)(((int)((current - min) * 255 / (max - min))) & 0xFF);
        }

        return 0;
    }

    protected State getTemperatureSetpoint() {
        if(getMessageType() == MID.ID2 && bytes.length > 4) {
            return new QuantityType<>(bytes[4] & 0xFF, SIUnits.CELSIUS);
        }

        return UnDefType.UNDEF;
    }

    protected State getValidTemperatureCorrection() {
        if(getMessageType() == MID.ID2 && bytes.length > 5) {
            return new DecimalType((bytes[5] & 0xF0) >> 4);
        }

        return UnDefType.UNDEF;
    }

    protected State getFanSpeed() {
        if(getMessageType() == MID.ID2 && bytes.length > 5) {
            return new DecimalType((bytes[5] & 0x0F) >> 1);
        }

        return UnDefType.UNDEF;
    }

    protected State getOccupancy() {
        if(getMessageType() == MID.ID2 && bytes.length > 5) {
            return (getBit(bytes[5], 7)) ? OnOffType.ON : OnOffType.OFF;
        }

        return UnDefType.UNDEF;
    }

    protected MID getMessageType() {
        byte mid = (byte)(bytes[0] & 0b1111);
        return MID.getMID(mid);
        
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, Function<String, State> getCurrentStateFunc,
            Configuration config) {

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

    protected abstract byte getFanSpeedOverride(State currentState);
    protected abstract byte getOccupancyOverride(State currentState);

    @Override
    protected void convertFromCommandImpl(String channelId, String channelTypeId, Command command,
            Function<String, State> getCurrentStateFunc, Configuration config) {

        if (channelId.equals(CHANNEL_SEND_COMMAND)) {
            byte db_3 = MID.ID1.value;
            if(getCurrentStateFunc.apply(CHANNEL_HEATINGSTATE) == OnOffType.ON) {
                db_3 += 0b01000000;
            }
            if(getCurrentStateFunc.apply(CHANNEL_COOLINGSTATE) == OnOffType.ON) {
                db_3 += 0b00100000;
            }
            if(getCurrentStateFunc.apply(CHANNEL_WINDOWSTATE) == OnOffType.ON) {
                db_3 += 0b00010000;
            }
            
            byte db_0 = 0;
            byte db_2 = 0;
            byte db_1 = 0;
            State validTempCorr = getCurrentStateFunc.apply(CHANNEL_VALIDTEMPERATURECORRECTION);
            State tempCorr = getCurrentStateFunc.apply(CHANNEL_TEMPERATURECORRECTION);
            State currentBaseSetpoint = getCurrentStateFunc.apply(CHANNEL_TEMPERATURE_SETPOINT);
            if(validTempCorr != UnDefType.UNDEF) {
                db_0 = (byte)(((DecimalType)validTempCorr).byteValue() << 4);
                db_2 = getTemperatureCorrectionOverride(validTempCorr, tempCorr);
            }
            if(currentBaseSetpoint != UnDefType.UNDEF && currentBaseSetpoint instanceof QuantityType<?>) {                
                @SuppressWarnings("unchecked")
                byte baseSetPoint = ((QuantityType<Temperature>)currentBaseSetpoint).byteValue();
                db_1 = baseSetPoint;
            }

            db_0 += (getFanSpeedOverride(getCurrentStateFunc.apply(CHANNEL_FANSPEEDSTAGE)) << 1);
            db_0 += getOccupancyOverride(getCurrentStateFunc.apply(CHANNEL_OCCUPANCYOVERWRITABLE));
            
            setData(db_3, db_2, db_1, db_0);
        }
    }
}


