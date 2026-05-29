package com.qgcar.bus;

/** General vehicle info change notification. */
public final class VehicleInfoChanged {
    public final int infoType;
    public final int value1;
    public final int value2;
    public final int value3;

    public VehicleInfoChanged(int type, int v1, int v2, int v3) {
        this.infoType = type;
        this.value1 = v1;
        this.value2 = v2;
        this.value3 = v3;
    }

    @Override public String toString() {
        return String.format("VehicleInfoChanged{type=%d v1=%d v2=%d v3=%d}",
            infoType, value1, value2, value3);
    }
}
