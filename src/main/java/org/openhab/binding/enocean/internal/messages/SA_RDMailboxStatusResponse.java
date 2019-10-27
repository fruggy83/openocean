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
package org.openhab.binding.enocean.internal.messages;

import org.openhab.binding.enocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class SA_RDMailboxStatusResponse extends Response {

    public enum MailBoxStatus {
        UNKNOWN((byte) -1),
        EMPTY((byte) 0),
        FULL((byte) 1),
        DOES_NOT_EXISTS((byte) 2);

        byte value;
        MailBoxStatus(byte value) {
            this.value = value;
        }

        public static MailBoxStatus getMailBoxStatus(byte value) {
            for (MailBoxStatus status : MailBoxStatus.values()) {
                if(status.value == value) {
                    return status;
                }
            }

            return MailBoxStatus.UNKNOWN;
        }
    }

    MailBoxStatus status = MailBoxStatus.UNKNOWN;

    public SA_RDMailboxStatusResponse(Response response) {
        this(response.getPayload().length, response.getOptionalPayload().length,
                Helper.concatAll(response.getPayload(), response.getOptionalPayload()));
    }

    SA_RDMailboxStatusResponse(int dataLength, int optionalDataLength, byte[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (this.payload == null || this.payload.length < 1) {
            return;
        } else {
            _isValid = true;
        }

        status = MailBoxStatus.getMailBoxStatus(payload[1]);
    }

    public MailBoxStatus getMailBoxStatus() {
        return status;
    }
}
