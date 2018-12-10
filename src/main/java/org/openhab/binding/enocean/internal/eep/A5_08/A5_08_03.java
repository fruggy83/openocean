/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep.A5_08;

import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_08_03 extends A5_08 {

    public A5_08_03(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected double getScaledTemperatureMin() {
        return -30;
    }

    @Override
    protected double getScaledTemperatureMax() {
        return 50;
    }

    @Override
    protected double getScaledIlluminationMin() {
        return 0;
    }

    @Override
    protected double getScaledIlluminationMax() {
        return 1530;
    }

}
