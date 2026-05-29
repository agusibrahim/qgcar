package com.qgcar.bus;

/** Engine fluid status — oil, coolant, brake fluid, wiper fluid. */
public final class EngineFluidStatus {
    public final int oilLevel;          // 0=ok, 1=low, 2=critical
    public final int coolantLevel;      // 0=ok, 1=low, 2=critical
    public final int brakePadWear;      // 0=ok, 1=worn, 2=critical
    public final int brakeFluidLevel;   // 0=ok, 1=low, 2=critical
    public final int wiperFluidLevel;   // 0=ok, 1=low

    public EngineFluidStatus(int oil, int coolant, int brakePad, int brakeFluid, int wiperFluid) {
        this.oilLevel = oil;
        this.coolantLevel = coolant;
        this.brakePadWear = brakePad;
        this.brakeFluidLevel = brakeFluid;
        this.wiperFluidLevel = wiperFluid;
    }

    @Override public String toString() {
        return String.format("Fluid{oil=%d coolant=%d brakePad=%d brakeFluid=%d wiper=%d}",
            oilLevel, coolantLevel, brakePadWear, brakeFluidLevel, wiperFluidLevel);
    }
}
