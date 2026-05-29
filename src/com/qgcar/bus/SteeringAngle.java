package com.qgcar.bus;

/**
 * Steering-wheel center angle. From com.qinggan.canbus.SWCAngle.
 *
 * Direction: CENTER=0, RIGHT=1, LEFT=2, UNKNOWN=Integer.MIN_VALUE.
 * Angle is in degrees from center (0..max). Sign meaning is vendor-defined
 * — combine with direction for left/right resolution.
 */
public final class SteeringAngle {
    public enum Direction {
        CENTER(0), RIGHT(1), LEFT(2), UNKNOWN(Integer.MIN_VALUE);
        public final int raw;
        Direction(int r) { raw = r; }
        public static Direction fromRaw(int v) {
            switch (v) {
                case 0: return CENTER;
                case 1: return RIGHT;
                case 2: return LEFT;
                default: return UNKNOWN;
            }
        }
    }

    public final Direction direction;
    public final int angleDeg;

    public SteeringAngle(Direction d, int angleDeg) {
        this.direction = d;
        this.angleDeg = angleDeg;
    }

    /** Signed degrees: negative=left, positive=right, 0=center. */
    public int signedDeg() {
        switch (direction) {
            case LEFT:  return -Math.abs(angleDeg);
            case RIGHT: return  Math.abs(angleDeg);
            default:    return 0;
        }
    }

    @Override public String toString() {
        return "SteeringAngle{" + direction + " " + angleDeg + "°}";
    }
}
