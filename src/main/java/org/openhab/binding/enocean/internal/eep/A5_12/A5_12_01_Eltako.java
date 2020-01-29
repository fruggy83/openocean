/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.enocean.internal.eep.A5_12;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.core.util.HexUtils;
import org.openhab.binding.enocean.internal.eep.EEPHelper;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

import java.util.function.Function;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.CHANNEL_INSTANTPOWER;
import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.CHANNEL_TOTALUSAGE;
import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.CHANNEL_INSTANTPOWER_ALTERNATETARIFF;
import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.CHANNEL_TOTALUSAGE_ALTERNATETARIFF;

/**
 *
 * @author Tobias Boesch - Initial contribution
 */
public class A5_12_01_Eltako extends A5_12 {

    public A5_12_01_Eltako(ERP1Message packet) {
        super(packet);
    }

    protected State getPowerValue(boolean secondaryTariff) {
        byte db0 = getDB_0();

        if (((db0 == 0x1C) && secondaryTariff) || ((db0 == 0x0C) && !secondaryTariff)) {
            float currentValue = Long.parseLong(HexUtils.bytesToHex(new byte[] { getDB_3(), getDB_2(), getDB_1() }), 16);
            return new QuantityType<>(currentValue, SmartHomeUnits.WATT);
        }

        return UnDefType.UNDEF;
    }

    protected State getWorkValue(boolean secondaryTariff) {
        byte db0 = getDB_0();

        if (((db0 == 0x19) && secondaryTariff) || ((db0 == 0x09) && !secondaryTariff)) {
            float currentValue = Long.parseLong(HexUtils.bytesToHex(new byte[] { getDB_3(), getDB_2(), getDB_1() }), 16)
                            * (float) 0.1;
            return new QuantityType<>(currentValue, SmartHomeUnits.KILOWATT_HOUR);
        }

        return UnDefType.UNDEF;
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId, Function<String, State> getCurrentStateFunc,
                                       Configuration config) {
        switch (channelId) {
            case CHANNEL_INSTANTPOWER:
                return getPowerValue(false);
            case CHANNEL_TOTALUSAGE:
                State value = getWorkValue(false);
                State currentState = getCurrentStateFunc.apply(channelId);
                return EEPHelper.validateTotalUsage(value, currentState, config);
            case CHANNEL_INSTANTPOWER_ALTERNATETARIFF:
                return getPowerValue(true);
            case CHANNEL_TOTALUSAGE_ALTERNATETARIFF:
                return getWorkValue(true);
        }

        return UnDefType.UNDEF;
    }
}
