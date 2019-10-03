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
package org.openhab.binding.enocean.internal.eep.A5_14;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.function.Function;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.enocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Dominik Krickl-Vorreiter - Initial contribution
 */
public class A5_14_01_ELTAKO extends _4BSMessage {

    public A5_14_01_ELTAKO(ERP1Message packet) {
        super(packet);
    }

    private State getEnergyStorage() {
        int db3 = getDB_3Value();

        double voltage = db3 / 51.0; // 0..255 = 0.0..5.0V

        return new QuantityType<>(voltage, SmartHomeUnits.VOLT);
    }

    private State getBatteryVoltage() {
        int db2 = getDB_2Value();

        double voltage = db2 / 51.0; // 0..255 = 0.0..5.0V

        return new QuantityType<>(voltage, SmartHomeUnits.VOLT);
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, Function<String, State> getCurrentStateFunc,
            Configuration config) {
        switch (channelId) {
            case CHANNEL_ENERGY_STORAGE:
                return getEnergyStorage();
            case CHANNEL_BATTERY_VOLTAGE:
                return getBatteryVoltage();
        }

        return UnDefType.UNDEF;
    }
}
