/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep.A5_3F;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.transform.actions.Transformation;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.openocean.internal.config.OpenOceanChannelTransformationConfig;
import org.openhab.binding.openocean.internal.eep.Base._4BSMessage;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.transceiver.Helper;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class A5_3F_7F_Universal extends _4BSMessage {

    // This class is currently not used => instead use Generic4BS

    public A5_3F_7F_Universal() {
        super();
    }

    public A5_3F_7F_Universal(ERP1Message packet) {
        super(packet);
    }

    @Override
    protected void convertFromCommandImpl(Command command, String channelId, State currentState, Configuration config) {

        if (config != null) {
            try {
                OpenOceanChannelTransformationConfig transformationInfo = config
                        .as(OpenOceanChannelTransformationConfig.class);
                String c = Transformation.transform(transformationInfo.transformationType,
                        transformationInfo.transformationFuntion, command.toString());

                if (c != command.toString()) {
                    setData(Helper.hexStringTo4Bytes(c));
                }

            } catch (Exception e) {
                logger.debug("Command {} could not transformed", command.toString());
            }
        }
    }
}
