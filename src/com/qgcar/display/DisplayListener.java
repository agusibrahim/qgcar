package com.qgcar.display;

/**
 * Listener for screen brightness and auto-dimmer changes from {@code SystemPolicyService}.
 */
public abstract class DisplayListener {
    /**
     * Called when screen backlight brightness changes.
     * @param brightness 0–100
     * @param flag       0=default, 1=do not notify
     * @return true if consumed, false otherwise (default false).
     */
    public boolean onBackLightChanged(int brightness, int flag) { return false; }

    /**
     * Called when day/night dimmer state changes.
     * @param dimmer 0=night, 1=day, 2=auto
     * @param flag   notification flag
     */
    public void onDimmerChanged(int dimmer, int flag) {}
}
