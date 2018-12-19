/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.eep;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.enocean.internal.EnOceanChannelDescription;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_01;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_02;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_03;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_04;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_05;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_06;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_07;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_08;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_09;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_0A;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_0B;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_10;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_11;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_12;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_13;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_14;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_15;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_16;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_17;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_18;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_19;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_1A;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_1B;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_20;
import org.openhab.binding.enocean.internal.eep.A5_02.A5_02_30;
import org.openhab.binding.enocean.internal.eep.A5_04.A5_04_01;
import org.openhab.binding.enocean.internal.eep.A5_04.A5_04_02;
import org.openhab.binding.enocean.internal.eep.A5_04.A5_04_03;
import org.openhab.binding.enocean.internal.eep.A5_08.A5_08_01;
import org.openhab.binding.enocean.internal.eep.A5_08.A5_08_01_FXBH;
import org.openhab.binding.enocean.internal.eep.A5_08.A5_08_02;
import org.openhab.binding.enocean.internal.eep.A5_08.A5_08_03;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_01;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_02;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_03;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_04;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_05;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_06;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_07;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_08;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_09;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_0A;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_0B;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_0C;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_0D;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_10;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_11;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_12;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_13;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_14;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_15;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_16;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_17;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_18;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_19;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1A;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1B;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1C;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1D;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1E;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_1F;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_20;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_21;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_22;
import org.openhab.binding.enocean.internal.eep.A5_10.A5_10_23;
import org.openhab.binding.enocean.internal.eep.A5_11.A5_11_03;
import org.openhab.binding.enocean.internal.eep.A5_11.A5_11_04;
import org.openhab.binding.enocean.internal.eep.A5_12.A5_12_00;
import org.openhab.binding.enocean.internal.eep.A5_12.A5_12_01;
import org.openhab.binding.enocean.internal.eep.A5_12.A5_12_02;
import org.openhab.binding.enocean.internal.eep.A5_12.A5_12_03;
import org.openhab.binding.enocean.internal.eep.A5_14.A5_14_09;
import org.openhab.binding.enocean.internal.eep.A5_38.A5_38_08_Blinds;
import org.openhab.binding.enocean.internal.eep.A5_38.A5_38_08_Dimming;
import org.openhab.binding.enocean.internal.eep.A5_38.A5_38_08_Switching;
import org.openhab.binding.enocean.internal.eep.A5_3F.A5_3F_7F_EltakoFSB;
import org.openhab.binding.enocean.internal.eep.Base.PTM200Message;
import org.openhab.binding.enocean.internal.eep.Base.UTEResponse;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_00;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_01;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_02;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_03;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_04;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_05;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_06;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_07;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_08;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_09;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_09_Permundo;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0A;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0B;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0C;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0D;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0E;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0F;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_0F_NodON;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_11;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_12;
import org.openhab.binding.enocean.internal.eep.D2_01.D2_01_12_NodON;
import org.openhab.binding.enocean.internal.eep.D2_05.D2_05_00;
import org.openhab.binding.enocean.internal.eep.D5_00.D5_00_01;
import org.openhab.binding.enocean.internal.eep.F6_01.F6_01_01;
import org.openhab.binding.enocean.internal.eep.F6_02.F6_02_01;
import org.openhab.binding.enocean.internal.eep.F6_02.F6_02_02;
import org.openhab.binding.enocean.internal.eep.F6_10.F6_10_00;
import org.openhab.binding.enocean.internal.eep.F6_10.F6_10_01;
import org.openhab.binding.enocean.internal.eep.Generic.Generic4BS;
import org.openhab.binding.enocean.internal.eep.Generic.GenericRPS;
import org.openhab.binding.enocean.internal.eep.Generic.GenericVLD;
import org.openhab.binding.enocean.internal.messages.ERP1Message.RORG;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public enum EEPType {
    Undef(RORG.Unknown, 0, 0, false, null, null, 0),

    UTEResponse(RORG.UTE, 0, 0, false, UTEResponse.class, null),

    GenericRPS(RORG.RPS, 0xFF, 0xFF, false, GenericRPS.class, THING_TYPE_GENERICTHING, CHANNEL_GENERIC_SWITCH,
            CHANNEL_GENERIC_ROLLERSHUTTER, CHANNEL_GENERIC_DIMMER, CHANNEL_GENERIC_NUMBER, CHANNEL_GENERIC_STRING,
            CHANNEL_GENERIC_COLOR, CHANNEL_GENERIC_TEACHINCMD),
    Generic4BS(RORG._4BS, 0xFF, 0xFF, false, Generic4BS.class, THING_TYPE_GENERICTHING, CHANNEL_GENERIC_SWITCH,
            CHANNEL_GENERIC_ROLLERSHUTTER, CHANNEL_GENERIC_DIMMER, CHANNEL_GENERIC_NUMBER, CHANNEL_GENERIC_STRING,
            CHANNEL_GENERIC_COLOR, CHANNEL_GENERIC_TEACHINCMD),
    GenericVLD(RORG.VLD, 0xFF, 0xFF, false, GenericVLD.class, THING_TYPE_GENERICTHING, CHANNEL_GENERIC_SWITCH,
            CHANNEL_GENERIC_ROLLERSHUTTER, CHANNEL_GENERIC_DIMMER, CHANNEL_GENERIC_NUMBER, CHANNEL_GENERIC_STRING,
            CHANNEL_GENERIC_COLOR, CHANNEL_GENERIC_TEACHINCMD),

    PTM200(RORG.RPS, 0x00, 0x00, false, PTM200Message.class, null, CHANNEL_GENERAL_SWITCHING, CHANNEL_ROLLERSHUTTER,
            CHANNEL_CONTACT, CHANNEL_RECEIVINGSTATE),

    PushButton(RORG.RPS, 0x01, 0x01, false, F6_01_01.class, THING_TYPE_PUSHBUTTON, CHANNEL_PUSHBUTTON,
            CHANNEL_RECEIVINGSTATE),

    RockerSwitch2RockerStyle1(RORG.RPS, 0x02, 0x01, false, F6_02_01.class, THING_TYPE_ROCKERSWITCH,
            CHANNEL_ROCKERSWITCH_CHANNELA, CHANNEL_ROCKERSWITCH_CHANNELB, CHANNEL_VIRTUALSWITCHA,
            CHANNEL_VIRTUALROLLERSHUTTERA, CHANNEL_VIRTUALROCKERSWITCHB, CHANNEL_ROCKERSWITCHLISTENERSWITCH,
            CHANNEL_ROCKERSWITCHLISTENERROLLERSHUTTER, CHANNEL_RECEIVINGSTATE),
    RockerSwitch2RockerStyle2(RORG.RPS, 0x02, 0x02, false, F6_02_02.class, THING_TYPE_ROCKERSWITCH,
            CHANNEL_ROCKERSWITCH_CHANNELA, CHANNEL_ROCKERSWITCH_CHANNELB, CHANNEL_VIRTUALSWITCHA,
            CHANNEL_VIRTUALROLLERSHUTTERA, CHANNEL_VIRTUALROCKERSWITCHB, CHANNEL_ROCKERSWITCHLISTENERSWITCH,
            CHANNEL_ROCKERSWITCHLISTENERROLLERSHUTTER, CHANNEL_RECEIVINGSTATE),

    MechanicalHandle00(RORG.RPS, 0x10, 0x00, false, F6_10_00.class, THING_TYPE_MECHANICALHANDLE,
            CHANNEL_WINDOWHANDLESTATE, CHANNEL_CONTACT, CHANNEL_RECEIVINGSTATE),
    MechanicalHandle01(RORG.RPS, 0x10, 0x01, false, F6_10_01.class, THING_TYPE_MECHANICALHANDLE,
            CHANNEL_WINDOWHANDLESTATE, CHANNEL_CONTACT, CHANNEL_RECEIVINGSTATE),
    MechanicalHandle02(RORG._4BS, 0x14, 0x09, false, A5_14_09.class, THING_TYPE_MECHANICALHANDLE,
            CHANNEL_WINDOWHANDLESTATE, CHANNEL_CONTACT, CHANNEL_BATTERY_VOLTAGE, CHANNEL_RECEIVINGSTATE),

    ContactAndSwitch(RORG._1BS, 0x00, 0x01, false, D5_00_01.class, THING_TYPE_CONTACT, CHANNEL_CONTACT,
            CHANNEL_RECEIVINGSTATE),

    TemperatureSensor_A5_02_01(RORG._4BS, 0x02, 0x01, false, A5_02_01.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_02(RORG._4BS, 0x02, 0x02, false, A5_02_02.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_03(RORG._4BS, 0x02, 0x03, false, A5_02_03.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_04(RORG._4BS, 0x02, 0x04, false, A5_02_04.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_05(RORG._4BS, 0x02, 0x05, false, A5_02_05.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_06(RORG._4BS, 0x02, 0x06, false, A5_02_06.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_07(RORG._4BS, 0x02, 0x07, false, A5_02_07.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_08(RORG._4BS, 0x02, 0x08, false, A5_02_08.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_09(RORG._4BS, 0x02, 0x09, false, A5_02_09.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_0A(RORG._4BS, 0x02, 0x0A, false, A5_02_0A.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_0B(RORG._4BS, 0x02, 0x0B, false, A5_02_0B.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_10(RORG._4BS, 0x02, 0x10, false, A5_02_10.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_11(RORG._4BS, 0x02, 0x11, false, A5_02_11.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_12(RORG._4BS, 0x02, 0x12, false, A5_02_12.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_13(RORG._4BS, 0x02, 0x13, false, A5_02_13.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_14(RORG._4BS, 0x02, 0x14, false, A5_02_14.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_15(RORG._4BS, 0x02, 0x15, false, A5_02_15.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_16(RORG._4BS, 0x02, 0x16, false, A5_02_16.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_17(RORG._4BS, 0x02, 0x17, false, A5_02_17.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_18(RORG._4BS, 0x02, 0x18, false, A5_02_18.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_19(RORG._4BS, 0x02, 0x19, false, A5_02_19.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_1A(RORG._4BS, 0x02, 0x1A, false, A5_02_1A.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_1B(RORG._4BS, 0x02, 0x1B, false, A5_02_1B.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_20(RORG._4BS, 0x02, 0x20, false, A5_02_20.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),
    TemperatureSensor_A5_02_30(RORG._4BS, 0x02, 0x30, false, A5_02_30.class, THING_TYPE_TEMPERATURESENSOR,
            CHANNEL_TEMPERATURE, CHANNEL_RECEIVINGSTATE),

    TemperatureHumiditySensor_A5_04_01(RORG._4BS, 0x04, 0x01, false, A5_04_01.class,
            THING_TYPE_TEMPERATUREHUMIDITYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_HUMIDITY, CHANNEL_RECEIVINGSTATE),
    TemperatureHumiditySensor_A5_04_02(RORG._4BS, 0x04, 0x02, false, A5_04_02.class,
            THING_TYPE_TEMPERATUREHUMIDITYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_HUMIDITY, CHANNEL_RECEIVINGSTATE),
    TemperatureHumiditySensor_A5_04_03(RORG._4BS, 0x04, 0x03, false, A5_04_03.class,
            THING_TYPE_TEMPERATUREHUMIDITYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_HUMIDITY, CHANNEL_RECEIVINGSTATE),

    LightTemperatureOccupancySensor_A5_08_01(RORG._4BS, 0x08, 0x01, false, A5_08_01.class,
            THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_MOTIONDETECTION,
            CHANNEL_ILLUMINATION, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    LightTemperatureOccupancySensor_A5_08_02(RORG._4BS, 0x08, 0x02, false, A5_08_02.class,
            THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_MOTIONDETECTION,
            CHANNEL_ILLUMINATION, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    LightTemperatureOccupancySensor_A5_08_03(RORG._4BS, 0x08, 0x03, false, A5_08_03.class,
            THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR, CHANNEL_TEMPERATURE, CHANNEL_MOTIONDETECTION,
            CHANNEL_ILLUMINATION, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    FXBH_A5_08_01(RORG._4BS, 0x08, 0x01, false, "FXBH", ELTAKOID, A5_08_01_FXBH.class,
            THING_TYPE_LIGHTTEMPERATUREOCCUPANCYSENSOR, CHANNEL_MOTIONDETECTION, CHANNEL_ILLUMINATION,
            CHANNEL_RECEIVINGSTATE),

    RoomPanel_A5_10_01(RORG._4BS, 0x10, 0x01, false, A5_10_01.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_FANSPEEDSTAGE, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_02(RORG._4BS, 0x10, 0x02, false, A5_10_02.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_FANSPEEDSTAGE, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_03(RORG._4BS, 0x10, 0x03, false, A5_10_03.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_04(RORG._4BS, 0x10, 0x04, false, A5_10_04.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_FANSPEEDSTAGE, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_05(RORG._4BS, 0x10, 0x05, false, A5_10_05.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_06(RORG._4BS, 0x10, 0x06, false, A5_10_06.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_07(RORG._4BS, 0x10, 0x07, false, A5_10_07.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_FANSPEEDSTAGE, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_08(RORG._4BS, 0x10, 0x08, false, A5_10_08.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_FANSPEEDSTAGE, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_09(RORG._4BS, 0x10, 0x09, false, A5_10_09.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_FANSPEEDSTAGE, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_0A(RORG._4BS, 0x10, 0x0A, false, A5_10_0A.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_0B(RORG._4BS, 0x10, 0x0B, false, A5_10_0B.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_0C(RORG._4BS, 0x10, 0x0C, false, A5_10_0C.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_0D(RORG._4BS, 0x10, 0x0D, false, A5_10_0D.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_10(RORG._4BS, 0x10, 0x10, false, A5_10_10.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_11(RORG._4BS, 0x10, 0x11, false, A5_10_11.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_12(RORG._4BS, 0x10, 0x12, false, A5_10_12.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_13(RORG._4BS, 0x10, 0x13, false, A5_10_13.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_14(RORG._4BS, 0x10, 0x14, false, A5_10_14.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_15(RORG._4BS, 0x10, 0x15, false, A5_10_15.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_16(RORG._4BS, 0x10, 0x16, false, A5_10_16.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_17(RORG._4BS, 0x10, 0x17, false, A5_10_17.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_18(RORG._4BS, 0x10, 0x18, false, A5_10_18.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_19(RORG._4BS, 0x10, 0x19, false, A5_10_19.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1A(RORG._4BS, 0x10, 0x1A, false, A5_10_1A.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1B(RORG._4BS, 0x10, 0x1B, false, A5_10_1B.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1C(RORG._4BS, 0x10, 0x1C, false, A5_10_1C.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1D(RORG._4BS, 0x10, 0x1D, false, A5_10_1D.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1E(RORG._4BS, 0x10, 0x1E, false, A5_10_1E.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_1F(RORG._4BS, 0x10, 0x1F, false, A5_10_1F.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_OCCUPANCY, CHANNEL_SETPOINT, CHANNEL_FANSPEEDSTAGE, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_20(RORG._4BS, 0x10, 0x20, false, A5_10_20.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_21(RORG._4BS, 0x10, 0x21, false, A5_10_21.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_22(RORG._4BS, 0x10, 0x22, false, A5_10_22.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_RECEIVINGSTATE),
    RoomPanel_A5_10_23(RORG._4BS, 0x10, 0x23, false, A5_10_23.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT, CHANNEL_OCCUPANCY, CHANNEL_RECEIVINGSTATE),

    AutomatedMeterReading_00(RORG._4BS, 0x12, 0x00, false, A5_12_00.class, THING_TYPE_AUTOMATEDMETERSENSOR,
            CHANNEL_COUNTER, CHANNEL_CURRENTNUMBER, CHANNEL_RECEIVINGSTATE),
    AutomatedMeterReading_01(RORG._4BS, 0x12, 0x01, false, A5_12_01.class, THING_TYPE_AUTOMATEDMETERSENSOR,
            CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_RECEIVINGSTATE),
    AutomatedMeterReading_02(RORG._4BS, 0x12, 0x02, false, A5_12_02.class, THING_TYPE_AUTOMATEDMETERSENSOR,
            CHANNEL_TOTALCUBICMETRE, CHANNEL_INSTANTLITRE, CHANNEL_RECEIVINGSTATE),
    AutomatedMeterReading_03(RORG._4BS, 0x12, 0x03, false, A5_12_03.class, THING_TYPE_AUTOMATEDMETERSENSOR,
            CHANNEL_TOTALCUBICMETRE, CHANNEL_INSTANTLITRE, CHANNEL_RECEIVINGSTATE),

    Rollershutter_A5(RORG._4BS, 0x11, 0x03, false, A5_11_03.class, THING_TYPE_ROLLERSHUTTER, CHANNEL_ROLLERSHUTTER,
            CHANNEL_ANGLE, CHANNEL_RECEIVINGSTATE),
    ExtendedLight_A5(RORG._4BS, 0x11, 0x04, false, A5_11_04.class, THING_TYPE_CENTRALCOMMAND, CHANNEL_GENERAL_SWITCHING,
            CHANNEL_DIMMER, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_COUNTER, CHANNEL_RECEIVINGSTATE),

    CentralCommandSwitching(RORG._4BS, 0x38, 0x08, false, A5_38_08_Switching.class, THING_TYPE_CENTRALCOMMAND, 0x01,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TEACHINCMD, CHANNEL_RECEIVINGSTATE),
    CentralCommandDimming(RORG._4BS, 0x38, 0x08, false, A5_38_08_Dimming.class, THING_TYPE_CENTRALCOMMAND, 0x02,
            CHANNEL_DIMMER, CHANNEL_TEACHINCMD, CHANNEL_RECEIVINGSTATE),
    CentralCommandBlinds(RORG._4BS, 0x38, 0x08, false, A5_38_08_Blinds.class, THING_TYPE_ROLLERSHUTTER, 0x07,
            CHANNEL_ROLLERSHUTTER, CHANNEL_ANGLE, CHANNEL_TEACHINCMD, CHANNEL_RECEIVINGSTATE),

    // UniversalCommand(RORG._4BS, 0x3f, 0x7f, false, A5_3F_7F_Universal.class, THING_TYPE_UNIVERSALACTUATOR,
    // CHANNEL_GENERIC_ROLLERSHUTTER, CHANNEL_GENERIC_LIGHT_SWITCHING, CHANNEL_GENERIC_DIMMER, CHANNEL_TEACHINCMD,
    // CHANNEL_RECEIVINGSTATE),
    EltakoFSB(RORG._4BS, 0x3f, 0x7f, false, "EltakoFSB", 0, A5_3F_7F_EltakoFSB.class, THING_TYPE_ROLLERSHUTTER, 0,
            new Hashtable<String, Configuration>() {
                private static final long serialVersionUID = 1L;
                {
                    put(CHANNEL_ROLLERSHUTTER, new Configuration());
                    put(CHANNEL_TEACHINCMD, new Configuration() {
                        {
                            put(PARAMETER_CHANNEL_TeachInMSG, "fff80d80");
                        }
                    });
                    put(CHANNEL_RECEIVINGSTATE, new Configuration());
                }
            }),

    SwitchWithEnergyMeasurment_00(RORG.VLD, 0x01, 0x00, true, D2_01_00.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_01(RORG.VLD, 0x01, 0x01, true, D2_01_01.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_02(RORG.VLD, 0x01, 0x02, true, D2_01_02.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_DIMMER, CHANNEL_TOTALUSAGE, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_03(RORG.VLD, 0x01, 0x03, true, D2_01_03.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_DIMMER, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_04(RORG.VLD, 0x01, 0x04, true, D2_01_04.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_DIMMER, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER,
            CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_05(RORG.VLD, 0x01, 0x05, true, D2_01_05.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_DIMMER, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER,
            CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_06(RORG.VLD, 0x01, 0x06, true, D2_01_06.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_07(RORG.VLD, 0x01, 0x07, true, D2_01_07.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_08(RORG.VLD, 0x01, 0x08, true, D2_01_08.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_DIMMER, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER,
            CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_09(RORG.VLD, 0x01, 0x09, true, D2_01_09.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_09_PERMUNDO(RORG.VLD, 0x01, 0x09, true, "PERMUNDO", PERMUNDOID, D2_01_09_Permundo.class,
            THING_TYPE_MEASUREMENTSWITCH, CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER,
            CHANNEL_ECOMODE, CHANNEL_REPEATERMODE, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0A(RORG.VLD, 0x01, 0x0A, true, D2_01_0A.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0B(RORG.VLD, 0x01, 0x0B, true, D2_01_0B.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0C(RORG.VLD, 0x01, 0x0C, true, D2_01_0C.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0D(RORG.VLD, 0x01, 0x0D, true, D2_01_0D.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0E(RORG.VLD, 0x01, 0x0E, true, D2_01_0E.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0F_NODON(RORG.VLD, 0x01, 0x0F, true, "NODON", NODONID, D2_01_0F_NodON.class,
            THING_TYPE_MEASUREMENTSWITCH, CHANNEL_GENERAL_SWITCHING, CHANNEL_REPEATERMODE, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_0F(RORG.VLD, 0x01, 0x0F, true, D2_01_0F.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_11(RORG.VLD, 0x01, 0x11, true, D2_01_11.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHINGA, CHANNEL_GENERAL_SWITCHINGB, CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_12_NODON(RORG.VLD, 0x01, 0x12, true, "NODON", NODONID, D2_01_12_NodON.class,
            THING_TYPE_MEASUREMENTSWITCH, CHANNEL_GENERAL_SWITCHINGA, CHANNEL_GENERAL_SWITCHINGB, CHANNEL_REPEATERMODE,
            CHANNEL_RECEIVINGSTATE),
    SwitchWithEnergyMeasurment_12(RORG.VLD, 0x01, 0x12, true, D2_01_12.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHINGA, CHANNEL_GENERAL_SWITCHINGB, CHANNEL_RECEIVINGSTATE),

    Rollershutter_D2(RORG.VLD, 0x05, 0x00, true, D2_05_00.class, THING_TYPE_ROLLERSHUTTER, CHANNEL_ROLLERSHUTTER,
            CHANNEL_RECEIVINGSTATE);

    private RORG rorg;
    private int func;
    private int type;
    private int command;
    private Class<? extends EEP> eepClass;

    private String manufactorSuffix;
    private int manufactorId;

    private ThingTypeUID thingTypeUID;

    private Hashtable<String, Configuration> channelIdsWithConfig = new Hashtable<String, Configuration>();
    private Hashtable<String, EnOceanChannelDescription> supportedChannels = new Hashtable<String, EnOceanChannelDescription>();

    private boolean supportsRefresh;

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, Class<? extends EEP> eepClass,
            ThingTypeUID thingTypeUID, String... channelIds) {
        this(rorg, func, type, supportsRefresh, eepClass, thingTypeUID, -1, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix, int manufId,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, String... channelIds) {
        this(rorg, func, type, supportsRefresh, manufactorSuffix, manufId, eepClass, thingTypeUID, 0, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, Class<? extends EEP> eepClass,
            ThingTypeUID thingTypeUID, int command, String... channelIds) {
        this(rorg, func, type, supportsRefresh, "", 0, eepClass, thingTypeUID, command, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix, int manufId,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, int command, String... channelIds) {
        this.rorg = rorg;
        this.func = func;
        this.type = type;
        this.eepClass = eepClass;
        this.thingTypeUID = thingTypeUID;
        this.command = command;
        this.manufactorSuffix = manufactorSuffix;
        this.manufactorId = manufId;
        this.supportsRefresh = supportsRefresh;

        for (String id : channelIds) {
            this.channelIdsWithConfig.put(id, new Configuration());
            this.supportedChannels.put(id, CHANNELID2CHANNELDESCRIPTION.get(id));
        }
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix, int manufId,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, int command,
            Hashtable<String, Configuration> channelConfigs) {
        this.rorg = rorg;
        this.func = func;
        this.type = type;
        this.eepClass = eepClass;
        this.thingTypeUID = thingTypeUID;
        this.command = command;
        this.channelIdsWithConfig = channelConfigs;
        this.manufactorSuffix = manufactorSuffix;
        this.manufactorId = manufId;
        this.supportsRefresh = supportsRefresh;

        for (String id : channelConfigs.keySet()) {
            this.supportedChannels.put(id, CHANNELID2CHANNELDESCRIPTION.get(id));
        }
    }

    public Class<? extends EEP> getEEPClass() {
        return eepClass;
    }

    public RORG getRORG() {
        return rorg;
    }

    public int getFunc() {
        return func;
    }

    public int getType() {
        return type;
    }

    public boolean getSupportsRefresh() {
        return supportsRefresh;
    }

    public Map<String, EnOceanChannelDescription> GetSupportedChannels() {
        return Collections.unmodifiableMap(supportedChannels);
    }

    public boolean isChannelSupported(Channel channel) {
        return isChannelSupported(channel.getUID().getId(), channel.getChannelTypeUID().getId());
    }

    public boolean isChannelSupported(String channelId, String channelTypeId) {
        return supportedChannels.containsKey(channelId)
                || supportedChannels.values().stream().anyMatch(c -> c.channelTypeUID.getId().equals(channelTypeId));
    }

    public ThingTypeUID getThingTypeUID() {
        return thingTypeUID;
    }

    @NonNull
    public String getId() {
        if (command == -1) {
            return String.format("%02X_%02X_%02X", rorg.getValue(), func, type);
        } else if (command == 0) {
            return String.format("%02X_%02X_%02X_%s", rorg.getValue(), func, type, manufactorSuffix);
        } else {
            return String.format("%02X_%02X_%02X_%02X", rorg.getValue(), func, type, command);
        }
    }

    @NonNull
    public Configuration getChannelConfig(String channelId) {
        Configuration c = null;

        if (channelIdsWithConfig != null) {
            c = channelIdsWithConfig.get(channelId);
        }

        if (c != null) {
            return c;
        }

        return new Configuration();
    }

    public static EEPType getType(String receivingEEPId) {
        for (EEPType eep : values()) {
            if (eep.getId().equals(receivingEEPId)) {
                return eep;
            }
        }

        throw new IllegalArgumentException(String.format("EEP with id {} could not be found", receivingEEPId));
    }

    public static EEPType getType(Class<? extends EEP> eepClass) {
        for (EEPType eep : values()) {
            if (eep.eepClass != null && eep.eepClass.equals(eepClass)) {
                return eep;
            }
        }

        throw new IllegalArgumentException(String.format("EEP with class {} could not be found", eepClass));
    }

    public static EEPType getType(RORG rorg, int func, int type, int manufId) {
        EEPType fallback = null;

        for (EEPType eep : values()) {
            if (eep.rorg == rorg && eep.func == func && eep.type == type && eep.manufactorId == manufId) {
                return eep;
            } else if (fallback == null && eep.rorg == rorg && eep.func == func && eep.type == type) {
                fallback = eep;
            }
        }

        return fallback;
    }

}
