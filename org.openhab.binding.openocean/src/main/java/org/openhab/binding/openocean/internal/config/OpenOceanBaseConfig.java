/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.config;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBaseConfig {
    public String eepId;
    public String receivingEEPId;

    public String getReceivingEEPId() {
        if (receivingEEPId == null || receivingEEPId.isEmpty()) {
            return eepId;
        }

        return receivingEEPId;
    }
}
