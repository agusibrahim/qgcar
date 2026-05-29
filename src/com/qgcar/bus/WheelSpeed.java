package com.qgcar.bus;

/** Per-wheel speed from ABS/ESP system. */
public final class WheelSpeed {
    public final float frontLeft;   // km/h
    public final float frontRight;
    public final float rearLeft;
    public final float rearRight;

    public WheelSpeed(float fl, float fr, float rl, float rr) {
        this.frontLeft = fl;
        this.frontRight = fr;
        this.rearLeft = rl;
        this.rearRight = rr;
    }

    @Override public String toString() {
        return String.format("WheelSpeed{FL=%.1f FR=%.1f RL=%.1f RR=%.1f}",
            frontLeft, frontRight, rearLeft, rearRight);
    }
}
