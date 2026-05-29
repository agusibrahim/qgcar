package com.qgcar.bus;

/** Tire Pressure Monitoring System data — 4 wheels. */
public final class TpmsInfo {
    public final float frontLeftPressure;   // kPa
    public final float frontRightPressure;
    public final float rearLeftPressure;
    public final float rearRightPressure;
    public final float frontLeftTemp;       // celsius
    public final float frontRightTemp;
    public final float rearLeftTemp;
    public final float rearRightTemp;
    public final int frontLeftStatus;       // 0=normal, 1=warning
    public final int frontRightStatus;
    public final int rearLeftStatus;
    public final int rearRightStatus;

    public TpmsInfo(float flP, float frP, float rlP, float rrP,
                    float flT, float frT, float rlT, float rrT,
                    int flS, int frS, int rlS, int rrS) {
        this.frontLeftPressure = flP;
        this.frontRightPressure = frP;
        this.rearLeftPressure = rlP;
        this.rearRightPressure = rrP;
        this.frontLeftTemp = flT;
        this.frontRightTemp = frT;
        this.rearLeftTemp = rlT;
        this.rearRightTemp = rrT;
        this.frontLeftStatus = flS;
        this.frontRightStatus = frS;
        this.rearLeftStatus = rlS;
        this.rearRightStatus = rrS;
    }

    @Override public String toString() {
        return String.format("TpmsInfo{FL=%.1f/%.0f°C FR=%.1f/%.0f°C RL=%.1f/%.0f°C RR=%.1f/%.0f°C}",
            frontLeftPressure, frontLeftTemp, frontRightPressure, frontRightTemp,
            rearLeftPressure, rearLeftTemp, rearRightPressure, rearRightTemp);
    }
}
