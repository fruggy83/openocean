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
public class OpenOceanChannelTeachInConfig {

    /*
     * public enum TeachInType {
     * UNDEF("UNDEF"),
     * UniWoEEP("uniWoEEP"),
     * UniWEEP("uniWEEP"),
     * BiWEEP("biWEEP");
     *
     * private String value;
     *
     * TeachInType(String value) {
     * this.value = value;
     * }
     *
     * static TeachInType getTeachInType(String teachInType) {
     * for (TeachInType type : values()) {
     * if (type.value.equals(teachInType)) {
     * return type;
     * }
     * }
     *
     * return UNDEF;
     * }
     * }
     */

    public String teachInMSG;
    public String manufacturerId;
    // private String teachInType;

    /*
     * public TeachInType getTeachInType() {
     * return TeachInType.getTeachInType(this.teachInType);
     * }
     */
}
