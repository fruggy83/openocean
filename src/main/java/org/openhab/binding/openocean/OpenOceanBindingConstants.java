/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;

import com.google.common.collect.ImmutableSet;

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
    public final static ThingTypeUID THING_TYPE_UNIVERSALACTUATOR = new ThingTypeUID(BINDING_ID, "universalCommand");
    public final static ThingTypeUID THING_TYPE_CENTRALCOMMAND = new ThingTypeUID(BINDING_ID, "centralCommand");
    public final static ThingTypeUID THING_TYPE_ROOMOPERATINGPANEL = new ThingTypeUID(BINDING_ID, "roomOperatingPanel");
    public final static ThingTypeUID THING_TYPE_MECHANICALHANDLE = new ThingTypeUID(BINDING_ID, "mechanicalHandle");
    public final static ThingTypeUID THING_TYPE_CONTACTANDSWITCH = new ThingTypeUID(BINDING_ID, "contactSwitch");
    public final static ThingTypeUID THING_TYPE_MEASUREMENTSWITCH = new ThingTypeUID(BINDING_ID, "measurementSwitch");

    public static final Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_ROCKERSWITCH,
            THING_TYPE_UNIVERSALACTUATOR, THING_TYPE_CENTRALCOMMAND, THING_TYPE_ROOMOPERATINGPANEL,
            THING_TYPE_MECHANICALHANDLE, THING_TYPE_CONTACTANDSWITCH, THING_TYPE_MEASUREMENTSWITCH);

    public final static String CHANNELTYPE_ROCKERSWITCH = "rockerSwitch";
    public final static ChannelTypeUID CHANNEL_TYPE_ROCKERSWITCH = new ChannelTypeUID(BINDING_ID,
            CHANNELTYPE_ROCKERSWITCH);

    // List of all Channel IDs
    public final static String REPEATERMODE = "repeater";
    public final static String CHANNEL_LIGHT_SWITCHING = "lightSwitch";
    public final static String CHANNEL_GENERAL_SWITCHING = "generalSwitch";
    public final static String CHANNEL_DIMMER = "dimmer";
    public final static String CHANNEL_ROLLERSHUTTER = "rollershutter";
    public final static String CHANNEL_TEMPERATURE = "temperature";
    public final static String CHANNEL_SETPOINT = "setPoint";
    public final static String CHANNEL_ROCKERSWITCH_CHANNELA = "rockerswitchA"; // this channel is used to react on
                                                                                // trigger events
    public final static String CHANNEL_ROCKERSWITCH_CHANNELB = "rockerswitchB"; // this channel is used to react on
                                                                                // trigger events
    public final static String CHANNEL_GENERALSWITCH_CHANNELA = "generalSwitchA"; // this channel is used to emit an
                                                                                  // enocean telegram
    public final static String CHANNEL_GENERALSWITCH_CHANNELB = "generalSwitchB"; // this channel is used to emit an
                                                                                  // enocean telegram
    public final static String CHANNEL_WINDOWHANDLESTATE = "windowHandleState";
    public final static String CHANNEL_CONTACT = "contact";
    public final static String CHANNEL_TEACHINCMD = "teachInCMD";
    public final static String CHANNEL_GENERIC_LIGHT_SWITCHING = "genericLightSwitch";
    public final static String CHANNEL_GENERIC_GENERAL_SWITCHING = "genericGeneralSwitch";
    public final static String CHANNEL_GENERIC_DIMMER = "genericDimmer";
    public final static String CHANNEL_GENERIC_ROLLERSHUTTER = "genericRollershutter";

    // item types
    public final static String DIMMER = "Dimmer";
    public final static String SWITCH = "Switch";
    public final static String BLIND = "Rollershutter";
    public final static String STRING = "String";
    public final static String NUMBER = "Number";
    public final static String CONTACT = "Contact";

    public static final Map<String, ChannelDescription> ChannelId2ChannelDescription = Collections
            .unmodifiableMap(new HashMap<String, ChannelDescription>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CHANNEL_LIGHT_SWITCHING,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_LIGHT_SWITCHING), SWITCH));
                    put(CHANNEL_GENERAL_SWITCHING,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERAL_SWITCHING), SWITCH));
                    put(CHANNEL_DIMMER, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_DIMMER), DIMMER));
                    put(CHANNEL_ROLLERSHUTTER,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_ROLLERSHUTTER), BLIND));
                    put(CHANNEL_TEMPERATURE,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_TEMPERATURE), NUMBER));
                    put(CHANNEL_SETPOINT,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_SETPOINT), NUMBER));
                    put(CHANNEL_CONTACT,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_CONTACT), CONTACT));
                    put(CHANNEL_WINDOWHANDLESTATE,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_WINDOWHANDLESTATE), STRING));
                    put(CHANNEL_TEACHINCMD,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_TEACHINCMD), SWITCH));
                    put(CHANNEL_GENERIC_LIGHT_SWITCHING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_LIGHT_SWITCHING), SWITCH));
                    put(CHANNEL_GENERIC_GENERAL_SWITCHING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_GENERAL_SWITCHING), SWITCH));
                    put(CHANNEL_GENERIC_DIMMER,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_DIMMER), DIMMER));
                    put(CHANNEL_GENERIC_ROLLERSHUTTER, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_ROLLERSHUTTER), BLIND));
                    put(CHANNEL_ROCKERSWITCH_CHANNELA, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNELTYPE_ROCKERSWITCH), null, "Rockerswitch channel A"));
                    put(CHANNEL_ROCKERSWITCH_CHANNELB, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNELTYPE_ROCKERSWITCH), null, "Rockerswitch channel B"));
                    put(CHANNEL_GENERALSWITCH_CHANNELA,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERALSWITCH_CHANNELA),
                                    SWITCH, "Rockerswitch channel A"));
                    put(CHANNEL_GENERALSWITCH_CHANNELB,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERALSWITCH_CHANNELB),
                                    SWITCH, "Rockerswitch channel B"));
                }
            });

    // List of all repeater mode states
    public final static String REPEATERMODE_OFF = "OFF";
    public final static String REPEATERMODE_LEVEL_1 = "LEVEL1";
    public final static String REPEATERMODE_LEVEL_2 = "LEVEL2";

    // Bridge config properties
    public static final String SENDERID = "senderId";
    public static final String PORT = "port";
    public static final String NEXTDEVICEID = "nextDeviceId";

    // Bridge properties
    public static final String PROPERTY_Base_ID = "Base ID";
    public static final String PROPERTY_REMAINING_WRITE_CYCLES_Base_ID = "Remaining Base ID Write Cycles";
    public static final String PROPERTY_APP_VERSION = "APP Version";
    public static final String PROPERTY_API_VERSION = "API Version";
    public static final String PROPERTY_CHIP_ID = "Chip ID";
    public static final String PROPERTY_DESCRIPTION = "Description";

    // Thing properties
    public static final String PROPERTY_Enocean_ID = "enoceanId";

    // Thing config parameter
    public static final String PARAMETER_SENDERIDOFFSET = "senderIdOffset";
    public static final String PARAMETER_RECEIVINGEEPID = "receivingEEPId";
    public static final String PARAMETER_EEPID = "eepId";

    // Channel config parameter
    public static final String PARAMETER_CHANNEL_TeachInMSG = "teachInMSG";
    // public static final String PARAMETER_CHANNEL_TeachInType = "teachInType";

    // Trigger events
    public static final String UP_PRESSED = "UP_PRESSED";
    public static final String UP_RELEASED = "UP_RELEASED";
    public static final String DOWN_PRESSED = "DOWN_PRESSED";
    public static final String DOWN_RELEASED = "DOWN_RELEASED";

    public static final ProfileTypeUID RockerSwitchEventsToOnOffProfileTypeUID = new ProfileTypeUID(BINDING_ID,
            "rockerSwitchToOnOff");

    public static final Set<ProfileTypeUID> SUPPORTED_PROFILETYPES_UIDS = ImmutableSet
            .of(RockerSwitchEventsToOnOffProfileTypeUID);

}
