package com.qgcar.keys;

/**
 * Receive steering-wheel button events.
 *
 * Return {@code true} to swallow the event before it reaches default handlers
 * (voice service, volume, etc.); {@code false} to let it pass through normally.
 */
public interface KeyListener {
    boolean onKey(KeyEvent event);
}
