package com.qgcar.bus;

/** 12V battery status. */
public final class BatteryState {
    public final float voltage;      // volts
    public final int health;         // 0=good, 1=warning, 2=critical
    public final int chargingState;  // 0=idle, 1=charging, 2=discharging

    public BatteryState(float voltage, int health, int chargingState) {
        this.voltage = voltage;
        this.health = health;
        this.chargingState = chargingState;
    }

    @Override public String toString() {
        return String.format("BatteryState{%.1fV health=%d charge=%d}", voltage, health, chargingState);
    }
}
