/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.Base;

import org.eclipse.smarthome.config.core.Configuration;
import org.openhab.binding.openocean.internal.config.OpenOceanChannelTeachInConfig;
import org.openhab.binding.openocean.internal.eep.EEP;
import org.openhab.binding.openocean.internal.eep.EEPType;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class _4BSMessage extends EEP {

    public _4BSMessage(ERP1Message packet) {
        super(packet);
    }

    public _4BSMessage() {
        super();
    }

    public static final int TeachInBit = 0x08;
    public static final int LRN_Type_Mask = 0x80;

    public int getDB_0() {
        return bytes[3];
    }

    public int getDB_1() {
        return bytes[2];
    }

    public int getDB_2() {
        return bytes[1];
    }

    public int getDB_3() {
        return bytes[0];
    }

    @Override
    protected void teachInQueryImpl(Configuration config) {

        if (config == null) {
            return;
        }

        OpenOceanChannelTeachInConfig c = config.as(OpenOceanChannelTeachInConfig.class);
        if (c.teachInMSG == null || c.teachInMSG.isEmpty()) {

            EEPType type = getEEPType();

            int db3 = (getEEPType().getFunc() << 2) + ((type.getType()) >> 5);
            int db2 = ((type.getType() << 3) & 255);
            int db1 = 0;

            try {
                int manufId = (Integer.parseInt(c.manufacturerId, 16) & 0x7ff); // => 11 bit
                db2 += (manufId >> 8);
                db1 += (manufId & 255);
            } catch (Exception e) {

            }

            setData(db3, db2, db1, LRN_Type_Mask);

        } else {
            try {
                int[] msg = Helper.hexStringTo4Bytes(c.teachInMSG);
                setData(msg);
            } catch (Exception e) {
            }
        }

    }
}
