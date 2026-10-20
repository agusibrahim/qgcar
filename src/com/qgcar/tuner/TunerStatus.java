package com.qgcar.tuner;

import android.os.Parcel;

/**
 * Hardware FM/AM tuner station status returned by {@code QGTuner.getTunerStatus()}.
 * Matches {@code com.qinggan.tuner.TunerStatus}.
 */
public final class TunerStatus {
    public final int frequency;
    public final boolean validStation;
    public final int level;
    public final int usn;
    public final int wam;
    public final int fof;
    public final int stereo;

    public TunerStatus(int frequency, boolean validStation, int level, int usn, int wam, int fof, int stereo) {
        this.frequency = frequency;
        this.validStation = validStation;
        this.level = level;
        this.usn = usn;
        this.wam = wam;
        this.fof = fof;
        this.stereo = stereo;
    }

    public static TunerStatus fromParcel(Parcel p) {
        int freq = p.readInt();
        boolean valid = p.readInt() != 0;
        int level = p.readInt();
        int usn = p.readInt();
        int wam = p.readInt();
        int fof = p.readInt();
        int stereo = p.readInt();
        return new TunerStatus(freq, valid, level, usn, wam, fof, stereo);
    }

    @Override
    public String toString() {
        return "TunerStatus{freq=" + frequency + ", valid=" + validStation +
            ", level=" + level + ", stereo=" + stereo + "}";
    }
}
