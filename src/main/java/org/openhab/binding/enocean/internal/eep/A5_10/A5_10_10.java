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

import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_10_10 extends A5_10 {

    protected final double tempFactor = 40.0 / 250.0;

    public A5_10_10(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected int getSetPointValue() {
        return getDB_3Value();
    }

    @Override
    protected State getTemperature() {
        double temp = (getDB_1Value()) * tempFactor;
        return new QuantityType<>(temp, SIUnits.CELSIUS);
    }
}
