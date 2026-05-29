package com.qgcar.bus;

/** Seatbelts for the two front seats. */
public final class SeatBelts {
    public final SeatBeltState driver;
    public final SeatBeltState passenger;

    public SeatBelts(SeatBeltState driver, SeatBeltState passenger) {
        this.driver = driver;
        this.passenger = passenger;
    }

    @Override public String toString() {
        return "SeatBelts{driver=" + driver + ", passenger=" + passenger + "}";
    }
}
