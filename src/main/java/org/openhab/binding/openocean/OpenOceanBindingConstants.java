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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.CoreItemFactory;
import org.eclipse.smarthome.core.thing.DefaultSystemChannelTypeProvider;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.openhab.binding.openocean.profiles.OpenOceanProfileTypes;

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
    public final static ThingTypeUID THING_TYPE_SERIALBRIDGE = new ThingTypeUID(BINDING_ID, "serialbridge");
    public final static ThingTypeUID THING_TYPE_TCPBRIDGE = new ThingTypeUID(BINDING_ID, "tcpbridge");

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_PUSHBUTTON = new ThingTypeUID(BINDING_ID, "pushButton");
    public final static ThingTypeUID THING_TYPE_ROCKERSWITCH = new ThingTypeUID(BINDING_ID, "rockerSwitch");
    public final static ThingTypeUID THING_TYPE_VIRTUALROCKERSWITCH = new ThingTypeUID(BINDING_ID,
            "virtualRockerSwitch");
    // public final static ThingTypeUID THING_TYPE_UNIVERSALACTUATOR = new ThingTypeUID(BINDING_ID, "universalCommand");
    public final static ThingTypeUID THING_TYPE_CENTRALCOMMAND = new ThingTypeUID(BINDING_ID, "centralCommand");
    public final static ThingTypeUID THING_TYPE_ROOMOPERATINGPANEL = new ThingTypeUID(BINDING_ID, "roomOperatingPanel");
    public final static ThingTypeUID THING_TYPE_MECHANICALHANDLE = new ThingTypeUID(BINDING_ID, "mechanicalHandle");
    public final static ThingTypeUID THING_TYPE_CONTACTANDSWITCH = new ThingTypeUID(BINDING_ID, "contactSwitch");
    public final static ThingTypeUID THING_TYPE_MEASUREMENTSWITCH = new ThingTypeUID(BINDING_ID, "measurementSwitch");
    public final static ThingTypeUID THING_TYPE_TEMPERATURESENSOR = new ThingTypeUID(BINDING_ID, "temperatureSensor");
    public final static ThingTypeUID THING_TYPE_HUMIDITYTEMPERATURESENSOR = new ThingTypeUID(BINDING_ID,
            "humidityTemperatureSensor");
    public final static ThingTypeUID THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR = new ThingTypeUID(BINDING_ID,
            "lightTemperatureOccupancySensor");
    public final static ThingTypeUID THING_TYPE_GENERICTHING = new ThingTypeUID(BINDING_ID, "genericThing");
    // public final static ThingTypeUID THING_TYPE_ELTAKOFSB = new ThingTypeUID(BINDING_ID, "eltakoFSB");
    public final static ThingTypeUID THING_TYPE_ROLLERSHUTTER = new ThingTypeUID(BINDING_ID, "rollershutter");

    public static final Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_PUSHBUTTON,
            THING_TYPE_ROCKERSWITCH, THING_TYPE_VIRTUALROCKERSWITCH, THING_TYPE_CENTRALCOMMAND,
            THING_TYPE_ROOMOPERATINGPANEL, THING_TYPE_MECHANICALHANDLE, THING_TYPE_CONTACTANDSWITCH,
            THING_TYPE_MEASUREMENTSWITCH, THING_TYPE_TEMPERATURESENSOR, THING_TYPE_HUMIDITYTEMPERATURESENSOR,
            THING_TYPE_GENERICTHING, THING_TYPE_ROLLERSHUTTER, THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR);

    // List of all Channel IDs
    public final static String CHANNEL_REPEATERMODE = "repeater";

    // List of all Channel Type Ids
    public final static String CHANNELTYPE_REPEATERMODE = "repeaterMode";
    public final static String CHANNEL_SETBASEID = "setBaseId";
    public final static String CHANNEL_LIGHT_SWITCHING = "lightSwitch";
    public final static String CHANNEL_GENERAL_SWITCHING = "generalSwitch";

    public final static String CHANNEL_GENERAL_SWITCHINGA = "generalSwitchA"; // used for D2-01-12 EEP
    public final static String CHANNEL_GENERAL_SWITCHINGB = "generalSwitchB"; // used for D2-01-12 EEP

    public final static String CHANNEL_DIMMER = "dimmer";
    public final static String CHANNEL_ROLLERSHUTTER = "rollershutter";
    public final static String CHANNEL_ANGLE = "angle";
    public final static String CHANNEL_TEMPERATURE = "temperature";
    public final static String CHANNEL_HUMIDITY = "humidity";
    public final static String CHANNEL_SETPOINT = "setPoint";
    public final static String CHANNEL_FANSPEEDSTAGE = "fanSpeedStage";
    public final static String CHANNEL_OCCUPANCY = "occupancy";
    public final static String CHANNEL_MOTIONDETECTION = "motionDetection";
    public final static String CHANNEL_ILLUMINATION = "illumination";

    public final static String CHANNEL_PUSHBUTTON = "pushButton";

    public final static String CHANNEL_ROCKERSWITCH_CHANNELA = "rockerswitchA";
    public final static String CHANNEL_ROCKERSWITCH_CHANNELB = "rockerswitchB";

    public final static String CHANNEL_VIRTUALROCKERSWITCH_CHANNELA = "virtualRockerswitchA";
    public final static String CHANNEL_VIRTUALROCKERSWITCH_CHANNELB = "virtualRockerswitchB";

    public final static String CHANNEL_WINDOWHANDLESTATE = "windowHandleState";
    public final static String CHANNEL_CONTACT = "contact";
    public final static String CHANNEL_TEACHINCMD = "teachInCMD";
    public final static String CHANNEL_INSTANTPOWER = "instantpower";
    public final static String CHANNEL_TOTALUSAGE = "totalusage";
    public final static String CHANNEL_AUTOOFF = "autoOFF";
    public final static String CHANNEL_DELAYRADIOOFF = "delayRadioOFF";
    public final static String CHANNEL_EXTERNALINTERFACEMODE = "externalInterfaceMode";
    public final static String CHANNEL_TWOSTATESWITCH = "twoStateSwitch";

    public final static String CHANNEL_RECEIVINGSTATE = "receivingState";

    public final static String CHANNEL_GENERIC_LIGHT_SWITCHING = "genericLightSwitch";
    public final static String CHANNEL_GENERIC_ROLLERSHUTTER = "genericRollershutter";
    public final static String CHANNEL_GENERIC_DIMMER = "genericDimmer";
    public final static String CHANNEL_GENERIC_NUMBER = "genericNumber";
    public final static String CHANNEL_GENERIC_STRING = "genericString";
    public final static String CHANNEL_GENERIC_COLOR = "genericColor";
    public final static String CHANNEL_GENERIC_TEACHINCMD = "genericTeachInCMD";

    public final static ChannelTypeUID VirtualRockerSwitchChannelType = new ChannelTypeUID(BINDING_ID,
            "virtualRockerswitch");

    public static final Map<String, ChannelDescription> ChannelId2ChannelDescription = Collections
            .unmodifiableMap(new HashMap<String, ChannelDescription>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CHANNEL_LIGHT_SWITCHING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_LIGHT_SWITCHING), CoreItemFactory.SWITCH));
                    put(CHANNEL_GENERAL_SWITCHING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERAL_SWITCHING), CoreItemFactory.SWITCH));

                    put(CHANNEL_GENERAL_SWITCHINGA, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERAL_SWITCHINGA), CoreItemFactory.SWITCH));
                    put(CHANNEL_GENERAL_SWITCHINGB, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERAL_SWITCHINGB), CoreItemFactory.SWITCH));

                    put(CHANNEL_DIMMER, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_DIMMER),
                            CoreItemFactory.DIMMER));
                    put(CHANNEL_ROLLERSHUTTER, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_ROLLERSHUTTER), CoreItemFactory.ROLLERSHUTTER));
                    put(CHANNEL_ANGLE, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_ANGLE),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_TEMPERATURE, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_TEMPERATURE),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_HUMIDITY, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_HUMIDITY),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_FANSPEEDSTAGE, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_FANSPEEDSTAGE), CoreItemFactory.STRING));
                    put(CHANNEL_OCCUPANCY, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_OCCUPANCY),
                            CoreItemFactory.SWITCH));
                    put(CHANNEL_MOTIONDETECTION, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_MOTIONDETECTION), CoreItemFactory.SWITCH));
                    put(CHANNEL_ILLUMINATION, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_ILLUMINATION), CoreItemFactory.NUMBER));
                    put(CHANNEL_SETPOINT, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_SETPOINT),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_CONTACT, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_CONTACT),
                            CoreItemFactory.CONTACT));
                    put(CHANNEL_WINDOWHANDLESTATE, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_WINDOWHANDLESTATE), CoreItemFactory.STRING));
                    put(CHANNEL_TEACHINCMD, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_TEACHINCMD),
                            CoreItemFactory.SWITCH));

                    put(CHANNEL_PUSHBUTTON, new ChannelDescription(
                            DefaultSystemChannelTypeProvider.SYSTEM_RAWBUTTON.getUID(), "", "Push button", false));

                    put(CHANNEL_ROCKERSWITCH_CHANNELA,
                            new ChannelDescription(DefaultSystemChannelTypeProvider.SYSTEM_RAWROCKER.getUID(), "",
                                    "Rockerswitch channel A", false));
                    put(CHANNEL_ROCKERSWITCH_CHANNELB,
                            new ChannelDescription(DefaultSystemChannelTypeProvider.SYSTEM_RAWROCKER.getUID(), "",
                                    "Rockerswitch channel B", false));

                    put(CHANNEL_VIRTUALROCKERSWITCH_CHANNELA,
                            new ChannelDescription(VirtualRockerSwitchChannelType, "", "Rockerswitch channel A", true));
                    put(CHANNEL_VIRTUALROCKERSWITCH_CHANNELB,
                            new ChannelDescription(VirtualRockerSwitchChannelType, "", "Rockerswitch channel B", true));

                    put(CHANNEL_INSTANTPOWER, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_INSTANTPOWER), CoreItemFactory.NUMBER));
                    put(CHANNEL_TOTALUSAGE, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_TOTALUSAGE),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_AUTOOFF, new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_AUTOOFF),
                            CoreItemFactory.NUMBER));
                    put(CHANNEL_DELAYRADIOOFF, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_DELAYRADIOOFF), CoreItemFactory.NUMBER));
                    put(CHANNEL_EXTERNALINTERFACEMODE, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_EXTERNALINTERFACEMODE), CoreItemFactory.STRING));
                    put(CHANNEL_TWOSTATESWITCH, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_TWOSTATESWITCH), CoreItemFactory.SWITCH));

                    put(CHANNEL_RECEIVINGSTATE, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_RECEIVINGSTATE), CoreItemFactory.STRING));

                    put(CHANNEL_GENERIC_LIGHT_SWITCHING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_LIGHT_SWITCHING), CoreItemFactory.SWITCH));
                    put(CHANNEL_GENERIC_ROLLERSHUTTER,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_ROLLERSHUTTER),
                                    CoreItemFactory.ROLLERSHUTTER));
                    put(CHANNEL_GENERIC_DIMMER, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_DIMMER), CoreItemFactory.DIMMER));
                    put(CHANNEL_GENERIC_NUMBER, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_NUMBER), CoreItemFactory.NUMBER));
                    put(CHANNEL_GENERIC_STRING, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_STRING), CoreItemFactory.STRING));
                    put(CHANNEL_GENERIC_COLOR, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_COLOR), CoreItemFactory.COLOR));
                    put(CHANNEL_GENERIC_TEACHINCMD,
                            new ChannelDescription(new ChannelTypeUID(BINDING_ID, CHANNEL_GENERIC_TEACHINCMD),
                                    CoreItemFactory.SWITCH, "Teach in", true));

                    put(CHANNELTYPE_REPEATERMODE, new ChannelDescription(
                            new ChannelTypeUID(BINDING_ID, CHANNELTYPE_REPEATERMODE), CoreItemFactory.STRING));
                }
            });

    // List of all repeater mode states
    @NonNull
    public final static String REPEATERMODE_OFF = "OFF";
    public final static String REPEATERMODE_LEVEL_1 = "LEVEL1";
    public final static String REPEATERMODE_LEVEL_2 = "LEVEL2";

    // Bridge config properties
    public static final String SENDERID = "senderId";
    public static final String PORT = "port";
    public static final String HOST = "host";
    public static final String RS485 = "rs485";
    public static final String NEXTDEVICEID = "nextDeviceId";

    // Bridge properties
    @NonNull
    public static final String PROPERTY_Base_ID = "Base ID";
    @NonNull
    public static final String PROPERTY_REMAINING_WRITE_CYCLES_Base_ID = "Remaining Base ID Write Cycles";
    @NonNull
    public static final String PROPERTY_APP_VERSION = "APP Version";
    @NonNull
    public static final String PROPERTY_API_VERSION = "API Version";
    @NonNull
    public static final String PROPERTY_CHIP_ID = "Chip ID";
    @NonNull
    public static final String PROPERTY_DESCRIPTION = "Description";

    // Thing properties
    public static final String PROPERTY_Enocean_ID = "enoceanId";

    // Thing config parameter
    public static final String PARAMETER_SENDERIDOFFSET = "senderIdOffset";
    public static final String PARAMETER_RECEIVINGEEPID = "receivingEEPId";
    @NonNull
    public static final String PARAMETER_EEPID = "eepId";

    public static final String PARAMETER_BROADCASTMESSAGES = "broadcastMessages";

    // Channel config parameter
    public static final String PARAMETER_CHANNEL_TeachInMSG = "teachInMSG";
    // public static final String PARAMETER_CHANNEL_TeachInType = "teachInType";

    @NonNull
    public static final Set<ProfileTypeUID> SUPPORTED_PROFILETYPES_UIDS = ImmutableSet
            .of(OpenOceanProfileTypes.RockerSwitchToPlayPause, OpenOceanProfileTypes.RockerSwitchFromOnOff);

    public static final int EltakoId = 0x00d;
    public static final int NodONId = 0x046; // NodOn devices are designed by ID-RF hence use their ID

}
