package com.qgcar.bus;

/**
 * Window / sunroof state from com.qinggan.canbus.WindowStatus.
 *
 * Each window slot reports an int (UNKNOWN=-1, CLOSED=0, OPEN=1) plus a
 * float position (0.0..1.0 fraction open in vendor units).
 */
public final class WindowStatus {
    public final DoorState frontLeft;
    public final DoorState frontRight;
    public final DoorState rearLeft;
    public final DoorState rearRight;
    public final DoorState sunroof;
    public final DoorState roll;
    public final float sunroofPosition;
    public final float rollPosition;
    public final float frontLeftPosition;
    public final float frontRightPosition;
    public final float rearLeftPosition;
    public final float rearRightPosition;
    public final int sunroofTilt;

    public WindowStatus(DoorState fL, DoorState fR, DoorState rL, DoorState rR,
                        DoorState sunroof, DoorState roll,
                        float sunroofPos, float rollPos,
                        float fLPos, float fRPos, float rLPos, float rRPos,
                        int sunroofTilt) {
        this.frontLeft = fL; this.frontRight = fR;
        this.rearLeft = rL; this.rearRight = rR;
        this.sunroof = sunroof; this.roll = roll;
        this.sunroofPosition = sunroofPos;
        this.rollPosition = rollPos;
        this.frontLeftPosition = fLPos;
        this.frontRightPosition = fRPos;
        this.rearLeftPosition = rLPos;
        this.rearRightPosition = rRPos;
        this.sunroofTilt = sunroofTilt;
    }

    public enum SunroofState {
        CLOSED,
        TILTED,
        OPEN,
        UNKNOWN
    }

    public enum WindowState {
        CLOSED,
        OPEN,
        UNKNOWN
    }

    public WindowState getFLWindowState() {
        if (frontLeft == DoorState.UNKNOWN) return WindowState.UNKNOWN;
        return ((int) frontLeftPosition == 0) ? WindowState.CLOSED : WindowState.OPEN;
    }

    public WindowState getFRWindowState() {
        if (frontRight == DoorState.UNKNOWN) return WindowState.UNKNOWN;
        return ((int) frontRightPosition == 0) ? WindowState.CLOSED : WindowState.OPEN;
    }

    public WindowState getRLWindowState() {
        if (rearLeft == DoorState.UNKNOWN) return WindowState.UNKNOWN;
        return ((int) rearLeftPosition == 0) ? WindowState.CLOSED : WindowState.OPEN;
    }

    public WindowState getRRWindowState() {
        if (rearRight == DoorState.UNKNOWN) return WindowState.UNKNOWN;
        return ((int) rearRightPosition == 0) ? WindowState.CLOSED : WindowState.OPEN;
    }

    public WindowState getRollState() {
        if (roll == DoorState.UNKNOWN) return WindowState.UNKNOWN;
        return ((int) rollPosition == 0) ? WindowState.CLOSED : WindowState.OPEN;
    }

    public int getRollPercent() {
        return (int) rollPosition;
    }

    public int getFLWindowPercent() { return (int) frontLeftPosition; }
    public int getFRWindowPercent() { return (int) frontRightPosition; }
    public int getRLWindowPercent() { return (int) rearLeftPosition; }
    public int getRRWindowPercent() { return (int) rearRightPosition; }

    public SunroofState getSunroofState() {
        int pos = (int) sunroofPosition;
        if (pos == 0) {
            return SunroofState.CLOSED;
        } else if (pos == 100) {
            return SunroofState.TILTED;
        } else if (pos > 100 && pos <= 200) {
            return SunroofState.OPEN;
        } else {
            return SunroofState.UNKNOWN;
        }
    }

    public int getSunroofSlidePercent() {
        int pos = (int) sunroofPosition;
        if (pos > 100 && pos <= 200) {
            return pos - 100;
        }
        return 0;
    }

    @Override public String toString() {
        return String.format(
            "WindowStatus{fL=%s(%.2f) fR=%s(%.2f) rL=%s(%.2f) rR=%s(%.2f) sunroof=%s(%.2f)/state=%s roll=%s(%.2f) tilt=%d}",
            frontLeft, frontLeftPosition, frontRight, frontRightPosition,
            rearLeft, rearLeftPosition, rearRight, rearRightPosition,
            sunroof, sunroofPosition, getSunroofState(), roll, rollPosition, sunroofTilt);
    }
}
