package com.qgcar.bus;

/** Instrument cluster info. */
public final class InstrumentCluster {
    public final int brightness;    // backlight level 0-100
    public final int themeMode;     // theme index
    public final int dimmerStatus;  // 0=auto, 1=manual

    public InstrumentCluster(int brightness, int themeMode, int dimmerStatus) {
        this.brightness = brightness;
        this.themeMode = themeMode;
        this.dimmerStatus = dimmerStatus;
    }

    @Override public String toString() {
        return String.format("Cluster{bright=%d theme=%d dimmer=%d}", brightness, themeMode, dimmerStatus);
    }
}
