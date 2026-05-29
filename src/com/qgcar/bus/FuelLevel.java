package com.qgcar.bus;

/**
 * Fuel info from com.qinggan.canbus.FuelLevel. Some fields are EV-specific
 * and only meaningful on hybrid/electric variants.
 */
public final class FuelLevel {
    public final int capacity;          // tank size (vendor-defined unit)
    public final int remain;            // remaining (same unit)
    public final float percentage;      // 0..100 typically
    public final boolean shortage;      // low-fuel flag
    public final float instantConsumption;
    public final float avgConsumption;
    public final float historyAvgConsumption;
    public final float resetAvgConsumption;

    public FuelLevel(int capacity, int remain, float pct, boolean shortage,
                     float instant, float avg, float histAvg, float resetAvg) {
        this.capacity = capacity;
        this.remain = remain;
        this.percentage = pct;
        this.shortage = shortage;
        this.instantConsumption = instant;
        this.avgConsumption = avg;
        this.historyAvgConsumption = histAvg;
        this.resetAvgConsumption = resetAvg;
    }

    @Override public String toString() {
        return String.format("FuelLevel{%.1f%% remain=%d/%d short=%s inst=%.2f avg=%.2f}",
            percentage, remain, capacity, shortage, instantConsumption, avgConsumption);
    }
}
