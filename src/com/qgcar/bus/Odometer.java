package com.qgcar.bus;

/** Odometer + EV battery range from com.qinggan.canbus.Odometer. */
public final class Odometer {
    public final float canTravelMileage;
    public final float evCruisingRange;
    public final float enduranceMileage;
    public final float powerConsumption;
    public final float chargeRemainTime;
    public final float totalTravelingMileage;

    public Odometer(float canTravel, float evRange, float endurance,
                    float power, float chargeRemain, float total) {
        this.canTravelMileage = canTravel;
        this.evCruisingRange = evRange;
        this.enduranceMileage = endurance;
        this.powerConsumption = power;
        this.chargeRemainTime = chargeRemain;
        this.totalTravelingMileage = total;
    }

    @Override public String toString() {
        return String.format("Odometer{total=%.1f canTravel=%.1f evRange=%.1f endurance=%.1f}",
            totalTravelingMileage, canTravelMileage, evCruisingRange, enduranceMileage);
    }
}
