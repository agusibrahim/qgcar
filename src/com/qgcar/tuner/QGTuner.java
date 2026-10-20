package com.qgcar.tuner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;

import com.qgcar.bus.internal.RawBinderProbe;

/**
 * Control the vehicle's hardware FM/AM radio tuner chip via {@code TunerService}
 * ("qg.tuner_service" / {@code com.qinggan.tuner.ITunerService}).
 */
public final class QGTuner {

    private static final String SVC_MGR    = "qg.tuner_service";
    private static final String SVC_PKG    = "com.qinggan.tuner.service";
    private static final String SVC_ACTION = "com.qinggan.tuner.action.TUNER_SERVICE";
    private static final String DESCRIPTOR = "com.qinggan.tuner.ITunerService";

    public static final int BAND_FM = 0;
    public static final int BAND_AM = 1;

    public static final int SEEK_UP   = 0;
    public static final int SEEK_DOWN = 1;

    public static final int SEEK_NOT_AROUND = 0;
    public static final int SEEK_AROUND     = 1;

    public static final int ENABLE_SEEKING     = 0;
    public static final int FORCE_STOP_SEEKING = 1;

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

    private QGTuner(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGTuner connect(Context ctx) {
        QGTuner t = new QGTuner(ctx);
        IBinder sys = RawBinderProbe.getSystemServiceBinder(SVC_MGR);
        if (sys != null) t.mService = sys;
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        t.mBound = t.mCtx.bindService(it, t.mConn, Context.BIND_AUTO_CREATE);
        return t;
    }

    /** Tune and play a specific frequency (e.g. 9870 for 98.7 MHz FM, or 999 for 999 kHz AM) (TXN=1). */
    public int play(int frequency) { return transactOneInt(1, frequency); }

    /** Seek/check a specific frequency during scan (TXN=2). */
    public int seekSet(int frequency) { return transactOneInt(2, frequency); }

    /** Set radio band (BAND_FM=0, BAND_AM=1) (TXN=3). */
    public int setBand(int band) { return transactOneInt(3, band); }

    /** Get current radio band (BAND_FM=0, BAND_AM=1) (TXN=4). */
    public int getBand() { return transactNoArgsInt(4, -1); }

    /** Get frequency range info [minFreq, maxFreq, step] for current band (TXN=5). */
    public int[] getFreqInfo() {
        if (mService == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(5, data, reply, 0);
            reply.readException();
            return reply.createIntArray();
        } catch (Throwable t) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Seek up to next station (TXN=6). */
    public int seekUp() { return transactNoArgsInt(6, -1); }

    /** Seek down to previous station (TXN=7). */
    public int seekDown() { return transactNoArgsInt(7, -1); }

    /** Set seek status (ENABLE_SEEKING=0, FORCE_STOP_SEEKING=1) (TXN=8). */
    public int setSeekStatus(int status) { return transactOneInt(8, status); }

    /** Get detailed hardware tuner status (frequency, signal level, stereo, etc.) (TXN=9). */
    public TunerStatus getTunerStatus() {
        if (mService == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(9, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0 ? TunerStatus.fromParcel(reply) : null;
        } catch (Throwable t) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Mute or unmute hardware radio tuner output (TXN=10). */
    public int setMute(boolean mute) { return transactOneInt(10, mute ? 1 : 0); }

    /** Seek radio station with direction and wrap-around flag (TXN=11). */
    public int seekRadio(int direction, int around) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(direction);
            data.writeInt(around);
            mService.transact(11, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public void disconnect() {
        if (mBound) {
            try { mCtx.unbindService(mConn); } catch (Throwable ignored) {}
            mBound = false;
        }
        mService = null;
    }

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

    private int transactOneInt(int txn, int arg) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(arg);
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
