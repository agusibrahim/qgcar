package com.qgcar.bus;

/**
 * Vehicle scene modes that trigger preset vehicle behaviours.
 * Sent via {@link QGBus#setVehicleSceneMode(SceneMode)}.
 */
public enum SceneMode {
    /** Base / Off — reset to default state. */
    BASE(0),
    /** Cool mode. */
    COOL(1),
    /** Rain / Snow — auto-close all windows. */
    RAIN_SNOW(6),
    /** Smoke — open windows for ventilation. */
    SMOKE(7),
    /** Sky — full sunroof open. */
    SKY(13);

    private final int value;

    SceneMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
