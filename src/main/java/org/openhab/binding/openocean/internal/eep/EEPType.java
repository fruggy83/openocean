/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.eep;

import static org.openhab.binding.openocean.OpenOceanBindingConstants.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_01;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_02;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_03;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_04;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_05;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_06;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_07;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_08;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_09;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_0A;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_0B;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_0C;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_0D;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_10;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_11;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_12;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_13;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_14;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_15;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_16;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_17;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_18;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_19;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1A;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1B;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1C;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1D;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1E;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_1F;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_20;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_21;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_22;
import org.openhab.binding.openocean.internal.eep.A5_10.A5_10_23;
import org.openhab.binding.openocean.internal.eep.A5_38.A5_38_08_Dimming;
import org.openhab.binding.openocean.internal.eep.A5_38.A5_38_08_Switching;
import org.openhab.binding.openocean.internal.eep.A5_3F.A5_3F_7F_EltakoFSB;
import org.openhab.binding.openocean.internal.eep.A5_3F.A5_3F_7F_Universal;
import org.openhab.binding.openocean.internal.eep.D2_01.D2_01_09;
import org.openhab.binding.openocean.internal.eep.D2_01.D2_01_0A;
import org.openhab.binding.openocean.internal.eep.D5_00.D5_00_01;
import org.openhab.binding.openocean.internal.eep.F6_02.F6_02_02;
import org.openhab.binding.openocean.internal.eep.F6_02.F6_02_03;
import org.openhab.binding.openocean.internal.eep.F6_02.F6_02_04;
import org.openhab.binding.openocean.internal.eep.F6_10.F6_10_00;
import org.openhab.binding.openocean.internal.messages.ERP1Message.RORG;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public enum EEPType {
    Undef(RORG.Unknown, 0, 0, false, null, null, 0),
    Generic1BS(RORG._1BS, 0, 0, false, null, null),
    Generic4BS(RORG._4BS, 0, 0, false, null, null),
    Generic(RORG.Unknown, 0, 0, false, GenericEEP.class, null, CHANNEL_GENERIC_ROLLERSHUTTER,
            CHANNEL_GENERIC_LIGHT_SWITCHING, CHANNEL_GENERIC_DIMMER),
    UTEResponse(RORG.UTE, 0, 0, false, UTEResponse.class, null),
    RockerSwitch2RockerStyle1(RORG.RPS, 0x02, 0x02, false, F6_02_02.class, THING_TYPE_ROCKERSWITCH,
            CHANNEL_ROCKERSWITCH_CHANNELA, CHANNEL_ROCKERSWITCH_CHANNELB, CHANNEL_GENERALSWITCH_CHANNELA,
            CHANNEL_GENERALSWITCH_CHANNELB),
    RockerSwitch2RockerStyle2(RORG.RPS, 0x02, 0x03, false, F6_02_03.class, THING_TYPE_ROCKERSWITCH,
            CHANNEL_ROCKERSWITCH_CHANNELA, CHANNEL_ROCKERSWITCH_CHANNELB, CHANNEL_GENERALSWITCH_CHANNELA,
            CHANNEL_GENERALSWITCH_CHANNELB),
    RockerSwitch2RockerStyle3(RORG.RPS, 0x02, 0x04, false, F6_02_04.class, THING_TYPE_ROCKERSWITCH,
            CHANNEL_ROCKERSWITCH_CHANNELA, CHANNEL_ROCKERSWITCH_CHANNELB, CHANNEL_GENERALSWITCH_CHANNELA,
            CHANNEL_GENERALSWITCH_CHANNELB),
    MechanicalHandle00(RORG.RPS, 0x10, 0x00, false, F6_10_00.class, THING_TYPE_MECHANICALHANDLE,
            CHANNEL_WINDOWHANDLESTATE, CHANNEL_CONTACT),
    PTM200(RORG.RPS, 0x00, 0x00, false, PTM200Message.class, null, CHANNEL_LIGHT_SWITCHING),
    ContactAndSwitch(RORG._1BS, 0x00, 0x01, false, D5_00_01.class, THING_TYPE_CONTACTANDSWITCH, CHANNEL_CONTACT),
    RoomPanel_A5_10_01(RORG._4BS, 0x10, 0x01, false, A5_10_01.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_02(RORG._4BS, 0x10, 0x02, false, A5_10_02.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_03(RORG._4BS, 0x10, 0x03, false, A5_10_03.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_04(RORG._4BS, 0x10, 0x04, false, A5_10_04.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_05(RORG._4BS, 0x10, 0x05, false, A5_10_05.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_06(RORG._4BS, 0x10, 0x06, false, A5_10_06.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_07(RORG._4BS, 0x10, 0x07, false, A5_10_07.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_08(RORG._4BS, 0x10, 0x08, false, A5_10_08.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_09(RORG._4BS, 0x10, 0x09, false, A5_10_09.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_0A(RORG._4BS, 0x10, 0x0A, false, A5_10_0A.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_0B(RORG._4BS, 0x10, 0x0B, false, A5_10_0B.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_0C(RORG._4BS, 0x10, 0x0C, false, A5_10_0C.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_0D(RORG._4BS, 0x10, 0x0D, false, A5_10_0D.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_10(RORG._4BS, 0x10, 0x10, false, A5_10_10.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_11(RORG._4BS, 0x10, 0x11, false, A5_10_11.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_12(RORG._4BS, 0x10, 0x12, false, A5_10_12.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_13(RORG._4BS, 0x10, 0x13, false, A5_10_13.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_14(RORG._4BS, 0x10, 0x14, false, A5_10_14.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_15(RORG._4BS, 0x10, 0x15, false, A5_10_15.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_16(RORG._4BS, 0x10, 0x16, false, A5_10_16.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_17(RORG._4BS, 0x10, 0x17, false, A5_10_17.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_18(RORG._4BS, 0x10, 0x18, false, A5_10_18.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_19(RORG._4BS, 0x10, 0x19, false, A5_10_19.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1A(RORG._4BS, 0x10, 0x1A, false, A5_10_1A.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1B(RORG._4BS, 0x10, 0x1B, false, A5_10_1B.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1C(RORG._4BS, 0x10, 0x1C, false, A5_10_1C.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1D(RORG._4BS, 0x10, 0x1D, false, A5_10_1D.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1E(RORG._4BS, 0x10, 0x1E, false, A5_10_1E.class, THING_TYPE_ROOMOPERATINGPANEL,
            CHANNEL_TEMPERATURE),
    RoomPanel_A5_10_1F(RORG._4BS, 0x10, 0x1F, false, A5_10_1F.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_20(RORG._4BS, 0x10, 0x20, false, A5_10_20.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_21(RORG._4BS, 0x10, 0x21, false, A5_10_21.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_22(RORG._4BS, 0x10, 0x22, false, A5_10_22.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    RoomPanel_A5_10_23(RORG._4BS, 0x10, 0x23, false, A5_10_23.class, THING_TYPE_ROOMOPERATINGPANEL, CHANNEL_TEMPERATURE,
            CHANNEL_SETPOINT),
    CentralCommandSwitching(RORG._4BS, 0x38, 0x08, false, A5_38_08_Switching.class, THING_TYPE_CENTRALCOMMAND, 0x01,
            CHANNEL_LIGHT_SWITCHING, CHANNEL_TEACHINCMD),
    CentralCommandDimming(RORG._4BS, 0x38, 0x08, false, A5_38_08_Dimming.class, THING_TYPE_CENTRALCOMMAND, 0x02,
            CHANNEL_DIMMER, CHANNEL_TEACHINCMD),
    UniversalCommand(RORG._4BS, 0x3f, 0x7f, false, A5_3F_7F_Universal.class, THING_TYPE_UNIVERSALACTUATOR,
            CHANNEL_GENERIC_ROLLERSHUTTER, CHANNEL_GENERIC_LIGHT_SWITCHING, CHANNEL_GENERIC_DIMMER, CHANNEL_TEACHINCMD),
    EltakoFSB(RORG._4BS, 0x3f, 0x7f, false, "EltakoFSB", A5_3F_7F_EltakoFSB.class, THING_TYPE_UNIVERSALACTUATOR, 0,
            new Hashtable<String, Configuration>() {
                private static final long serialVersionUID = 1L;
                {
                    put(CHANNEL_ROLLERSHUTTER, new Configuration());
                    put(CHANNEL_TEACHINCMD, new Configuration() {
                        {
                            put(PARAMETER_CHANNEL_TeachInMSG, "fff80d80");
                        }
                    });
                }
            }),
    SwitchWithEnergyMeasurment_09(RORG.VLD, 0x01, 0x09, true, D2_01_09.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING, CHANNEL_TOTALUSAGE, CHANNEL_INSTANTPOWER),
    SwitchWithEnergyMeasurment_0A(RORG.VLD, 0x01, 0x0A, true, D2_01_0A.class, THING_TYPE_MEASUREMENTSWITCH,
            CHANNEL_GENERAL_SWITCHING);

    private RORG rorg;
    private int func;
    private int type;
    private int command;
    private Class<? extends EEP> eepClass;

    private String manufactorSuffix;

    private ThingTypeUID thingTypeUID;

    private Hashtable<String, Configuration> channelIdsWithConfig = new Hashtable<String, Configuration>();

    private boolean supportsRefresh;

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, Class<? extends EEP> eepClass,
            ThingTypeUID thingTypeUID, String... channelIds) {
        this(rorg, func, type, supportsRefresh, eepClass, thingTypeUID, -1, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, String... channelIds) {
        this(rorg, func, type, supportsRefresh, manufactorSuffix, eepClass, thingTypeUID, 0, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, Class<? extends EEP> eepClass,
            ThingTypeUID thingTypeUID, int command, String... channelIds) {
        this(rorg, func, type, supportsRefresh, "", eepClass, thingTypeUID, command, channelIds);
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, int command, String... channelIds) {
        this.rorg = rorg;
        this.func = func;
        this.type = type;
        this.eepClass = eepClass;
        this.thingTypeUID = thingTypeUID;
        this.command = command;
        this.manufactorSuffix = manufactorSuffix;
        this.supportsRefresh = supportsRefresh;

        for (String id : channelIds) {
            this.channelIdsWithConfig.put(id, new Configuration());
        }
    }

    EEPType(RORG rorg, int func, int type, boolean supportsRefresh, String manufactorSuffix,
            Class<? extends EEP> eepClass, ThingTypeUID thingTypeUID, int command,
            Hashtable<String, Configuration> channels) {
        this.rorg = rorg;
        this.func = func;
        this.type = type;
        this.eepClass = eepClass;
        this.thingTypeUID = thingTypeUID;
        this.command = command;
        this.channelIdsWithConfig = channels;
        this.manufactorSuffix = manufactorSuffix;
        this.supportsRefresh = supportsRefresh;
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

    public Set<String> GetChannelIds() {
        return Collections.unmodifiableSet(channelIdsWithConfig.keySet());
    }

    public ThingTypeUID getThingTypeUID() {
        return thingTypeUID;
    }

    public String getId() {
        if (command == -1) {
            return String.format("%02X_%02X_%02X", rorg.getValue(), func, type);
        } else if (command == 0) {
            return String.format("%02X_%02X_%02X_%s", rorg.getValue(), func, type, manufactorSuffix);
        } else {
            return String.format("%02X_%02X_%02X_%02X", rorg.getValue(), func, type, command);
        }
    }

    public Configuration getChannelConfig(String channelId) {
        if (channelIdsWithConfig != null) {
            return channelIdsWithConfig.get(channelId);
        }

        return null;
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

    public static EEPType getType(RORG rorg, int func, int type) {
        for (EEPType eep : values()) {
            if (eep.rorg == rorg && eep.func == func && eep.type == type) {
                return eep;
            }
        }

        return null;
    }

}
