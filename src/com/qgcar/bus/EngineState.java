package com.qgcar.bus;

/**
 * Engine running state. Values from com.qinggan.canbus.VehicleInfo:
 *   NO_RUNNING=0, RUNNING=1.
 */
public enum EngineState {
    NOT_RUNNING(0),
    RUNNING(1),
    UNKNOWN(-9999);

    public final int raw;
    EngineState(int raw) { this.raw = raw; }

    public static EngineState fromRaw(int v) {
        switch (v) {
            case 0: return NOT_RUNNING;
            case 1: return RUNNING;
            default: return UNKNOWN;
        }
    }
}
