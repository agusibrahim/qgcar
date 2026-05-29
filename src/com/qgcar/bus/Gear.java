package com.qgcar.bus;

/**
 * Vehicle gear state. Values match com.qinggan.canbus.GearState ordinal()
 * exactly (which is what gets written to the parcel as the first int).
 *   Parking=0, Reverse=1, Neutral=2, Drive=3, Battery=4, Sport=5, Unknown=6.
 */
public enum Gear {
    PARKING(0, "P"),
    REVERSE(1, "R"),
    NEUTRAL(2, "N"),
    DRIVE(3, "D"),
    BATTERY(4, "B"),
    SPORT(5, "S"),
    UNKNOWN(6, "?");

    public final int ordinal;
    public final String label;
    Gear(int ord, String label) { this.ordinal = ord; this.label = label; }

    public static Gear fromOrdinal(int ord) {
        for (Gear g : values()) if (g.ordinal == ord) return g;
        return UNKNOWN;
    }
}
