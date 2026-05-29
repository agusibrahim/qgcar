package com.qgcar.bus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Commands and parameters used to control and query the Air Conditioning (AC) system.
 * The order of enum elements corresponds precisely to the system ordinals.
 */
public enum AirConditionState implements Parcelable {
    AC_BLOWER(1),
    HVAC_VD_MODE(2),
    AC_LEFT_TEMP(3),
    AC_RIGHT_TEMP(4),
    AC_SWITCH(5),
    AC_MAX_SWITCH(6),
    AC_AUTO(7),
    AC_POWER_SWITCH(8),
    AC_FRONT_DEFROST_SWITCH(9),
    AC_REAR_DEFROST_SWITCH(10),
    AC_RECIRC_AIR(11),
    AC_DUAL(12),
    AC_FLOW_MODE(13),
    AC_TEMP_DEM(14),
    AC_HEAT_SWITCH(15),
    AC_TEMP_DEM_ADD(16),
    AC_TEMP_DEM_DEC(17),
    AC_BLOWER_DEM_ADD(18),
    AC_BLOWER_DEM_DEC(19),
    AC_ION_SWITCH(23),
    AC_PM_SWITCH(24),
    AC_AQS_SWITCH(25),
    AC_TEMP_OUTCAR(20),
    AC_PM_LEVEL(21),
    AC_RUNNING_STATE(22),
    AC_COMBINATION_FUNCTION_1(23),
    AC_COMBINATION_FUNCTION_2(24),
    AC_COMBINATION_FUNCTION_3(25),
    AC_COMBINATION_FUNCTION_4(26),
    AC_COMBINATION_FUNCTION_5(27),
    AC_COMBINATION_FUNCTION_6(28),
    AC_COMBINATION_FUNCTION_7(29),
    AC_COMBINATION_FUNCTION_8(30),
    AC_RAPID_COOLING_MODE(31),
    AC_ONE_BUTTON_WARMTH_MODE(32),
    AC_HAZE_MODE(33),
    AC_BABY_CARE_MODE(34),
    AC_AIR_CLEANER(35),
    AC_RAIN_SNOW_MODE(36),
    AC_SMOKING_MODE(37),
    AC_STOP_CAR_MODE(38),
    AC_REAR_BLOWER_SWITCH(39);

    public static final int AUTO_BLOWING_IN = 0;
    public static final int DEFAULT_LOOP = 0;
    public static final int DEFORST_AND_TO_DOWN_BLOWING_IN = 18;
    public static final int EXTERNAL_LOOP = 1;
    public static final int FLOW_MODE_DEF = 5;
    public static final int FLOW_MODE_FACE = 1;
    public static final int FLOW_MODE_FACE_LEG = 3;
    public static final int FLOW_MODE_LEG = 2;
    public static final int FLOW_MODE_LEG_DEF = 4;
    public static final int FLOW_MODE_NO = 0;
    public static final int FRONT_GLASS_BLOWING_IN = 1;
    public static final int INTERNAL_LOOP = 2;
    public static final int INVAILD_LOOP = 3;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    public static final int LEVEL_5 = 5;
    public static final int LEVEL_6 = 6;
    public static final int LEVEL_7 = 7;
    public static final int LEVEL_8 = 8;
    public static final int LEVEL_NO = 0;
    public static final int PARALLEL_AND_TO_UP_BLOWING_IN = 12;
    public static final int PARALLEL_BLOWING_IN = 4;
    public static final int SWITCH_OFF = 0;
    public static final int SWITCH_ON = 1;
    public static final int TO_DOWN_AND_PARALLEL_BLOWING_IN = 6;
    public static final int TO_DOWN_BLOWING_IN = 2;
    public static final int TO_UP_AND_PARALLEL_AND_TO_DOWN_BLOWING_IN = 14;
    public static final int TO_UP_AND_TO_DOWN_BLOWING_IN = 10;
    public static final int TO_UP_BLOWING_IN = 8;

    private int state;

    AirConditionState(int i) {
        this.state = i;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
        parcel.writeInt(this.state);
    }

    public static final Parcelable.Creator<AirConditionState> CREATOR = new Parcelable.Creator<AirConditionState>() {
        @Override
        public AirConditionState createFromParcel(Parcel parcel) {
            AirConditionState airConditionState = AirConditionState.values()[parcel.readInt()];
            airConditionState.state = parcel.readInt();
            return airConditionState;
        }

        @Override
        public AirConditionState[] newArray(int i) {
            return new AirConditionState[i];
        }
    };
}
