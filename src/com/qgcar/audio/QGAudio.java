package com.qgcar.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;

import com.qgcar.bus.internal.RawBinderProbe;

/**
 * Control the vehicle's hardware DSP, 5-band Equalizer, Balance/Fader soundstage,
 * Subwoofer, Loudness, Arkamys/DTS effects, speed-compensated volume, reverse audio
 * ducking, and per-stream volumes via {@code AudioPolicyService}
 * ("qg.audio_policy" / {@code com.qinggan.audiopolicy.IAudioPolicyService}).
 */
public final class QGAudio {

    private static final String SVC_MGR    = "qg.audio_policy";
    private static final String SVC_PKG    = "com.qinggan.audiopolicy.service";
    private static final String SVC_ACTION = "com.qinggan.audiopolicy.action.AUDIO_POLICY_SERVICE";
    private static final String DESCRIPTOR = "com.qinggan.audiopolicy.IAudioPolicyService";

    // Arkamys soundstage modes
    public static final int ARKAMYS_FLAT           = 0;
    public static final int ARKAMYS_DRIVER         = 1;
    public static final int ARKAMYS_ALL_PASSENGERS = 2;
    public static final int ARKAMYS_BYPASS         = 3;

    // DTS modes
    public static final int DTS_CINEMA = 0;
    public static final int DTS_MUSIC  = 1;
    public static final int DTS_MONO   = 2;

    // Speed-compensated volume modes
    public static final int SPEED_VOL_OFF    = 0;
    public static final int SPEED_VOL_LOW    = 3;
    public static final int SPEED_VOL_MIDDLE = 5;
    public static final int SPEED_VOL_HIGH   = 7;

    // Reverse audio ducking policies
    public static final int REVERSE_POLICY_MUTE        = 0;
    public static final int REVERSE_POLICY_VOLUME_DOWN = 1;

    // Navigation mix policies
    public static final int MIX_POLICY_DIRECT     = 16;
    public static final int MIX_POLICY_NAVI_FIRST = 32;

    // Volume flags
    public static final int FLAG_SHOW_UI    = 1;
    public static final int FLAG_PLAY_SOUND = 2;

    private final Context mCtx;
    private volatile IBinder mService;
    private boolean mBound;

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mService = service;
            mBound = true;
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    private QGAudio(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGAudio connect(Context ctx) {
        QGAudio a = new QGAudio(ctx);
        IBinder sys = RawBinderProbe.getSystemServiceBinder(SVC_MGR);
        if (sys != null) a.mService = sys;
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        a.mBound = a.mCtx.bindService(it, a.mConn, Context.BIND_AUTO_CREATE);
        return a;
    }

    // ---- Volume & Mute ----

    /** Get volume for a specific stream (TXN=16). */
    public int getStreamVolume(int streamType) { return transactIntInt(16, streamType, -1); }

    /** Get max volume for a specific stream (TXN=17). */
    public int getStreamMaxVolume(int streamType) { return transactIntInt(17, streamType, -1); }

    /** Set volume for a specific stream (TXN=18). */
    public boolean setStreamVolume(int streamType, int index, int flags) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(streamType);
            data.writeInt(index);
            data.writeInt(flags);
            mService.transact(18, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Adjust stream volume (+1=raise, 255=lower, 0=same) (TXN=14). */
    public boolean adjustStreamVolume(int streamType, int direction, int flags, int step) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(streamType);
            data.writeInt(direction);
            data.writeInt(flags);
            data.writeInt(step);
            mService.transact(14, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Check if master audio is muted (TXN=13). */
    public boolean isMasterMute() { return transactNoArgsInt(13, 0) != 0; }

    /** Set master audio mute (TXN=12). */
    public boolean setMasterMute(boolean mute, int flags) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(mute ? 1 : 0);
            data.writeInt(flags);
            mService.transact(12, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Check if a specific stream is muted (TXN=11). */
    public boolean isStreamMute(int streamType) { return transactIntInt(11, streamType, 0) != 0; }

    /** Mute or unmute a specific stream (TXN=10). */
    public boolean setStreamMute(int streamType, boolean mute) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(streamType);
            data.writeInt(mute ? 1 : 0);
            mService.transact(10, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    // ---- Equalizer (EQ) & Soundstage (Balance / Fader) ----

    /** Get current EQ preset index (TXN=24). */
    public int getEQModeIndex() { return transactNoArgsInt(24, -1); }

    /** Set EQ preset index (TXN=25). */
    public int setEQMode(int modeIndex) { return transactIntInt(25, modeIndex, -1); }

    /** Get maximum EQ band index (TXN=27). */
    public int getMaxBandIndex() { return transactNoArgsInt(27, -1); }

    /** Get maximum EQ band value (TXN=28). */
    public int getMaxBandValue() { return transactNoArgsInt(28, -1); }

    /** Get EQ gain value for a specific band index (TXN=29). */
    public int getBandValue(int bandIndex) { return transactIntInt(29, bandIndex, Integer.MIN_VALUE); }

    /** Set EQ gain value for a specific band index (TXN=26). */
    public int setBandValue(int bandIndex, int value) {
        return transactTwoIntInt(26, bandIndex, value);
    }

    /** Get max Balance/Fader level range (TXN=31). */
    public int getMaxBalanceFaderLevel() { return transactNoArgsInt(31, -1); }

    /** Get [balance, fader] level array (TXN=32). */
    public int[] getBalanceFaderLevel() {
        if (mService == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(32, data, reply, 0);
            reply.readException();
            return reply.createIntArray();
        } catch (Throwable t) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Set cabin soundstage Balance (left/right) and Fader (front/rear) (TXN=30). */
    public int setBalanceFaderLevel(int balance, int fader) {
        return transactTwoIntInt(30, balance, fader);
    }

    // ---- DSP, Loudness, Subwoofer, Arkamys & DTS ----

    /** Get loudness compensation state (TXN=34). */
    public int getLoudnessState() { return transactNoArgsInt(34, -1); }

    /** Set loudness compensation state (TXN=33). */
    public int setLoudness(int state) { return transactIntInt(33, state, -1); }

    /** Get subwoofer mode (TXN=64). */
    public int getSubwooferMode() { return transactNoArgsInt(64, -1); }

    /** Set subwoofer mode (TXN=63). */
    public int setSubwooferMode(int mode) { return transactIntInt(63, mode, -1); }

    /** Get Arkamys 3D sound mode (TXN=62). */
    public int getArkarmysMode() { return transactNoArgsInt(62, -1); }

    /** Set Arkamys 3D sound mode (see ARKAMYS_* constants) (TXN=61). */
    public int setArkarmysMode(int mode) { return transactIntInt(61, mode, -1); }

    /** Check if DTS sound effect is enabled (TXN=50). */
    public boolean isDtsEnabled() { return transactNoArgsInt(50, 0) != 0; }

    /** Enable or disable DTS sound effect (TXN=49). */
    public int setDtsEnabled(boolean enabled) { return transactIntInt(49, enabled ? 1 : 0, -1); }

    /** Get DTS mode (TXN=52). */
    public int getDtsMode() { return transactNoArgsInt(52, -1); }

    /** Set DTS mode (see DTS_* constants) (TXN=51). */
    public int setDtsMode(int mode) { return transactIntInt(51, mode, -1); }

    /** Get CAN external amplifier DSP type (TXN=37). */
    public int getCanDspType() { return transactNoArgsInt(37, -1); }

    /** Set CAN external amplifier DSP 3-band EQ (treble, mid, bass) (TXN=35). */
    public int setCanDspEffect(int treble, int mid, int bass) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(treble);
            data.writeInt(mid);
            data.writeInt(bass);
            mService.transact(35, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Set CAN external amplifier loudness (TXN=36). */
    public int setCanDspLoudness(boolean enabled) { return transactIntInt(36, enabled ? 1 : 0, -1); }

    // ---- Automotive Audio Policies (Speed Volume, Reverse Ducking, Baby Mode) ----

    /** Get speed-compensated volume mode (TXN=20). */
    public int getSpeedCoefMode() { return transactNoArgsInt(20, -1); }

    /** Set speed-compensated volume mode (see SPEED_VOL_* constants) (TXN=21). */
    public int setSpeedCoefMode(int mode) { return transactIntInt(21, mode, -1); }

    /** Get reverse gear audio ducking policy (TXN=38). */
    public int getReversePolicy() { return transactNoArgsInt(38, -1); }

    /** Set reverse gear audio ducking policy (0=mute, 1=volume down) (TXN=39). */
    public int setReversePolicy(int policy) { return transactIntInt(39, policy, -1); }

    /** Get navigation/media audio mix policy (TXN=40). */
    public int getMixPolicy() { return transactNoArgsInt(40, -1); }

    /** Set navigation/media audio mix policy (TXN=41). */
    public int setMixPolicy(int policy) { return transactIntInt(41, policy, -1); }

    /** Check if Baby-in-Car audio mode is active (TXN=60). */
    public boolean isBabyInCar() { return transactNoArgsInt(60, 0) != 0; }

    /** Enable or disable Baby-in-Car audio mode (TXN=59). */
    public int setBabyInCar(boolean enabled) { return transactIntInt(59, enabled ? 1 : 0, -1); }

    /** Get vehicle warning chime volume mode (0=normal, 1=high) (TXN=54). */
    public int getChimeMode() { return transactNoArgsInt(54, -1); }

    /** Set vehicle warning chime volume mode (0=normal, 1=high) (TXN=53). */
    public int setChimeMode(int mode) { return transactIntInt(53, mode, -1); }

    /** Get UI touch hint tone state (TXN=55). */
    public int getHintToneState() { return transactNoArgsInt(55, -1); }

    /** Set UI touch hint tone state (TXN=56). */
    public int setHintToneState(int state) { return transactIntInt(56, state, -1); }

    /** Get microphone mode (TXN=65). */
    public int getMicMode() { return transactNoArgsInt(65, -1); }

    /** Set microphone mode (TXN=66). */
    public int setMicMode(int mode) { return transactIntInt(66, mode, -1); }

    /** Check if microphone is muted (TXN=67). */
    public boolean isMicMute() { return transactNoArgsInt(67, 0) != 0; }

    /** Mute or unmute microphone (TXN=45). */
    public int setMicMute(boolean mute) { return transactIntInt(45, mute ? 1 : 0, -1); }

    /** Check if mainboard is reported hot by AudioPolicyService (TXN=74). */
    public boolean isMainBoardHot() { return transactNoArgsInt(74, 0) != 0; }

    public void disconnect() {
        if (mBound) {
            try { mCtx.unbindService(mConn); } catch (Throwable ignored) {}
            mBound = false;
        }
        mService = null;
    }

    // ---- Internal helpers ----

    private int transactNoArgsInt(int txn, int defVal) {
        if (mService == null) return defVal;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return defVal;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private int transactIntInt(int txn, int arg, int defVal) {
        if (mService == null) return defVal;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(arg);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return defVal;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private int transactTwoIntInt(int txn, int a1, int a2) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(a1);
            data.writeInt(a2);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }
}
