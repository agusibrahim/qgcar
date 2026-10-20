package com.qgcar.power;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.qgcar.bus.internal.RawBinderProbe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Control head-unit AVN power state, screensaver, screen off, and standby display policy
 * via {@code PowerManagerService} ("qg.power_manager" / {@code com.qinggan.system.IPowerManagerService}).
 */
public final class QGPower {

    private static final String SVC_MGR    = "qg.power_manager";
    private static final String SVC_PKG    = "com.qinggan.powermanager";
    private static final String SVC_ACTION = "com.qinggan.powermanager.action.POWER_MANAGER_SERVICE";
    private static final String DESCRIPTOR = "com.qinggan.system.IPowerManagerService";
    private static final String CB_DESC    = "com.qinggan.power.IPowerManagerListener";

    public static final int AVN_STATUS_RUN_ON      = 0;
    public static final int AVN_STATUS_OFF         = 1;
    public static final int AVN_STATUS_OFF_PHONE   = 2;
    public static final int AVN_STATUS_TIME_ON     = 3;
    public static final int AVN_STATUS_CRANK       = 4;
    public static final int AVN_STATUS_STANDBY     = 5;
    public static final int AVN_STATUS_BOOT        = 6;
    public static final int AVN_STATUS_STANDBY_END = 7;
    public static final int AVN_STATUS_DISPLAY_OFF = 8;
    public static final int AVN_STATUS_DEEP_SLEEP  = 9;
    public static final int AVN_STATUS_UNKNOWN     = 255;

    public static final int STANDBY_PRI_LOW    = 0;
    public static final int STANDBY_PRI_NORMAL = 1;
    public static final int STANDBY_PRI_HIGH   = 2;

    private final Context mCtx;
    private final CopyOnWriteArrayList<PowerListener> mListeners = new CopyOnWriteArrayList<>();
    private volatile IBinder mService;
    private boolean mBound;
    private boolean mCallbackRegistered;

    private final IBinder mCallback = new Binder() {
        { attachInterface(null, CB_DESC); }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                if (reply != null) reply.writeString(CB_DESC);
                return true;
            }
            if (code == 1) {
                data.enforceInterface(CB_DESC);
                int status = data.readInt();
                for (PowerListener l : mListeners) {
                    try { l.onAVNStatusChanged(status); } catch (Throwable ignored) {}
                }
                if (reply != null) reply.writeNoException();
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mService = service;
            mBound = true;
            tryRegisterCallback();
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
            mCallbackRegistered = false;
        }
    };

    private QGPower(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGPower connect(Context ctx) {
        QGPower p = new QGPower(ctx);
        IBinder sys = RawBinderProbe.getSystemServiceBinder(SVC_MGR);
        if (sys != null) {
            p.mService = sys;
            p.tryRegisterCallback();
        }
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        p.mBound = p.mCtx.bindService(it, p.mConn, Context.BIND_AUTO_CREATE);
        return p;
    }

    private void tryRegisterCallback() {
        if (mService == null || mCallbackRegistered) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeStrongBinder(mCallback);
            mService.transact(1, data, reply, 0); // TRANSACTION_registerListener = 1
            reply.readException();
            mCallbackRegistered = reply.readInt() != 0;
        } catch (Throwable ignored) {
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public void addListener(PowerListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(PowerListener l) { mListeners.remove(l); }

    /** Get current AVN power status (see AVN_STATUS_* constants) (TXN=3). */
    public int getAVNStatus() { return transactReadInt(3, AVN_STATUS_UNKNOWN); }

    /** Get car key status (TXN=4). */
    public int getCarKeyStatus() { return transactReadInt(4, -1); }

    /** Enter screen-off mode (TXN=5). */
    public boolean enterScreenOff() { return transactVoid(5); }

    /** Exit screen-off mode (TXN=6). */
    public boolean exitScreenOff() { return transactVoid(6); }

    /** Enter screensaver (clock lockscreen) mode (TXN=7). */
    public boolean enterScreenSaver() { return transactVoid(7); }

    /** Exit screensaver mode (TXN=8). */
    public boolean exitScreenSaver() { return transactVoid(8); }

    /** Enter secure mode (TXN=9). */
    public boolean enterSecureMode() { return transactVoid(9); }

    /** Exit secure mode (TXN=10). */
    public boolean exitSecureMode() { return transactVoid(10); }

    /** Enter standby mode (TXN=20). */
    public boolean enterStandbyMode() { return transactVoid(20); }

    /** Exit standby mode (TXN=19). */
    public boolean exitStandbyMode() { return transactVoid(19); }

    /** Check if screensaver is active (TXN=12). */
    public boolean isScreenSaver() { return transactReadInt(12, 0) != 0; }

    /** Check if secure window mode is active (TXN=13). */
    public boolean isSecureWindowMode() { return transactReadInt(13, 0) != 0; }

    /** Check if screen is off (TXN=14). */
    public boolean isScreenOff() { return transactReadInt(14, 0) != 0; }

    /** Check if backlight is off (TXN=17). */
    public boolean isBacklightOff() { return transactReadInt(17, 0) != 0; }

    /** Check if standby UI is showing (TXN=18). */
    public boolean isStandbyUIShow() { return transactReadInt(18, 0) != 0; }

    /** Request keeping display on during standby (see STANDBY_PRI_* constants) (TXN=15). */
    public boolean requestDisplayInStandby(int priority) {
        return transactWriteIntBool(15, priority);
    }

    /** Release display-in-standby request (TXN=16). */
    public boolean abandonDisplayInStandby(int priority) {
        return transactWriteIntBool(16, priority);
    }

    public void disconnect() {
        if (mCallbackRegistered && mService != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeStrongBinder(mCallback);
                mService.transact(2, data, reply, 0); // TRANSACTION_unregisterListener = 2
            } catch (Throwable ignored) {
            } finally {
                reply.recycle();
                data.recycle();
            }
        }
        if (mBound) {
            try { mCtx.unbindService(mConn); } catch (Throwable ignored) {}
        }
        mBound = false;
        mCallbackRegistered = false;
        mService = null;
        mListeners.clear();
    }

    private int transactReadInt(int txn, int defVal) {
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

    private boolean transactWriteIntBool(int txn, int arg) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(arg);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private boolean transactVoid(int txn) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }
}
