package com.qgcar.bus;

/**
 * AIDL transaction codes for ICarSignalServiceCallBack.
 * CarSignalService is an alternate channel for some events (notably keys).
 */
public final class CarSignalCodes {
    private CarSignalCodes() {}

    public static final int onKeyEvent                  = 1;
    public static final int onBatteryChanged            = 2;
    public static final int onPowerStateChanged         = 3;
    public static final int onACCStateChanged           = 4;
    public static final int onPowerKeyStateChanged      = 5;
    public static final int onPADistortionStateChanged  = 6;
    public static final int onHeadLightStateChanged     = 7;
    public static final int onCameraStateChanged        = 8;
    public static final int onCarSafeStateChanged       = 9;
    public static final int onAVINStateChanged          = 10;
    public static final int onTouchScreenStateChanged   = 11;
    public static final int onTemValueChanged           = 12;
    public static final int onLightSensorChanged        = 13;
    public static final int onVolumeKeyChanged          = 14;
    public static final int onMuteKeyChanged            = 15;
    public static final int onLightKeyChanged           = 16;
    public static final int onLCDTempChanged            = 17;
    public static final int onCanBusStateChanged        = 18;
    public static final int onVoltageStateChanged       = 19;
    public static final int onKeyChannelLine1ValueChanged = 20;
    public static final int onKeyChannelLine2ValueChanged = 21;
    public static final int OnBackLightStateChanged     = 22;
    public static final int onMCUDiagRequest            = 23;
    public static final int onMCUDiagRequestEx          = 24;
    public static final int onBLEConnectStatusChanged   = 25;

    /** Service-side transaction code for registerCallback(callback). */
    public static final int TXN_registerCallback = 43;
    /** Service-side transaction code for unregisterCallback(callback). */
    public static final int TXN_unregisterCallback = 44;

    public static String nameOf(int code) {
        switch (code) {
            case 1: return "onKeyEvent";
            case 2: return "onBatteryChanged";
            case 3: return "onPowerStateChanged";
            case 4: return "onACCStateChanged";
            case 5: return "onPowerKeyStateChanged";
            case 6: return "onPADistortionStateChanged";
            case 7: return "onHeadLightStateChanged";
            case 8: return "onCameraStateChanged";
            case 9: return "onCarSafeStateChanged";
            case 10: return "onAVINStateChanged";
            case 11: return "onTouchScreenStateChanged";
            case 12: return "onTemValueChanged";
            case 13: return "onLightSensorChanged";
            case 14: return "onVolumeKeyChanged";
            case 15: return "onMuteKeyChanged";
            case 16: return "onLightKeyChanged";
            case 17: return "onLCDTempChanged";
            case 18: return "onCanBusStateChanged";
            case 19: return "onVoltageStateChanged";
            case 20: return "onKeyChannelLine1ValueChanged";
            case 21: return "onKeyChannelLine2ValueChanged";
            case 22: return "OnBackLightStateChanged";
            case 23: return "onMCUDiagRequest";
            case 24: return "onMCUDiagRequestEx";
            case 25: return "onBLEConnectStatusChanged";
            default: return "code_" + code;
        }
    }
}
