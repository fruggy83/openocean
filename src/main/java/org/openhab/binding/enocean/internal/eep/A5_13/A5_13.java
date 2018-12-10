/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep.A5_13;

import org.openhab.binding.enocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class A5_13 extends _4BSMessage {
    public A5_13(ERP1Message packet) {
        super(packet);
    }

    protected final int PARTONE = 0x10;
    protected final int PARTTWO = 0x20;

    protected int getMessageIdentifier() {
        return getDB_0Value() & 0xF0;
    }

    protected boolean isPartOne() {
        return getMessageIdentifier() == PARTONE;
    }

    protected boolean isPartTwo() {
        return getMessageIdentifier() == PARTTWO;
    }
}