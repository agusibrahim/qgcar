package com.qgcar.bus;

/** Dashboard camera (DVR) state. */
public final class DvrState {
    public final int recording;     // 0=not recording, 1=recording
    public final int storage;       // 0=ok, 1=full, 2=error, 3=no card
    public final int error;         // 0=ok, other=error code

    public DvrState(int recording, int storage, int error) {
        this.recording = recording;
        this.storage = storage;
        this.error = error;
    }

    @Override public String toString() {
        return String.format("DvrState{rec=%d storage=%d err=%d}", recording, storage, error);
    }
}
