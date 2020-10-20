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
package org.openhab.binding.enocean.internal.eep.A5_10;

import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.types.State;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_10_15 extends A5_10 {

    public A5_10_15(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected int getSetPointValue() {
        return getDB_2Value() >>> 2;
    }

    @Override
    protected State getTemperature() {
        int value = ((getDB_2Value() & 0b11) << 8) + getDB_1Value();
        double temp = 41.2 - (value * (41.2 + 10.0) / 1023.0);
        return new QuantityType<>(temp, SIUnits.CELSIUS);
    }
}
