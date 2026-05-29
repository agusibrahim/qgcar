package com.qgcar.bus;

/**
 * AIDL transaction codes (callback side) for ICanBusServiceCallback.
 * Decoded from FactoryTest jadx — keep in sync with vendor service.
 */
public final class CanBusCodes {
    private CanBusCodes() {}

    public static final int onDoorStatusChanged                  = 1;
    public static final int onCanBoxVersionChange                = 2;
    public static final int onSeatBeltChanged                    = 3;
    public static final int onAirConditionChanged                = 4;
    public static final int onDVRStateChenaged                   = 5;
    public static final int onRadarDataChanged                   = 6;
    public static final int onParkStateChanged                   = 7;
    public static final int onReTrackingChanged                  = 8;
    public static final int onHandBrakeStatusChanged             = 9;
    public static final int onLightStatusChanged                 = 10;
    public static final int onEngineFluidStatusChanged           = 11;
    public static final int onGearStatusChanged                  = 12;
    public static final int onEngineSpeedChanged                 = 13;
    public static final int onAmbientTemperatureChanged          = 14;
    public static final int onWheelSpeedChanged                  = 15;
    public static final int onVehicleSpeedChanged                = 16;
    public static final int onFuelLevelChanged                   = 17;
    public static final int onWheelCountChanged                  = 18;
    public static final int onEngineTemperatureChanged           = 19;
    public static final int onBrakePadStatusChanged              = 20;
    public static final int onBrakeFluidStatusChanged            = 21;
    public static final int onWiperFluidStatusChanged            = 22;
    public static final int onFuelConsumptionChanged             = 23;
    public static final int onEngineStatusChanged                = 24;
    public static final int onOdometerChanged                    = 25;
    public static final int onBatteryStateChanged                = 26;
    public static final int onCarKeyChanged                      = 27;
    public static final int onIlluminationChanged                = 28;
    public static final int onVehicleIOChanged                   = 29;
    public static final int onSecondaryOdometerChanged           = 30;
    public static final int onVehicleKeyStateChanged             = 31;
    public static final int onWindowsStatusChanged               = 32;
    public static final int onAlarmDataChanged                   = 33;
    public static final int onSWCAngleChanged                    = 34;
    public static final int onHEVSystemModelChanged              = 35;
    public static final int onVehicleStateChanged                = 36;
    public static final int onVehicleCenterControlEnabledChanged = 37;
    public static final int onTravellingInfo                     = 38;
    public static final int onAccStateChanged                    = 39;
    public static final int onRealityWarningInfoChange           = 43;
    public static final int onTPMSInfoChange                     = 45;
    public static final int onPowerLevelChanged                  = 47;
    public static final int onInstantaneousFuelConsumptionChanged= 48;
    public static final int onRainFallLevelChanged               = 49;
    public static final int onVehicleSceneModeChanged            = 50;
    public static final int onCanRawDataChanged                  = 51;
    public static final int onInstrumentClusterInfoChanged       = 56;
    public static final int onVehicleInfoChanged                 = 58;

    /** Service-side transaction code for addCallback(callback). */
    public static final int TXN_addCallback = 28;

    public static String nameOf(int code) {
        switch (code) {
            case 1: return "onDoorStatusChanged";
            case 2: return "onCanBoxVersionChange";
            case 3: return "onSeatBeltChanged";
            case 4: return "onAirConditionChanged";
            case 5: return "onDVRStateChenaged";
            case 6: return "onRadarDataChanged";
            case 7: return "onParkStateChanged";
            case 8: return "onReTrackingChanged";
            case 9: return "onHandBrakeStatusChanged";
            case 10: return "onLightStatusChanged";
            case 11: return "onEngineFluidStatusChanged";
            case 12: return "onGearStatusChanged";
            case 13: return "onEngineSpeedChanged";
            case 14: return "onAmbientTemperatureChanged";
            case 15: return "onWheelSpeedChanged";
            case 16: return "onVehicleSpeedChanged";
            case 17: return "onFuelLevelChanged";
            case 19: return "onEngineTemperatureChanged";
            case 24: return "onEngineStatusChanged";
            case 25: return "onOdometerChanged";
            case 28: return "onIlluminationChanged";
            case 31: return "onVehicleKeyStateChanged";
            case 32: return "onWindowsStatusChanged";
            case 34: return "onSWCAngleChanged";
            case 39: return "onAccStateChanged";
            case 51: return "onCanRawDataChanged";
            default: return "code_" + code;
        }
    }
}
