package com.qgcar.keys;

/** Action types delivered by KeyManagerService. */
public final class QGKeyAction {
    private QGKeyAction() {}

    public static final int DOWN       = 4096;
    public static final int SHORT      = 8192;
    public static final int HOLD       = 16384;
    public static final int LONG       = 32768;
    public static final int UP         = 65536;
    public static final int DOUBLE     = 131072;
    public static final int HOLD_STOP  = 262144;
    public static final int LONG_STOP  = 524288;

    public static String name(int action) {
        switch (action) {
            case DOWN: return "DOWN";
            case SHORT: return "SHORT";
            case HOLD: return "HOLD";
            case LONG: return "LONG";
            case UP: return "UP";
            case DOUBLE: return "DOUBLE";
            case HOLD_STOP: return "HOLD_STOP";
            case LONG_STOP: return "LONG_STOP";
            default: return "ACT_" + action;
        }
    }
}
