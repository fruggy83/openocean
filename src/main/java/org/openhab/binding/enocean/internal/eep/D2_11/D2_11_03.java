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

import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.core.types.State;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class D2_11_03 extends D2_11 {

    public D2_11_03() {
        super();
    }

    public D2_11_03(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected byte getOccupancyOverride(State currentState) {
        return OCCUPANCY_NOTAVAILABLE;
    }
}
