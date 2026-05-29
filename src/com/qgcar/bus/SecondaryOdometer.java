package com.qgcar.bus;

/** Trip meter / secondary odometer. */
public final class SecondaryOdometer {
    public final float tripA;       // km
    public final float tripB;       // km

    public SecondaryOdometer(float tripA, float tripB) {
        this.tripA = tripA;
        this.tripB = tripB;
    }

    @Override public String toString() {
        return String.format("SecondaryOdometer{A=%.1fkm B=%.1fkm}", tripA, tripB);
    }
}
