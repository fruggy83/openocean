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
package org.openhab.binding.enocean.internal.eep.A5_13;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.function.Function;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 * Convertes EEP A5-13 messages of type 0x01 into Temperature, Illumination, Wind Speed and Temperature states
 * 
 * @author Daniel Weber - Initial contribution
 */
public class A5_13_01 extends A5_13 {

    public A5_13_01(ERP1Message packet) {
        super(packet);
    }

    protected State getIllumination() {
        return new QuantityType<>(((getDB_3Value() * 1000.0) / 255.0), SmartHomeUnits.LUX);
    }

    protected State getIllumination(double value) {
        return new QuantityType<>(((value * 1000.0 * 150.0) / 255.0), SmartHomeUnits.LUX);
    }

    protected State getIlluminationWest() {
        return getIllumination(getDB_3Value());
    }

    protected State getIlluminationSouthNorth() {
        return getIllumination(getDB_2Value());
    }

    protected State getIlluminationEast() {
        return getIllumination(getDB_1Value());
    }

    protected State getTemperature() {
        return new QuantityType<>(-40.0 + ((getDB_2Value() * 120.0) / 255.0), SIUnits.CELSIUS);
    }

    protected State getWindSpeed() {
        return new QuantityType<>(((getDB_1Value() * 70.0) / 255.0), SmartHomeUnits.METRE_PER_SECOND);
    }

    protected State getRainStatus() {
        return getBit(getDB_0Value(), 1) ? OnOffType.ON : OnOffType.OFF;
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, Function<String, State> getCurrentStateFunc,
            Configuration config) {

        if (isPartOne()) {
            switch (channelId) {
                case CHANNEL_ILLUMINATION:
                    return getIllumination();
                case CHANNEL_TEMPERATURE:
                    return getTemperature();
                case CHANNEL_WINDSPEED:
                    return getWindSpeed();
                case CHANNEL_RAINSTATUS:
                    return getRainStatus();
            }
        }

        if (isPartTwo()) {
            switch (channelId) {
                case CHANNEL_ILLUMINATIONWEST:
                    return getIlluminationWest();
                case CHANNEL_ILLUMINATIONSOUTHNORTH:
                    return getIlluminationSouthNorth();
                case CHANNEL_ILLUMINATIONEAST:
                    return getIlluminationEast();
            }
        }

        return UnDefType.UNDEF;
    }
}
