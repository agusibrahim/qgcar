package com.qgcar.bus;

/**
 * Door / hood / trunk state. From com.qinggan.canbus.DoorStatus:
 *   CLOSED = 0, OPEN = 1, UNKNOWN = -1.
 */
public enum DoorState {
    CLOSED(0),
    OPEN(1),
    UNKNOWN(-1);

    public final int raw;
    DoorState(int raw) { this.raw = raw; }

    public static DoorState fromRaw(int v) {
        switch (v) {
            case 0: return CLOSED;
            case 1: return OPEN;
            default: return UNKNOWN;
        }
    }
}
