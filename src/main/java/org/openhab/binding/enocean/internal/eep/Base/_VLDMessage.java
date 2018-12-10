/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep.Base;

import org.openhab.binding.enocean.internal.eep.EEP;
import org.openhab.binding.enocean.internal.messages.ERP1Message;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class _VLDMessage extends EEP {

    public _VLDMessage() {
        super();
    }

    public _VLDMessage(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected int getDataLength() {
        if (packet != null) {
            return packet.getPayload().length - SenderIdLength - RORGLength - StatusLength;
        } else {
            return bytes.length;
        }
    }

    @Override
    protected boolean validateData(byte[] bytes) {
        return true;
    }

}
