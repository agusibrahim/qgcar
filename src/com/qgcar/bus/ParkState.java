package com.qgcar.bus;

/** Parking assist state. */
public final class ParkState {
    public final int active;        // 0=inactive, 1=searching, 2=parking
    public final int direction;     // 0=forward, 1=reverse
    public final int type;          // 0=parallel, 1=perpendicular

    public ParkState(int active, int direction, int type) {
        this.active = active;
        this.direction = direction;
        this.type = type;
    }

    @Override public String toString() {
        return String.format("ParkState{active=%d dir=%d type=%d}", active, direction, type);
    }
}
