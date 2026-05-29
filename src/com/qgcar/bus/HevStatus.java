package com.qgcar.bus;

/** Hybrid/EV system status. */
public final class HevStatus {
    public final int mode;              // 0=EV, 1=HEV, 2=Engine, 3=Sport
    public final int batterySoc;        // battery state of charge 0-100%
    public final int energyFlow;        // energy flow direction
    public final int energyRecovery;    // regen braking level
    public final int powerLevel;        // power output level

    public HevStatus(int mode, int soc, int flow, int recovery, int power) {
        this.mode = mode;
        this.batterySoc = soc;
        this.energyFlow = flow;
        this.energyRecovery = recovery;
        this.powerLevel = power;
    }

    @Override public String toString() {
        return String.format("HevStatus{mode=%d SOC=%d%% recovery=%d}", mode, batterySoc, energyRecovery);
    }
}
