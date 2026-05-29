package com.qgcar.camera;

/**
 * Identifies one of the four AVM camera channels.
 *
 * Physical mapping depends on the deserializer wiring on the head-unit and is
 * unit-specific. Treat the labels as logical IDs: try each in your app and
 * note which is which on your vehicle.
 */
public enum CameraChannel {
    CH0(0), CH1(1), CH2(2), CH3(3);

    private final int idx;
    CameraChannel(int idx) { this.idx = idx; }
    public int idx() { return idx; }
}
