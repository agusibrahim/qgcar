package com.qgcar.keys;

/**
 * QG/vendor keycodes that the steering-wheel buttons report through
 * KeyManagerService. Subset of {@code com.qinggan.system.QGKeyEvent} kept here
 * for API stability — values match the vendor enum.
 */
public final class QGKeyCode {
    private QGKeyCode() {}

    public static final int VOICE         = 130; // KEYCODE_IVOKA
    public static final int MEDIA_NEXT    = 3;
    public static final int MEDIA_PREV    = 4;
    public static final int MEDIA_PLAY    = 8;
    public static final int MEDIA_PAUSE   = 9;
    public static final int MUTE          = 5;
    public static final int PHONE         = 128;
    public static final int HANGUP        = 176;
    public static final int HOME          = 32;
    public static final int BACK          = 33;
    public static final int MENU          = 138;
    public static final int MODE          = 64;
    public static final int SRC           = 65;
    public static final int RADIO         = 136;
    public static final int NAVI          = 135;
    public static final int SETUP         = 133;
    public static final int POWER         = 34;
    public static final int OPERATE_OK    = 256;

    public static final int SWS_LEFT      = 49;
    public static final int SWS_RIGHT     = 50;
    public static final int SWS_UP_WHEEL  = 51;
    public static final int SWS_DOWN_WHEEL= 52;
    public static final int SWS_USER      = 54;
}
