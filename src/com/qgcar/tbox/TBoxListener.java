package com.qgcar.tbox;

/**
 * Listener for Telematics Box (T-Box) events from {@code TBoxService}.
 */
public abstract class TBoxListener {
    public void onNetworkStateChanged(int netType, String operator, String ip, int status) {}
    public void onNetworkSignalStrengthChanged(int signalBars) {}
    public void onCallStateChanged(int callType, int callState, int callbackMode) {}
    public void onTxRxChanged(long txBytes, long rxBytes) {}
    public void onPoiNotify(String name, double lon, double lat) {}
    public void onCheckFOTAResponse(int status) {}
    public void onUpgradeStateChanged(int state) {}
    public void onUpgradeResult(boolean success) {}
    public void onDatacomSwitchStateChanged(boolean enabled) {}
    public void onWlanSwitchStateChanged(boolean enabled) {}
    public void onTBoxConnectedStateChanged(boolean connected) {}
}
