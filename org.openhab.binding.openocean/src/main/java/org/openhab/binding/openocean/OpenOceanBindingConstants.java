/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link OpenOceanBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanBindingConstants {

    public static final String BINDING_ID = "openocean";

    // bridge
    public final static ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_ROCKERSWITCH = new ThingTypeUID(BINDING_ID, "rockerSwitch");
    public final static ThingTypeUID THING_TYPE_SWITCHINGACTUATOR = new ThingTypeUID(BINDING_ID, "switchingActuator");

    // List of all Channel ids
    public final static String REPEATERMODE = "repeater";

    // List of all repeater mode states
    public final static String REPEATERMODE_OFF = "OFF";
    public final static String REPEATERMODE_LEVEL_1 = "LEVEL1";
    public final static String REPEATERMODE_LEVEL_2 = "LEVEL2";

    // Bridge config properties
    public static final String OFFSETID = "offsetId";
    public static final String SENDERID = "senderId";
    public static final String PORT = "port";

    // Bridge properties
    public static final String PROPERTY_Base_ID = "Base ID";
    public static final String PROPERTY_REMAINING_WRITE_CYCLES_Base_ID = "Remaining Base ID Write Cycles";
    public static final String PROPERTY_APP_VERSION = "APP Version";
    public static final String PROPERTY_API_VERSION = "API Version";
    public static final String PROPERTY_CHIP_ID = "Chip ID";
    public static final String PROPERTY_DESCRIPTION = "Description";
}
