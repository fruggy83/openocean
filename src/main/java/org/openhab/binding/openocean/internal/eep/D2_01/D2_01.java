/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.D2_01;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.PARAMETER_EEPID;

import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.binding.openocean.internal.eep._VLDMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class D2_01 extends _VLDMessage {

    protected int cmdMask = 0x0f;
    protected int outputValueMask = 0x7f;

    protected int CMD_ACTUATOR_STATUS_RESPONE = 0x04;

    public D2_01() {
        super();
    }

    public D2_01(ERP1Message packet) {
        super(packet);
    }

    protected int getCMD() {
        return bytes[0] & cmdMask;
    }

    protected void setSwitchingData(OnOffType command, int outputChannel) {
        if (command == OnOffType.ON) {
            setData(0x01, outputChannel, 0x01);
        } else {
            setData(0x01, outputChannel, 0x00);
        }

        if (destinationId != null) {
            setOptionalData(Helper.concatAll(new int[] { 0x01 }, destinationId, new int[] { 0xff, 0x00 }));
        }
    }

    @Override
    public void addConfigPropertiesTo(DiscoveryResultBuilder discoveredThingResultBuilder) {
        discoveredThingResultBuilder.withProperty(PARAMETER_EEPID, getEEPType().getId());
    }
}
