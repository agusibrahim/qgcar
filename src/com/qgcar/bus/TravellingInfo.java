package com.qgcar.bus;

/** Trip/travelling info — distance, time, fuel used for current trip. */
public final class TravellingInfo {
    public final float distance;        // km
    public final float duration;        // hours
    public final float fuelUsed;        // liters
    public final float avgFuelConsumption; // L/100km
    public final float avgSpeed;        // km/h

    public TravellingInfo(float dist, float dur, float fuel, float avgFuel, float avgSpd) {
        this.distance = dist;
        this.duration = dur;
        this.fuelUsed = fuel;
        this.avgFuelConsumption = avgFuel;
        this.avgSpeed = avgSpd;
    }

    @Override public String toString() {
        return String.format("TravellingInfo{dist=%.1fkm time=%.1fh fuel=%.1fL avg=%.1fL/100km}",
            distance, duration, fuelUsed, avgFuelConsumption);
    }
}
