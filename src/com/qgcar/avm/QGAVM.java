package com.qgcar.avm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Control the vendor's Around View Monitor overlay via SystemPolicyService.
 *
 * Binds to {@code com.qinggan.systempolicy / SystemPolicyService} and uses
 * the decompiled {@code ISystemPolicy} AIDL to call showAVM(), getAVMState(),
 * sendMsg(), touchEvent(), etc. Callbacks arrive via {@code IReverseCallback}.
 *
 * Usage:
 * <pre>{@code
 *   QGAVM avm = QGAVM.connect(ctx);
 *   avm.addListener(new AvmListener() {
 *       @Override public void onAvmStateChanged(int state) { ... }
 *   });
 *   avm.show();    // show AVM overlay
 *   avm.hide();    // hide AVM overlay
 *   ...
 *   avm.disconnect();
 * }</pre>
 */
public final class QGAVM {

    private static final String SVC_PKG    = "com.qinggan.systempolicy";
    private static final String SVC_ACTION = "com.qinggan.systempolicy.SystemPolicyService";
    private static final String DESCRIPTOR = "com.qinggan.system.ISystemPolicy";

    // Transaction codes from ISystemPolicy
    private static final int TXN_registerCallback    = 15;
    private static final int TXN_unregisterCallback  = 16;
    private static final int TXN_getPreReverseState  = 17;
    private static final int TXN_getReverseState     = 18;
    private static final int TXN_getPdcState         = 19;
    private static final int TXN_showAVM             = 20;
    private static final int TXN_touchEvent          = 21;
    private static final int TXN_sendMsg             = 22;
    private static final int TXN_getAVMState         = 23;

    // AVM show modes (parameter to showAVM)
    public static final int AVM_HIDE            = 0;
    public static final int AVM_SHOW            = 1;
    public static final int AVM_SHOW_REVERSE    = 2;
    public static final int AVM_HIDE_FOR_SPEED  = 3;
    public static final int AVM_NO_VIDEO        = 255;

    // AVM states (from getAVMState / onAVMStateChange)
    public static final int STATE_OUT = 0;
    public static final int STATE_IN  = 1;

    // Touch event types
    public static final int TOUCH_DOWN     = 0;
    public static final int TOUCH_UP       = 1;
    public static final int TOUCH_SINGLE   = 2;
    public static final int TOUCH_DOUBLE   = 3;
    public static final int TOUCH_LONG     = 4;
    public static final int TOUCH_MOVE     = 5;
    public static final int TOUCH_SWIPE    = 6;
    public static final int TOUCH_ZOOMIN   = 7;
    public static final int TOUCH_ZOOMOUT  = 8;
    public static final int TOUCH_ROTATE   = 9;

    // Message IDs for sendMsg
    public static final int MSG_CALIBRATION_IN  = 1;
    public static final int MSG_CALIBRATION_OUT = 2;
    public static final int MSG_SETUP_IN        = 3;
    public static final int MSG_SETUP_OUT       = 4;
    public static final int MSG_RESET           = 5;
    public static final int MSG_GES_READY       = 6;
    public static final int MSG_SET_BRIGHT      = 7;
    public static final int MSG_SET_CONTRAST    = 8;
    public static final int MSG_SET_COLOR       = 9;
    public static final int MSG_GET_BRIGHT      = 10;
    public static final int MSG_GET_CONTRAST    = 11;
    public static final int MSG_GET_COLOR       = 12;
    public static final int MSG_SET_LANGUAGE    = 13;
    public static final int MSG_ENABLE_TOUCH    = 14;

    private final Context mCtx;
    private final CopyOnWriteArrayList<AvmListener> mListeners = new CopyOnWriteArrayList<>();
    private IBinder mService;
    private boolean mBound;
    private boolean mCallbackRegistered;

    private final IBinder mCallback = new android.os.Binder() {
        {
            attachInterface(null, "com.qinggan.system.IReverseCallback");
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1598968902) {
                reply.writeString("com.qinggan.system.IReverseCallback");
                return true;
            }
            switch (code) {
                case 1: // onPrepareReverseChange
                    data.enforceInterface("com.qinggan.system.IReverseCallback");
                    int pre = data.readInt();
                    reply.writeNoException();
                    for (AvmListener l : mListeners) {
                        try { l.onPreReverseChanged(pre); } catch (Throwable ignored) {}
                    }
                    return true;
                case 2: // onReverseChange
                    data.enforceInterface("com.qinggan.system.IReverseCallback");
                    int rev = data.readInt();
                    reply.writeNoException();
                    for (AvmListener l : mListeners) {
                        try { l.onReverseChanged(rev); } catch (Throwable ignored) {}
                    }
                    return true;
                case 3: // onPdcStateChange
                    data.enforceInterface("com.qinggan.system.IReverseCallback");
                    int pdc = data.readInt();
                    reply.writeNoException();
                    for (AvmListener l : mListeners) {
                        try { l.onPdcStateChanged(pdc); } catch (Throwable ignored) {}
                    }
                    return true;
                case 4: // onAVMStateChange
                    data.enforceInterface("com.qinggan.system.IReverseCallback");
                    int avm = data.readInt();
                    reply.writeNoException();
                    for (AvmListener l : mListeners) {
                        try { l.onAvmStateChanged(avm); } catch (Throwable ignored) {}
                    }
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    };

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = service;
            mBound = true;
            tryRegisterCallback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
            mCallbackRegistered = false;
        }
    };

    private QGAVM(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGAVM connect(Context ctx) {
        QGAVM a = new QGAVM(ctx);
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        boolean ok = a.mCtx.bindService(it, a.mConn, Context.BIND_AUTO_CREATE);
        if (!ok) throw new RuntimeException("Cannot bind to " + SVC_ACTION);
        return a;
    }

    private void tryRegisterCallback() {
        if (mService == null || mCallbackRegistered) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeStrongBinder(mCallback);
            mService.transact(TXN_registerCallback, data, reply, 0);
            reply.readException();
            mCallbackRegistered = reply.readInt() != 0;
        } catch (Throwable ignored) {
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    // ---- Public API ----

    public void addListener(AvmListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(AvmListener l) { mListeners.remove(l); }

    /** Show the AVM overlay. */
    public int show() { return showAVM(AVM_SHOW); }

    /** Hide the AVM overlay. */
    public int hide() { return showAVM(AVM_HIDE); }

    /** Low-level showAVM call. Mode: AVM_HIDE=0, AVM_SHOW=1, AVM_SHOW_REVERSE=2, etc. */
    public int showAVM(int mode) {
        return transactInt(TXN_showAVM, mode);
    }

    /** Query current AVM state: STATE_OUT=0, STATE_IN=1. */
    public int getAVMState() {
        return transactInt(TXN_getAVMState);
    }

    /** Query reverse gear state: 0=not in reverse, 1=in reverse. */
    public int getReverseState() {
        return transactInt(TXN_getReverseState);
    }

    /** Query pre-reverse state. */
    public int getPreReverseState() {
        return transactInt(TXN_getPreReverseState);
    }

    /** Query parking sensor state. */
    public int getPdcState() {
        return transactInt(TXN_getPdcState);
    }

    /** Send a message to the AVM system. See MSG_* constants. */
    public int sendMsg(int msgId, int param1, int param2) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(msgId);
            data.writeInt(param1);
            data.writeInt(param2);
            mService.transact(TXN_sendMsg, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable e) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Forward a touch event to the AVM overlay. */
    public int touchEvent(int type, int x, int y, int xVel, int yVel, int extra) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(type);
            data.writeInt(x);
            data.writeInt(y);
            data.writeInt(xVel);
            data.writeInt(yVel);
            data.writeInt(extra);
            mService.transact(TXN_touchEvent, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable e) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public void disconnect() {
        if (mCallbackRegistered && mService != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeStrongBinder(mCallback);
                mService.transact(TXN_unregisterCallback, data, reply, 0);
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
    }

    // ---- Internal helpers ----

    private int transactInt(int txnCode, int param) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(param);
            mService.transact(txnCode, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable e) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private int transactInt(int txnCode) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(txnCode, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable e) {
            return -1;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }
}
