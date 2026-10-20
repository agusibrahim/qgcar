package com.qgcar.signal;

/**
 * Listener for MCU / Head-Unit hardware signal events from {@code CarSignalService}.
 * Override only the callbacks you care about.
 */
public abstract class CarSignalListener {
    public void onKeyEvent(int keyCode, int keyAction) {}
    public void onBatteryChanged(int level) {}
    public void onPowerStateChanged(int state) {}
    public void onAccStateChanged(int state) {}
    public void onPowerKeyStateChanged(int state) {}
    public void onPADistortionStateChanged(int state) {}
    public void onHeadLightStateChanged(int state) {}
    public void onCameraStateChanged(int state) {}
    public void onCarSafeStateChanged(int state) {}
    public void onAVINStateChanged(int state) {}
    public void onTouchScreenStateChanged(int state) {}
    /** Mainboard thermal level changed (4 = high-temp warning on statusbar). */
    public void onTemValueChanged(int tempLevel) {}
    public void onLightSensorChanged(int level) {}
    public void onVolumeKeyChanged(int state) {}
    public void onMuteKeyChanged(int state) {}
    public void onLightKeyChanged(int state) {}
    /** LCD temperature level changed. */
    public void onLCDTempChanged(int lcdTemp) {}
    public void onCanBusStateChanged(int state) {}
    public void onVoltageStateChanged(int voltageState) {}
    public void onKeyChannelLine1ValueChanged(int value) {}
    public void onKeyChannelLine2ValueChanged(int value) {}
    public void onBackLightStateChanged(int state) {}
    public void onMCUDiagRequest(int req) {}
    public void onBLEConnectStatusChanged(int status) {}
    public void onRawEvent(int code, String name, byte[] payload) {}
}
