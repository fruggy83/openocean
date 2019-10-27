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

import java.util.List;

import org.openhab.binding.enocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class SA_RDLearnedClientsResponse extends Response {

    public class LearnedClient {
        public byte[] clientId;
        public byte[] controllerId;
        public int mailboxIndex;
    }

    LearnedClient[] learnedClients;

    public SA_RDLearnedClientsResponse(Response response) {
        this(response.getPayload().length, response.getOptionalPayload().length,
                Helper.concatAll(response.getPayload(), response.getOptionalPayload()));
    }

    SA_RDLearnedClientsResponse(int dataLength, int optionalDataLength, byte[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (this.payload == null || this.payload.length < 1) {
            return;
        } else {
            _isValid = true;
        }

        learnedClients = new LearnedClient[(this.payload.length - 1) / 9];
        for(int i = 0; i < (this.payload.length - 1) / 9; i++) {
            LearnedClient client = new LearnedClient();
            client.clientId = java.util.Arrays.copyOfRange(this.payload, 1 + i * 9, 1 + i * 9 + 4);
            client.controllerId = java.util.Arrays.copyOfRange(this.payload, 5 + i * 9, 5 + i * 9 + 4);
            client.mailboxIndex = payload[9 + i * 9] & 0xFF;            
            learnedClients[i] = client;
        }
    }

    public LearnedClient[] getLearnedClients() {
        return learnedClients;
    }
}
