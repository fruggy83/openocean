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
package org.openhab.binding.enocean.internal.eep.A5_20;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.function.Function;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 * Battery powered actuator (BI-DIR)
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_20_01 extends A5_20 {

    public A5_20_01() {
        super();
    }

    public A5_20_01(ERP1Message packet) {
        super(packet);
    }

    private byte getValvePosition(Function<String, State> getCurrentStateFunc) {
        State current = getCurrentStateFunc.apply(CHANNEL_VALVE_POSITION);

        if ((current != null) && (current instanceof DecimalType)) {
            DecimalType state = current.as(DecimalType.class);

            if (state != null) {
                return state.byteValue();
            }
        }

        // default to 25 %
        return 25; 
    }

    @Override
    protected void convertFromCommandImpl(String channelId, String channelTypeId, Command command,
            Function<String, State> getCurrentStateFunc, Configuration config) {

        if (CHANNEL_SEND_COMMAND.equals(channelId)) {
            byte db3 = getValvePosition(getCurrentStateFunc);
            byte db2 = 0b0; // Temperature from RCU
            byte db1 = 0; // DB1.2 is set to 0 => actuator uses given valve position            
            byte db0 = (byte) (0x00 | TeachInBit);

            setData(db3, db2, db1, db0);

            return;
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, Function<String, State> getCurrentStateFunc,
            Configuration config) {

        switch (channelId) {
            case CHANNEL_VALVE_POSITION:
                return getValvePosition();            
            case CHANNEL_TEMPERATURE:
                return getTemperature();
            case CHANNEL_LOW_BATTERY:
                return getBatteryLow();
            case CHANNEL_CONTACT:            
                return getWindowState();
        }

        return UnDefType.UNDEF;
    }

    private State getValvePosition() {
        return new QuantityType<>(getDB_3Value(), SmartHomeUnits.PERCENT);
    }

    private State getTemperature() {
        double value = getDB_1Value() * (40.0 / 255.0);

        return new QuantityType<>(value, SIUnits.CELSIUS);
    }

    private State getBatteryLow() {
        return getBit(getDB_2Value(), 4) ? OnOffType.OFF : OnOffType.ON;    // docs state 0 => true (battery low)
    }

    private State getWindowState() {
        return getBit(getDB_2Value(), 1) ? OpenClosedType.OPEN : OpenClosedType.CLOSED;
    }
}
