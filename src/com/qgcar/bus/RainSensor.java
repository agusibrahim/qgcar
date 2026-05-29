package com.qgcar.bus;

/** Rain fall level sensor. */
public final class RainSensor {
    public final int level;  // 0=no rain, 1=light, 2=moderate, 3=heavy

    public RainSensor(int level) {
        this.level = level;
    }

    @Override public String toString() {
        return String.format("RainSensor{level=%d}", level);
    }
}
