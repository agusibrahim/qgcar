package com.qgcar.bus;

/**
 * Seatbelt state for one seat. From com.qinggan.canbus.SeatBelt:
 *   PULLED=0 (engaged/buckled), PUSHED=1 (released), UNSUPPORT=-1.
 *
 * Names match the vendor enum even though "PULLED" feels backwards.
 */
public enum SeatBeltState {
    BUCKLED(0),     // vendor: PULLED
    UNBUCKLED(1),   // vendor: PUSHED
    UNKNOWN(-1);    // vendor: UNSUPPORT

    public final int raw;
    SeatBeltState(int raw) { this.raw = raw; }

    public static SeatBeltState fromRaw(int v) {
        switch (v) {
            case 0: return BUCKLED;
            case 1: return UNBUCKLED;
            default: return UNKNOWN;
        }
    }
}
