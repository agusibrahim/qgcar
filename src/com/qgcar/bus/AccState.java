package com.qgcar.bus;

/**
 * ACC (accessory) state. Values from com.qinggan.canbus.VehicleInfo:
 *   OFF=0, ACC=1, ON=2, START=3.
 *
 * - OFF   : ignition fully off
 * - ACC   : key in ACC position (radio/HU powered, engine off)
 * - ON    : key in ON position (instrument cluster powered)
 * - START : starter engaged
 */
public enum AccState {
    OFF(0),
    ACC(1),
    ON(2),
    START(3),
    UNKNOWN(-9999);

    public final int raw;
    AccState(int raw) { this.raw = raw; }

    public static AccState fromRaw(int v) {
        switch (v) {
            case 0: return OFF;
            case 1: return ACC;
            case 2: return ON;
            case 3: return START;
            default: return UNKNOWN;
        }
    }
}
