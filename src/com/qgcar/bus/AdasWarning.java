package com.qgcar.bus;

/** ADAS (Advanced Driver Assistance) warning info. */
public final class AdasWarning {
    public final int fcwState;       // Forward Collision Warning: 0=off, 1=on, 2=warning
    public final int aebState;       // Autonomous Emergency Braking
    public final int ldwState;       // Lane Departure Warning: 0=off, 1=left, 2=right
    public final int bsdState;       // Blind Spot Detection: 0=off, 1=left, 2=right
    public final int rctaState;      // Rear Cross Traffic Alert
    public final int tsrSpeed;       // Traffic Sign Recognition speed limit
    public final int tsrUnit;        // 0=km/h, 1=mph

    public AdasWarning(int fcw, int aeb, int ldw, int bsd, int rcta, int tsrSpeed, int tsrUnit) {
        this.fcwState = fcw;
        this.aebState = aeb;
        this.ldwState = ldw;
        this.bsdState = bsd;
        this.rctaState = rcta;
        this.tsrSpeed = tsrSpeed;
        this.tsrUnit = tsrUnit;
    }

    @Override public String toString() {
        return String.format("AdasWarning{FCW=%d AEB=%d LDW=%d BSD=%d TSR=%d}",
            fcwState, aebState, ldwState, bsdState, tsrSpeed);
    }
}
