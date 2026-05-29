package com.qinggan.system;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class QGKeyEvent {
    public static final int KEYACTION_DOUBLE = 131072;
    public static final int KEYACTION_DOWN = 4096;
    public static final int KEYACTION_HOLD = 16384;
    public static final int KEYACTION_HOLD_STOP = 262144;
    public static final int KEYACTION_LONG = 32768;
    public static final int KEYACTION_LONG_STOP = 524288;
    public static final int KEYACTION_NONE = 0;
    public static final int KEYACTION_SHORT = 8192;
    public static final int KEYACTION_UP = 65536;
    public static final int KEYCACTION_END = 1048576;
    public static final int KEYCODE_AIRCONDITION = 143;
    public static final int KEYCODE_AUDIO = 132;
    public static final int KEYCODE_BACK = 33;
    public static final int KEYCODE_BACKLIGHT_DOWN = 193;
    public static final int KEYCODE_BACKLIGHT_UP = 192;
    public static final int KEYCODE_CCP_DOWN = 195;
    public static final int KEYCODE_CCP_KNOB_LEFT = 200;
    public static final int KEYCODE_CCP_KNOB_RIGHT = 199;
    public static final int KEYCODE_CCP_LEFT = 196;
    public static final int KEYCODE_CCP_OPERATE_OK = 198;
    public static final int KEYCODE_CCP_RIGHT = 197;
    public static final int KEYCODE_CCP_UP = 194;
    public static final int KEYCODE_DOWN = 181;
    public static final int KEYCODE_ENTER = 48;
    public static final int KEYCODE_HANGUP = 176;
    public static final int KEYCODE_HOME = 32;
    public static final int KEYCODE_ICALL = 134;
    public static final int KEYCODE_IC_DOWN = 202;
    public static final int KEYCODE_IC_OPERATE_OK = 203;
    public static final int KEYCODE_IC_UP = 201;
    public static final int KEYCODE_IVOKA = 130;
    public static final int KEYCODE_LEFT = 182;
    public static final int KEYCODE_LINK = 131;
    public static final int KEYCODE_LONG_OPERATE_OK = 257;
    public static final int KEYCODE_MAGIC_MODE = 140;
    public static final int KEYCODE_MEDIA = 139;
    public static final int KEYCODE_MEDIA_CLOSE = 10;
    public static final int KEYCODE_MEDIA_EJECT = 11;
    public static final int KEYCODE_MEDIA_FAST_FORWARD = 14;
    public static final int KEYCODE_MEDIA_NEXT = 3;
    public static final int KEYCODE_MEDIA_PAUSE = 9;
    public static final int KEYCODE_MEDIA_PLAY = 8;
    public static final int KEYCODE_MEDIA_PLAY_PAUSE = 6;
    public static final int KEYCODE_MEDIA_PREVIOUS = 4;
    public static final int KEYCODE_MEDIA_RECORD = 12;
    public static final int KEYCODE_MEDIA_REWIND = 13;
    public static final int KEYCODE_MEDIA_STOP = 7;
    public static final int KEYCODE_MEMDOWN = 178;
    public static final int KEYCODE_MEMUP = 177;
    public static final int KEYCODE_MENU = 138;
    public static final int KEYCODE_MODE = 64;
    public static final int KEYCODE_MUTE = 5;
    public static final int KEYCODE_NAVI = 135;
    public static final int KEYCODE_NAVIGATIONBAR_BACK = 204;
    public static final int KEYCODE_OPERATE_OK = 256;
    public static final int KEYCODE_PHONE = 128;
    public static final int KEYCODE_PHOTOGRAPH = 142;
    public static final int KEYCODE_PICKUP = 179;
    public static final int KEYCODE_POWER = 34;
    public static final int KEYCODE_RADIO = 136;
    public static final int KEYCODE_RADIO_FREQUENCY_DOWN = 187;
    public static final int KEYCODE_RADIO_FREQUENCY_UP = 186;
    public static final int KEYCODE_RADIO_NEXT_STATION = 188;
    public static final int KEYCODE_RIGHT = 183;
    public static final int KEYCODE_SETUP = 133;
    public static final int KEYCODE_SRC = 65;
    public static final int KEYCODE_SWC_ESC = 259;
    public static final int KEYCODE_SWC_SEL = 257;
    public static final int KEYCODE_SWS_KEY_USER_DEFINED = 54;
    public static final int KEYCODE_SWS_SELECT_DOWN_WHEEL = 52;
    public static final int KEYCODE_SWS_SELECT_LEFT = 49;
    public static final int KEYCODE_SWS_SELECT_RIGHT = 50;
    public static final int KEYCODE_SWS_SELECT_UP_WHEEL = 51;
    public static final int KEYCODE_SWS_SHUT_DOWN = 53;
    public static final int KEYCODE_TEL = 129;
    public static final int KEYCODE_TURN_LEFT = 184;
    public static final int KEYCODE_TURN_RIGHT = 185;
    public static final int KEYCODE_UNKNOWN = 0;
    public static final int KEYCODE_UP = 180;
    public static final int KEYCODE_VEHICLE = 141;
    public static final int KEYCODE_VIDEO = 137;
    public static final int KEYCODE_VOLUME_DOWN = 1;
    public static final int KEYCODE_VOLUME_UP = 2;
    private static Map<Integer, String> mKeyStringMap = new HashMap();

    public static boolean isMediaKey(int i) {
        switch (i) {
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            case 14:
                return true;
            case 5:
            case 10:
            case 11:
            case 12:
            default:
                return false;
        }
    }

    public static boolean isPhoneKey(int i) {
        if (i == 176 || i == 179) {
            return true;
        }
        switch (i) {
            case 128:
            case 129:
                return true;
            default:
                return false;
        }
    }

    public static String keyActionToStr(int i) {
        return i != 0 ? i != 4096 ? i != 8192 ? i != 16384 ? i != 32768 ? i != 65536 ? i != 131072 ? i != 262144 ? i != 524288 ? "ACTION_UNKNOWN" : "ACTION_LONG_STOP" : "ACTION_HOLD_STOP" : "ACTION_DOUBLE" : "ACTION_UP" : "ACTION_LONG" : "ACTION_HOLD" : "ACTION_SHORT" : "ACTION_DOWN" : "ACTION_NONE";
    }

    static {
        mKeyStringMap.put(0, "KEYCODE_UNKNOWN");
        mKeyStringMap.put(1, "KEYCODE_VOLUME_DOWN");
        mKeyStringMap.put(2, "KEYCODE_VOLUME_UP");
        mKeyStringMap.put(3, "KEYCODE_MEDIA_NEXT");
        mKeyStringMap.put(4, "KEYCODE_MEDIA_PREVIOUS");
        mKeyStringMap.put(5, "KEYCODE_MUTE");
        mKeyStringMap.put(6, "KEYCODE_MEDIA_PLAY_PAUSE");
        mKeyStringMap.put(7, "KEYCODE_MEDIA_STOP");
        mKeyStringMap.put(8, "KEYCODE_MEDIA_PLAY");
        mKeyStringMap.put(9, "KEYCODE_MEDIA_PAUSE");
        mKeyStringMap.put(10, "KEYCODE_MEDIA_CLOSE");
        mKeyStringMap.put(11, "KEYCODE_MEDIA_EJECT");
        mKeyStringMap.put(12, "KEYCODE_MEDIA_RECORD");
        mKeyStringMap.put(13, "KEYCODE_MEDIA_REWIND");
        mKeyStringMap.put(14, "KEYCODE_MEDIA_FAST_FORWARD");
        mKeyStringMap.put(32, "KEYCODE_HOME");
        mKeyStringMap.put(33, "KEYCODE_BACK");
        mKeyStringMap.put(34, "KEYCODE_POWER");
        mKeyStringMap.put(64, "KEYCODE_MODE");
        mKeyStringMap.put(65, "KEYCODE_SRC");
        mKeyStringMap.put(128, "KEYCODE_PHONE");
        mKeyStringMap.put(129, "KEYCODE_TEL");
        mKeyStringMap.put(130, "KEYCODE_IVOKA");
        mKeyStringMap.put(131, "KEYCODE_LINK");
        mKeyStringMap.put(132, "KEYCODE_AUDIO");
        mKeyStringMap.put(133, "KEYCODE_SETUP");
        mKeyStringMap.put(134, "KEYCODE_ICALL");
        mKeyStringMap.put(135, "KEYCODE_NAVI");
        mKeyStringMap.put(136, "KEYCODE_RADIO");
        mKeyStringMap.put(137, "KEYCODE_VIDEO");
        mKeyStringMap.put(138, "KEYCODE_MENU");
        mKeyStringMap.put(139, "KEYCODE_MEDIA");
        mKeyStringMap.put(140, "KEYCODE_MAGIC_MODE");
        mKeyStringMap.put(141, "KEYCODE_VEHICLE");
        mKeyStringMap.put(176, "KEYCODE_HANGUP");
        mKeyStringMap.put(177, "KEYCODE_MEMUP");
        mKeyStringMap.put(178, "KEYCODE_MEMDOWN");
        mKeyStringMap.put(179, "KEYCODE_PICKUP");
        mKeyStringMap.put(180, "KEYCODE_UP");
        mKeyStringMap.put(181, "KEYCODE_DOWN");
        mKeyStringMap.put(182, "KEYCODE_LEFT");
        mKeyStringMap.put(183, "KEYCODE_RIGHT");
        mKeyStringMap.put(184, "KEYCODE_TURN_LEFT");
        mKeyStringMap.put(185, "KEYCODE_TURN_RIGHT");
        mKeyStringMap.put(256, "KEYCODE_OPERATE_OK");
        mKeyStringMap.put(257, "KEYCODE_LONG_OPERATE_OK");
        mKeyStringMap.put(257, "KEYCODE_SWC_SEL");
        mKeyStringMap.put(259, "KEYCODE_SWC_ESC");
        mKeyStringMap.put(143, "KEYCODE_AIRCONDITION");
        mKeyStringMap.put(48, "KEYCODE_ENTER");
        mKeyStringMap.put(49, "KEYCODE_SWS_SELECT_LEFT");
        mKeyStringMap.put(50, "KEYCODE_SWS_SELECT_RIGHT");
        mKeyStringMap.put(51, "KEYCODE_SWS_SELECT_UP_WHEEL");
        mKeyStringMap.put(52, "KEYCODE_SWS_SELECT_DOWN_WHEEL");
        mKeyStringMap.put(53, "KEYCODE_SWS_SHUT_DOWN");
        mKeyStringMap.put(54, "KEYCODE_SWS_KEY_USER_DEFINED");
    }

    public static String keyCodeToStr(int i) {
        String str = mKeyStringMap.get(Integer.valueOf(i));
        if (str != null) {
            return str;
        }
        return "None_" + Integer.toString(i);
    }
}
