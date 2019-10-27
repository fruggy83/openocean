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

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class D2_11_06 extends D2_11 {

    public D2_11_06() {
        super();
    }

    public D2_11_06(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected byte getFanSpeedOverride(State currentState) {
        if(currentState instanceof DecimalType) {
            return ((DecimalType)currentState).byteValue();
        }
        
        return FANSPEEDSTAGE_NOTAVAILABLE;
    }

    @Override
    protected byte getOccupancyOverride(State currentState) {
        if(currentState instanceof OnOffType) {
            return (byte)(((OnOffType)currentState) == OnOffType.ON ? 1 : 0);
        }
        
        return OCCUPANCY_NOTAVAILABLE;
    }
}


