package com.qgcar.bus;

/** Vehicle alarm data. */
public final class AlarmData {
    public final int alarmType;     // 0=none, 1=door, 2=window, 3=tilt, 4=intrusion
    public final int alarmState;    // 0=off, 1=triggered, 2=acknowledged

    public AlarmData(int type, int state) {
        this.alarmType = type;
        this.alarmState = state;
    }

    @Override public String toString() {
        return String.format("AlarmData{type=%d state=%d}", alarmType, alarmState);
    }
}
