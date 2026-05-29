package com.qgcar.avm;

/**
 * Listener for AVM (Around View Monitor) state changes.
 * All methods have default no-op so override only what you need.
 */
public interface AvmListener {
    /** AVM overlay visibility changed. 1=showing, 0=hidden. */
    default void onAvmStateChanged(int state) {}

    /** Gear reverse state changed. 1=in reverse, 0=not. */
    default void onReverseChanged(int state) {}

    /** Pre-reverse (about to enter reverse). 1=entering, 0=not. */
    default void onPreReverseChanged(int state) {}

    /** Parking sensor state changed. 1=active, 0=inactive. */
    default void onPdcStateChanged(int state) {}
}
