package com.qgcar.bus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representation of the vehicle Air Conditioning (AC) status.
 * Reconstructs precisely from the CanBus service parcel wire format.
 */
public final class AirCondition implements Parcelable {
    public static final int AIR_AVN_DISPLAY = 1;
    public static final int AIR_AVN_ERROR_DISPLAY = 3;
    public static final int AIR_AVN_NOT_DISPLAY = 0;
    public static final int AIR_CIRCULATION_AUTO = 255;
    public static final int AIR_CIRCULATION_ERROR = 3;
    public static final int AIR_CIRCULATION_INNER = 2;
    public static final int AIR_CIRCULATION_NONE = 0;
    public static final int AIR_CIRCULATION_OUTER = 1;
    public static final int AIR_DISPLAY_MODE_DEFROST = 5;
    public static final int AIR_DISPLAY_MODE_FACE = 1;
    public static final int AIR_DISPLAY_MODE_FACE_FOOT = 2;
    public static final int AIR_DISPLAY_MODE_FOOT = 3;
    public static final int AIR_DISPLAY_MODE_FOOT_DEFROST = 4;
    public static final int AIR_MODE_AUTO = 0;
    public static final int AIR_MODE_MANUAL = 1;
    public static final int AIR_SUPPLY_AUTO = 1;
    public static final int AIR_SUPPLY_DOWN = 3;
    public static final int AIR_SUPPLY_DOWN_PARALLEL = 4;
    public static final int AIR_SUPPLY_DOWN_UP = 8;
    public static final int AIR_SUPPLY_DOWN_UP_PARALLEL = 9;
    public static final int AIR_SUPPLY_FRONT = 2;
    public static final int AIR_SUPPLY_PARALLEL = 5;
    public static final int AIR_SUPPLY_UP = 6;
    public static final int AIR_SUPPLY_UP_PARALLEL = 7;
    public static final int AIR_SW_OFF = 0;
    public static final int AIR_SW_ON = 1;
    public static final int AIR_TEMP_HIGH = 32;
    public static final int AIR_TEMP_INVALID = 0;
    public static final int AIR_TEMP_LOW = 16;
    public static final int AirTempHigh = 127;
    public static final int AirTempInvalid = 255;
    public static final int AirTempLow = 0;
    public static final int AutoCirculation = 3;
    public static final int ExternalCirculation = 1;
    public static final int IGN_FLAG_OFF = 0;
    public static final int IGN_FLAG_ON = 1;
    public static final int IONS_STATE_HIGH = 2;
    public static final int IONS_STATE_LOW = 0;
    public static final int IONS_STATE_MIDDLE = 1;
    public static final int InternalCirculation = 2;
    public static final int NoDisplay = 0;
    public static final int OneHeating = 1;
    public static final int ThreeHeating = 3;
    public static final int TwoHeating = 2;
    public static final int noDisplay = 0;

    private int airSWStatus = -1;
    private int airACStatus = -1;
    private int airHighWindStatus = -1;
    private int airLowWindStatus = -1;
    private int airDUALStatus = -1;
    private int airMaxFrontStatus = -1;
    private int airRearLightStatus = -1;
    private int airSupplyStatus = -1;
    private int airDisplaySW = -1;
    private int airWindSpeed = -1;
    private float airLeftTemperature = -1.0f;
    private float airRightTemperature = -1.0f;
    private float airRearTemperature = -1.0f;
    private int airCirculationMode = -1;
    private int airLeftSeatHeatingLevel = -1;
    private int airRightSeatHeatingLevel = -1;
    private int airRearCtlLockSW = -1;
    private int airACMaxSW = -1;
    private int airRearWindowHeatingStatus = -1;
    private int airECO = -1;
    private int airFrontWindowDefogger = -1;
    private int airMode = -1;
    private int airCirculationStatus = -1;
    private int airRearWindSpeed = -1;
    private int airRearDownSupplyStatus = -1;
    private int airRearParallelSupplyStatus = -1;
    private int airRearUpSupplyStatus = -1;
    private int airRearWindowDefogger = -1;
    private int airRearWindowDefoggerEngine = -1;
    private int airLeftMirrorDefogger = -1;
    private int airRightMirrorDefogger = -1;
    private int airSyncStatus = -1;
    private int airDisplay_mode = -1;
    private int IGN_ON = -1;
    private float airTempInCar = -1.0f;
    private float airTempOutCar = -1.0f;
    private int displaypop = -1;
    private int ions_switch = -1;
    private int ions_state = -1;
    private int ac_power_switch = -1;
    private int airOffWindSpeed = -1;
    private int airTimeOut = 0;
    private int airTempDemLevel = -1;

    private int airPm2_5 = -1;
    private float airInCarPM25 = -1.0f;
    private int airPmLevel = -1;
    private int airInCarPM25Level = -1;
    private int airPMStatus = -1;
    private float airOurCarPM25 = -1.0f;
    private float airOutCarPM25 = -1.0f;
    private int airOutCarPM25Level = -1;
    private int airRearBlowerStatus = -1;
    private int airRemoteControlFailState = -1;
    private int airWTCWorkStatus = -1;
    private int airWTCErrorStatus = -1;
    private int airWTCLimitStart = -1;
    private int airEASWorkStatus = -1;
    private int airEASErrorStatus = -1;
    private int airEASLimitStart = -1;
    private int airAQSStatus = -1;

    public AirCondition() {}

    public static final Parcelable.Creator<AirCondition> CREATOR = new Parcelable.Creator<AirCondition>() {
        @Override
        public AirCondition createFromParcel(Parcel parcel) {
            AirCondition airCondition = new AirCondition();
            airCondition.airSWStatus = parcel.readInt();
            airCondition.airACStatus = parcel.readInt();
            airCondition.airHighWindStatus = parcel.readInt();
            airCondition.airLowWindStatus = parcel.readInt();
            airCondition.airDUALStatus = parcel.readInt();
            airCondition.airMaxFrontStatus = parcel.readInt();
            airCondition.airRearLightStatus = parcel.readInt();
            airCondition.airSupplyStatus = parcel.readInt();
            airCondition.airDisplaySW = parcel.readInt();
            airCondition.airWindSpeed = parcel.readInt();
            airCondition.airLeftTemperature = parcel.readFloat();
            airCondition.airRightTemperature = parcel.readFloat();
            airCondition.airRearTemperature = parcel.readFloat();
            airCondition.airCirculationMode = parcel.readInt();
            airCondition.airLeftSeatHeatingLevel = parcel.readInt();
            airCondition.airRearCtlLockSW = parcel.readInt();
            airCondition.airACMaxSW = parcel.readInt();
            airCondition.airRightSeatHeatingLevel = parcel.readInt();
            airCondition.airRearWindowHeatingStatus = parcel.readInt();
            airCondition.airECO = parcel.readInt();
            airCondition.airFrontWindowDefogger = parcel.readInt();
            airCondition.airMode = parcel.readInt();
            airCondition.airCirculationStatus = parcel.readInt();
            airCondition.airRearWindSpeed = parcel.readInt();
            airCondition.airRearDownSupplyStatus = parcel.readInt();
            airCondition.airRearParallelSupplyStatus = parcel.readInt();
            airCondition.airRearUpSupplyStatus = parcel.readInt();
            airCondition.airRearWindowDefogger = parcel.readInt();
            airCondition.airLeftMirrorDefogger = parcel.readInt();
            airCondition.airRightMirrorDefogger = parcel.readInt();
            airCondition.airSyncStatus = parcel.readInt();
            airCondition.airDisplay_mode = parcel.readInt();
            airCondition.IGN_ON = parcel.readInt();
            airCondition.airTempInCar = parcel.readFloat();
            airCondition.airTempOutCar = parcel.readFloat();
            airCondition.ions_state = parcel.readInt();
            airCondition.ions_switch = parcel.readInt();
            airCondition.ac_power_switch = parcel.readInt();
            airCondition.airPm2_5 = parcel.readInt();
            airCondition.airAQSStatus = parcel.readInt();
            airCondition.airPMStatus = parcel.readInt();
            airCondition.displaypop = parcel.readInt();
            airCondition.airRearBlowerStatus = parcel.readInt();
            airCondition.airPmLevel = parcel.readInt();
            airCondition.airInCarPM25 = parcel.readFloat();
            airCondition.airInCarPM25Level = parcel.readInt();
            airCondition.airOurCarPM25 = parcel.readFloat();
            airCondition.airOutCarPM25 = parcel.readFloat();
            airCondition.airOutCarPM25Level = parcel.readInt();
            airCondition.airTempDemLevel = parcel.readInt();
            airCondition.airOffWindSpeed = parcel.readInt();
            airCondition.airTimeOut = parcel.readInt();
            return airCondition;
        }

        @Override
        public AirCondition[] newArray(int i) {
            return new AirCondition[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.airSWStatus);
        parcel.writeInt(this.airACStatus);
        parcel.writeInt(this.airHighWindStatus);
        parcel.writeInt(this.airLowWindStatus);
        parcel.writeInt(this.airDUALStatus);
        parcel.writeInt(this.airMaxFrontStatus);
        parcel.writeInt(this.airRearLightStatus);
        parcel.writeInt(this.airSupplyStatus);
        parcel.writeInt(this.airDisplaySW);
        parcel.writeInt(this.airWindSpeed);
        parcel.writeFloat(this.airLeftTemperature);
        parcel.writeFloat(this.airRightTemperature);
        parcel.writeFloat(this.airRearTemperature);
        parcel.writeInt(this.airCirculationMode);
        parcel.writeInt(this.airLeftSeatHeatingLevel);
        parcel.writeInt(this.airRearCtlLockSW);
        parcel.writeInt(this.airACMaxSW);
        parcel.writeInt(this.airRightSeatHeatingLevel);
        parcel.writeInt(this.airRearWindowHeatingStatus);
        parcel.writeInt(this.airECO);
        parcel.writeInt(this.airFrontWindowDefogger);
        parcel.writeInt(this.airMode);
        parcel.writeInt(this.airCirculationStatus);
        parcel.writeInt(this.airRearWindSpeed);
        parcel.writeInt(this.airRearDownSupplyStatus);
        parcel.writeInt(this.airRearParallelSupplyStatus);
        parcel.writeInt(this.airRearUpSupplyStatus);
        parcel.writeInt(this.airRearWindowDefogger);
        parcel.writeInt(this.airLeftMirrorDefogger);
        parcel.writeInt(this.airRightMirrorDefogger);
        parcel.writeInt(this.airSyncStatus);
        parcel.writeInt(this.airDisplay_mode);
        parcel.writeInt(this.IGN_ON);
        parcel.writeFloat(this.airTempInCar);
        parcel.writeFloat(this.airTempOutCar);
        parcel.writeInt(this.ions_state);
        parcel.writeInt(this.ions_switch);
        parcel.writeInt(this.ac_power_switch);
        parcel.writeInt(this.airPm2_5);
        parcel.writeInt(this.airAQSStatus);
        parcel.writeInt(this.airPMStatus);
        parcel.writeInt(this.displaypop);
        parcel.writeInt(this.airRearBlowerStatus);
        parcel.writeInt(this.airPmLevel);
        parcel.writeFloat(this.airInCarPM25);
        parcel.writeInt(this.airInCarPM25Level);
        parcel.writeFloat(this.airOurCarPM25);
        parcel.writeFloat(this.airOutCarPM25);
        parcel.writeInt(this.airOutCarPM25Level);
        parcel.writeInt(this.airTempDemLevel);
        parcel.writeInt(this.airOffWindSpeed);
        parcel.writeInt(this.airTimeOut);
    }

    // Getters and Setters

    public int getAirTempDemLevel() { return this.airTempDemLevel; }
    public void setAirTempDemLevel(int i) { this.airTempDemLevel = i; }

    public int getAirRearBlowerStatus() { return this.airRearBlowerStatus; }
    public void setAirRearBlowerStatus(int i) { this.airRearBlowerStatus = i; }

    public int getAirPMStatus() { return this.airPMStatus; }
    public void setAirPMStatus(int i) { this.airPMStatus = i; }

    public int getAirAQSStatus() { return this.airAQSStatus; }
    public void setAirAQSStatus(int i) { this.airAQSStatus = i; }

    public float getAirInCarPM25() { return this.airInCarPM25; }
    public void setAirInCarPM25(float f) { this.airInCarPM25 = f; }

    public int getAirInCarPM25Level() { return this.airInCarPM25Level; }
    public void setAirInCarPM25Level(int i) { this.airInCarPM25Level = i; }

    public float getAirOurCarPM25() { return this.airOurCarPM25; }
    public void setAirOurCarPM25(float f) { this.airOurCarPM25 = f; }

    public float getAirOutCarPM25() { return this.airOutCarPM25; }
    public void setAirOutCarPM25(float f) { this.airOutCarPM25 = f; }

    public int getAirOutCarPM25Level() { return this.airOutCarPM25Level; }
    public void setAirOutCarPM25Level(int i) { this.airOutCarPM25Level = i; }

    public int getAirPm2_5() { return this.airPm2_5; }
    public void setAirPm2_5(int i) { this.airPm2_5 = i; this.airInCarPM25 = i; }

    public int getAirPmLevel() { return this.airInCarPM25Level; }
    public void setAirPmLevel(int i) { this.airPmLevel = i; this.airInCarPM25Level = i; }

    public void setAirDisplayMode(int i) { this.airDisplay_mode = i; }
    public int getAirDisplayMode() { return this.airDisplay_mode; }

    public void setIGN_ON(int i) { this.IGN_ON = i; }
    public int getIGN_ON() { return this.IGN_ON; }

    public void setAirTempInCar(float f) { this.airTempInCar = f; }
    public float getAirTempInCar() { return this.airTempInCar; }

    public void setAirTempOutCar(float f) { this.airTempOutCar = f; }
    public float getAirTempOutCar() { return this.airTempOutCar; }

    public void setAirSWStatus(int i) { this.airSWStatus = i; }
    public int getAirSWStatus() { return this.airSWStatus; }

    public float getAirRightTemperature() { return this.airRightTemperature; }
    public void setAirRightTemperature(float f) { this.airRightTemperature = f; }

    public float getAirLeftTemperature() { return this.airLeftTemperature; }
    public void setAirLeftTemperature(float f) { this.airLeftTemperature = f; }

    public int getAirWindSpeed() { return this.airWindSpeed; }
    public void setAirWindSpeed(int i) { this.airWindSpeed = i; }

    public int getAirOffWindSpeed() { return this.airOffWindSpeed; }
    public void setAirOffWindSpeed(int i) { this.airOffWindSpeed = i; }

    public int getAirMode() { return this.airMode; }
    public void setAirMode(int i) { this.airMode = i; }

    public int getAirFrontWindowDefogger() { return this.airFrontWindowDefogger; }
    public void setAirFrontWindowDefogger(int i) { this.airFrontWindowDefogger = i; }

    public int getAirECO() { return this.airECO; }
    public void setAirECO(int i) { this.airECO = i; }

    public int getAirDUALStatus() { return this.airDUALStatus; }
    public void setAirDUALStatus(int i) { this.airDUALStatus = i; }

    public int getIons_switch() { return this.ions_switch; }
    public void setIons_switch(int i) { this.ions_switch = i; }

    public int getIons_state() { return this.ions_state; }
    public void setIons_state(int i) { this.ions_state = i; }

    public int getAc_power_switch() { return this.ac_power_switch; }
    public void setAc_power_switch(int i) { this.ac_power_switch = i; }

    public int getAirCirculationMode() { return this.airCirculationMode; }
    public void setAirCirculationMode(int i) { this.airCirculationMode = i; }

    public int getAirACStatus() { return this.airACStatus; }
    public void setAirACStatus(int i) { this.airACStatus = i; }

    public int getAirdisplaypop() { return this.displaypop; }
    public void setAirdisplaypop(int i) { this.displaypop = i; }

    public int getAirHighWindStatus() { return this.airHighWindStatus; }
    public void setAirHighWindStatus(int i) { this.airHighWindStatus = i; }

    public int getAirLowWindStatus() { return this.airLowWindStatus; }
    public void setAirLowWindStatus(int i) { this.airLowWindStatus = i; }

    public int getAirMaxFrontStatus() { return this.airMaxFrontStatus; }
    public void setAirMaxFrontStatus(int i) { this.airMaxFrontStatus = i; }

    public int getAirRearLightStatus() { return this.airRearLightStatus; }
    public void setAirRearLightStatus(int i) { this.airRearLightStatus = i; }

    public int getAirSupplyStatus() { return this.airSupplyStatus; }
    public void setAirSupplyStatus(int i) { this.airSupplyStatus = i; }

    public int getAirWindSupplyFlagUp() { return this.airSupplyStatus == 6 ? 1 : 0; }
    public int getAirWindSupplyFlagDown() { return this.airSupplyStatus == 3 ? 1 : 0; }
    public int getAirWindSupplyFlagParallel() { return this.airSupplyStatus == 5 ? 1 : 0; }
    public int getAirWindSupplyFlagUpParallel() { return this.airSupplyStatus == 7 ? 1 : 0; }
    public int getAirWindSupplyFlagUpDown() { return this.airSupplyStatus == 8 ? 1 : 0; }
    public int getAirWindSupplyFlagUpDownParallel() { return this.airSupplyStatus == 9 ? 1 : 0; }
    public int getAirWindSupplyFlagDownParallel() { return this.airSupplyStatus == 4 ? 1 : 0; }
    public int getAirWindSupplyFlagFront() { return this.airSupplyStatus == 2 ? 1 : 0; }

    public int getAirRearWindowDefogger() { return this.airRearWindowDefogger; }
    public void setAirRearWindowDefogger(int i) { this.airRearWindowDefogger = i; }

    public int getAirRearWindowDefoggerEngine() { return this.airRearWindowDefoggerEngine; }
    public void setAirRearWindowDefoggerEngine(int i) { this.airRearWindowDefoggerEngine = i; }

    public int getAirSyncStatus() { return this.airSyncStatus; }
    public void setAirSyncStatus(int i) { this.airSyncStatus = i; }

    public int getAirLeftMirrorDefogger() { return this.airLeftMirrorDefogger; }
    public void setAirLeftMirrorDefogger(int i) { this.airLeftMirrorDefogger = i; }

    public int getAirRightMirrorDefogger() { return this.airRightMirrorDefogger; }
    public void setAirRightMirrorDefogger(int i) { this.airRightMirrorDefogger = i; }

    public int getAirUpSupplyStatus() {
        return (this.airSupplyStatus == 6 || this.airSupplyStatus == 7 || this.airSupplyStatus == 8 || this.airSupplyStatus == 9) ? 1 : 0;
    }

    public int getAirDownSupplyStatus() {
        return (this.airSupplyStatus == 3 || this.airSupplyStatus == 4 || this.airSupplyStatus == 8 || this.airSupplyStatus == 9) ? 1 : 0;
    }

    public int getAirParallelStatus() {
        return (this.airSupplyStatus == 5 || this.airSupplyStatus == 7 || this.airSupplyStatus == 4 || this.airSupplyStatus == 9) ? 1 : 0;
    }

    public int getAirDisplaySW() { return this.airDisplaySW; }
    public void setAirDisplaySW(int i) { this.airDisplaySW = i; }

    public int getAirLeftSeatHeatingLevel() { return this.airLeftSeatHeatingLevel; }
    public void setAirLeftSeatHeatingLevel(int i) { this.airLeftSeatHeatingLevel = i; }

    public int getAirRightSeatHeatingLevel() { return this.airRightSeatHeatingLevel; }
    public void setAirRightSeatHeatingLevel(int i) { this.airRightSeatHeatingLevel = i; }

    public int getAirRearCtlLockSW() { return this.airRearCtlLockSW; }
    public void setAirRearCtlLockSW(int i) { this.airRearCtlLockSW = i; }

    public int getAirACMaxSW() { return this.airACMaxSW; }
    public void setAirACMaxSW(int i) { this.airACMaxSW = i; }

    public int getAirRearWindowHeatingStatus() { return this.airRearWindowHeatingStatus; }
    public void setAirRearWindowHeatingStatus(int i) { this.airRearWindowHeatingStatus = i; }

    public int getAirCirculationStatus() { return this.airCirculationStatus; }
    public void setAirCirculationStatus(int i) { this.airCirculationStatus = i; }

    public float getAirRearTemperature() { return this.airRearTemperature; }
    public void setAirRearTemperature(float f) { this.airRearTemperature = f; }

    public int getAirRearWindSpeed() { return this.airRearWindSpeed; }
    public void setAirRearWindSpeed(int i) { this.airRearWindSpeed = i; }

    public int getAirRearDownSupplyStatus() { return this.airRearDownSupplyStatus; }
    public void setAirRearDownSupplyStatus(int i) { this.airRearDownSupplyStatus = i; }

    public int getAirRearParallelSupplyStatus() { return this.airRearParallelSupplyStatus; }
    public void setAirRearParallelSupplyStatus(int i) { this.airRearParallelSupplyStatus = i; }

    public int getAirRearUpSupplyStatus() { return this.airRearUpSupplyStatus; }
    public void setAirRearUpSupplyStatus(int i) { this.airRearUpSupplyStatus = i; }

    public int getAirWTCWorkStatus() { return this.airWTCWorkStatus; }
    public void setAirWTCWorkStatus(int i) { this.airWTCWorkStatus = i; }

    public int getAirWTCErrorStatus() { return this.airWTCErrorStatus; }
    public void setAirWTCErrorStatus(int i) { this.airWTCErrorStatus = i; }

    public int getAirWTCLimitStart() { return this.airWTCLimitStart; }
    public void setAirWTCLimitStart(int i) { this.airWTCLimitStart = i; }

    public int getAirEASWorkStatus() { return this.airEASWorkStatus; }
    public void setAirEASWorkStatus(int i) { this.airEASWorkStatus = i; }

    public int getAirEASErrorStatus() { return this.airEASErrorStatus; }
    public void setAirEASErrorStatus(int i) { this.airEASErrorStatus = i; }

    public int getAirEASLimitStart() { return this.airEASLimitStart; }
    public void setAirEASLimitStart(int i) { this.airEASLimitStart = i; }

    public void setEccTimeOutState(int i) { this.airTimeOut = i; }
    public int getEccTimeOutState() { return this.airTimeOut; }
}
