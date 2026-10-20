package com.qgcar.power;

/**
 * Listener for Head-Unit AVN power state changes from {@code PowerManagerService}.
 */
public abstract class PowerListener {
    /**
     * Called when AVN power state changes (see {@link QGPower} AVN_STATUS_* constants).
     */
    public void onAVNStatusChanged(int avnStatus) {}
}
