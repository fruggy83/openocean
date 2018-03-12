/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.Base;

import org.openhab.binding.openocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class UTEResponse extends _VLDMessage {

    public static final int TeachIn_MASK = 0x3f;
    public static final int ResponseNeeded_MASK = 0x40;
    public static final int TeachIn_NotSpecified = 0x20;

    public UTEResponse(ERP1Message packet) {

        int dataLength = packet.getPayload().length - SenderIdLength - RORGLength - StatusLength;

        setData(packet.getPayload(RORGLength, dataLength));
        bytes[0] = 0x91; // bidirectional communication, teach in accepted, teach in response

        setStatus(0x80);

        setDestinationId(packet.getSenderId());
    }
}
