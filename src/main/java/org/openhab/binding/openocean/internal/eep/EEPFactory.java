/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep;

import java.lang.reflect.InvocationTargetException;

import org.openhab.binding.openocean.internal.eep.Base.UTEResponse;
import org.openhab.binding.openocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.openocean.internal.eep.D5_00.D5_00_01;
import org.openhab.binding.openocean.internal.eep.F6_02.F6_02_01;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ERP1Message.RORG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class EEPFactory {

    private static final Logger logger = LoggerFactory.getLogger(EEPFactory.class);

    public static EEP createEEP(EEPType eepType) {

        try {
            Class<? extends EEP> cl = eepType.getEEPClass();
            if (cl == null) {
                throw new IllegalArgumentException("Message " + eepType + " not implemented");
            }
            return cl.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EEP buildEEP(EEPType eepType, ERP1Message packet) {
        try {
            Class<? extends EEP> cl = eepType.getEEPClass();
            if (cl == null) {
                throw new IllegalArgumentException("Message " + eepType + " not implemented");
            }
            return cl.getConstructor(ERP1Message.class).newInstance(packet);
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EEP buildEEPFromTeachInERP1(ERP1Message msg) {
        if (!msg.getIsTeachIn()) {
            return null;
        }

        switch (msg.getRORG()) {
            case RPS:
                return new F6_02_01(msg);
            case _1BS:
                return new D5_00_01(msg);
            case _4BS: {
                int db_0 = msg.getPayload()[4];
                if ((db_0 & _4BSMessage.LRN_Type_Mask) == 0) {
                    logger.info("Received 4BS Teach In without EEP");
                    return null;
                }

                int db_3 = msg.getPayload()[1];
                int db_2 = msg.getPayload()[2];

                int func = db_3 >> 2;
                int type = ((db_3 & 3) << 5) + (db_2 >> 3);

                EEPType eepType = EEPType.getType(RORG._4BS, func, type);
                if (eepType == null) {
                    logger.debug("Received unsupported EEP teach in, fallback to generic thing");
                    eepType = EEPType.Generic4BS;
                }

                return buildEEP(eepType, msg);
            }
            case UTE: {
                int[] payload = msg.getPayload();

                int rorg = payload[payload.length - 1 - EEP.StatusLength - EEP.SenderIdLength];
                int func = payload[payload.length - 1 - EEP.StatusLength - EEP.SenderIdLength - EEP.RORGLength];
                int type = payload[payload.length - 1 - EEP.StatusLength - EEP.SenderIdLength - EEP.RORGLength - 1];

                EEPType eepType = EEPType.getType(RORG.getRORG(rorg), func, type);
                if (eepType == null) {
                    logger.info("Received unsupported EEP teach in, fallback to generic thing");
                    RORG r = RORG.getRORG(rorg);
                    if (r == RORG._4BS) {
                        eepType = EEPType.Generic4BS;
                    } else if (r == RORG.VLD) {
                        eepType = EEPType.GenericVLD;
                    } else {
                        return null;
                    }
                }

                return buildEEP(eepType, msg);
            }
            case Unknown:
            case VLD:
                return null;
        }

        return null;
    }

    public static EEP buildResponseEEPFromTeachInERP1(ERP1Message msg, int[] senderId) {
        EEP result = new UTEResponse(msg);
        result.setSenderId(senderId);

        return result;
    }
}
