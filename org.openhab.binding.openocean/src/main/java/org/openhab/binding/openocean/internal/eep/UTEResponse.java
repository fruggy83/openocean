/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep;

import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class UTEResponse extends _VLDMessage {

    public static final int TeachIn_MASK = 0x3f;
    public static final int ResponseNeeded_MASK = 0x40;
    public static final int TeachIn_NotSpecified = 0x20;

    public UTEResponse(ERP1Message packet, int[] senderId) {

        int dataLength = packet.getPayload().length - SenderIdLength - RORGLength - StatusLength;

        // setData(Helper.concatAll(new int[] { RORG.UTE.getValue() }, packet.getPayload(RORGLength, dataLength),
        // packet.getSenderId()));

        setData(packet.getPayload(RORGLength, dataLength));

        setSenderId(senderId);
        // setStatus(packet.getPayload(RORGLength + dataLength + SenderIdLength, 1)[0]);
        setStatus(0x80);

        // bytes[1] = 0x91; // bidirectional communication, teach in accepted, teach in response
        bytes[0] = 0x91;

        setOptionalData(Helper.concatAll(new int[] { 0x01 }, packet.getSenderId(), new int[] { 0xff, 0x00 }));
    }
}
