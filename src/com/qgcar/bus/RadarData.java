package com.qgcar.bus;

/** Parking radar sensor distances. */
public final class RadarData {
    public final int frontLeft;     // distance in cm, 0=no obstacle
    public final int frontCenterLeft;
    public final int frontCenterRight;
    public final int frontRight;
    public final int rearLeft;
    public final int rearCenterLeft;
    public final int rearCenterRight;
    public final int rearRight;

    public RadarData(int fl, int fcl, int fcr, int fr,
                     int rl, int rcl, int rcr, int rr) {
        this.frontLeft = fl;
        this.frontCenterLeft = fcl;
        this.frontCenterRight = fcr;
        this.frontRight = fr;
        this.rearLeft = rl;
        this.rearCenterLeft = rcl;
        this.rearCenterRight = rcr;
        this.rearRight = rr;
    }

    @Override public String toString() {
        return String.format("RadarData{F:%d/%d/%d/%d R:%d/%d/%d/%d}",
            frontLeft, frontCenterLeft, frontCenterRight, frontRight,
            rearLeft, rearCenterLeft, rearCenterRight, rearRight);
    }
}
