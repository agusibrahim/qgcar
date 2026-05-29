package com.qgcar.bus;

/** Real-time fuel consumption data. */
public final class FuelConsumption {
    public final float instant;     // L/100km or L/h
    public final float average;     // L/100km
    public final float history;     // historical average
    public final float resetAvg;    // since last reset

    public FuelConsumption(float instant, float average, float history, float resetAvg) {
        this.instant = instant;
        this.average = average;
        this.history = history;
        this.resetAvg = resetAvg;
    }

    @Override public String toString() {
        return String.format("FuelConsumption{inst=%.2f avg=%.2f hist=%.2f}", instant, average, history);
    }
}
