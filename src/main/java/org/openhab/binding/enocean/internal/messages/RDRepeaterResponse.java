/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.messages;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.StringType;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class RDRepeaterResponse extends Response {

    protected String repeaterLevel;

    public RDRepeaterResponse(Response response) {
        this(response.getPayload().length, 0, response.getPayload());
    }

    RDRepeaterResponse(int dataLength, int optionalDataLength, byte[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (payload == null || payload.length < 3) {
            return;
        }

        if (payload[1] == 0) {
            repeaterLevel = REPEATERMODE_OFF;
        } else if (payload[1] == 1 || payload[1] == 2) {
            switch (payload[2]) {
                case 1:
                    repeaterLevel = REPEATERMODE_LEVEL_1;
                    break;
                case 2:
                    repeaterLevel = REPEATERMODE_LEVEL_2;
                    break;
                case 0:
                    repeaterLevel = REPEATERMODE_OFF;
                    break;
                default:
                    return;
            }

            _isValid = true;
        }
    }

    @NonNull
    public StringType getRepeaterLevel() {
        return StringType.valueOf(repeaterLevel);
    }
}
