package com.qgcar.bus;

/**
 * Navigation guidance state sent to the vehicle's instrument cluster (MID).
 * Wire ordinal matches {@code com.qinggan.canbus.GuideState}.
 */
public enum GuideState {
    START,
    AUTO_STOP,
    MAN_STOP,
    YAW_GUIDE,
    OVER_PASS,
    GUIDING,
    SIMULATING,
    ROAM,
    UNKNOWN
}
