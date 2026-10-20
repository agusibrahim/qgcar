package com.qgcar.bus;

import android.os.Parcel;

/**
 * Phone call information sent to the vehicle's Instrument Cluster (MID / Speedometer).
 * Serializes to the exact wire format expected by {@code com.qinggan.canbus.PhoneSrcInfo}.
 */
public final class PhoneClusterInfo {

    public enum CallState {
        NOTHING(0),
        COMING_CALL(1),
        HOLD(2),
        HANG_UP(3),
        GOING_CALL(4),
        CONNECTED(5);

        public final int value;
        CallState(int value) { this.value = value; }
    }

    public String phoneNum = "";
    public String name = "";
    public int durationSeconds;
    public CallState phoneState = CallState.NOTHING;
    public String deviceName = "";
    public int connectedStatus = 1;
    public int isVehicleCall;
    public boolean syncFromACC;

    public PhoneClusterInfo() {}

    public PhoneClusterInfo(String phoneNum, String name, CallState state) {
        this.phoneNum = phoneNum;
        this.name = name;
        this.phoneState = state;
    }

    public PhoneClusterInfo setPhoneNum(String n) { this.phoneNum = n; return this; }
    public PhoneClusterInfo setName(String n) { this.name = n; return this; }
    public PhoneClusterInfo setDurationSeconds(int d) { this.durationSeconds = d; return this; }
    public PhoneClusterInfo setPhoneState(CallState s) { this.phoneState = s; return this; }
    public PhoneClusterInfo setDeviceName(String d) { this.deviceName = d; return this; }
    public PhoneClusterInfo setConnectedStatus(int s) { this.connectedStatus = s; return this; }
    public PhoneClusterInfo setVehicleCall(int v) { this.isVehicleCall = v; return this; }
    public PhoneClusterInfo setSyncFromACC(boolean b) { this.syncFromACC = b; return this; }

    public void writeToVendorParcel(Parcel p) {
        p.writeString(phoneNum);
        p.writeString(name);
        p.writeInt(durationSeconds);
        if (phoneState == null) {
            p.writeString(null);
        } else {
            p.writeString("com.qinggan.canbus.PhoneState");
            p.writeInt(phoneState.ordinal());
            p.writeInt(phoneState.value);
        }
        p.writeString(deviceName);
        p.writeInt(connectedStatus);
        p.writeInt(isVehicleCall);
        p.writeInt(syncFromACC ? 1 : 0);
    }
}
