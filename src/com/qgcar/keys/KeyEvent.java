package com.qgcar.keys;

/**
 * Single steering-wheel key event. {@link #consumed} is what the listener
 * returns from {@link KeyListener#onKey} — set to {@code true} to swallow the
 * event so it doesn't reach the default handler (voice service, volume, ...).
 */
public final class KeyEvent {
    public final int code;
    public final int action;
    public final long timestampMs;

    public KeyEvent(int code, int action, long timestampMs) {
        this.code = code;
        this.action = action;
        this.timestampMs = timestampMs;
    }

    public boolean isDown()      { return action == QGKeyAction.DOWN; }
    public boolean isShort()     { return action == QGKeyAction.SHORT; }
    public boolean isLong()      { return action == QGKeyAction.LONG; }
    public boolean isHold()      { return action == QGKeyAction.HOLD; }
    public boolean isUp()        { return action == QGKeyAction.UP; }

    @Override public String toString() {
        return "KeyEvent{code=" + code + " act=" + QGKeyAction.name(action) + "}";
    }
}
