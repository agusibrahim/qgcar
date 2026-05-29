package com.qgcar.bus.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Internal helper: bind to any AIDL service, register a Binder callback whose
 * {@code onTransact} forwards every transaction to a {@link Sink} after stripping
 * the interface token.
 *
 * Used to listen to CanBusService and CarSignalService without having to
 * implement their full AIDL stubs.
 */
public final class RawBinderProbe {

    public interface Sink {
        void onEvent(int code, byte[] payload);
        default void onConnected(boolean connected) {}
    }

    private final Context mCtx;
    private final String mServicePackage;
    private final String mServiceAction;
    private final String mServiceDescriptor;
    private final String mCallbackDescriptor;
    private final int mAddCallbackTxnCode;
    private final Sink mSink;

    private IBinder mService;
    private boolean mBound;
    private final Binder mCallback;

    public RawBinderProbe(Context ctx,
                          String servicePackage, String serviceAction,
                          String serviceDescriptor, String callbackDescriptor,
                          int addCallbackTxnCode, Sink sink) {
        mCtx = ctx.getApplicationContext();
        mServicePackage = servicePackage;
        mServiceAction = serviceAction;
        mServiceDescriptor = serviceDescriptor;
        mCallbackDescriptor = callbackDescriptor;
        mAddCallbackTxnCode = addCallbackTxnCode;
        mSink = sink;
        mCallback = new Binder() {
            { attachInterface(null, mCallbackDescriptor); }
            @Override public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                    throws RemoteException {
                if (code == INTERFACE_TRANSACTION) {
                    if (reply != null) reply.writeString(mCallbackDescriptor);
                    return true;
                }
                int payloadStart;
                int prePos = data.dataPosition();
                try {
                    data.enforceInterface(mCallbackDescriptor);
                    payloadStart = data.dataPosition();
                } catch (Throwable t) {
                    payloadStart = prePos;
                }
                int payloadEnd = data.dataSize();
                int payloadLen = Math.max(0, payloadEnd - payloadStart);
                byte[] bytes = new byte[payloadLen];
                if (payloadLen > 0) {
                    data.setDataPosition(0);
                    byte[] all = data.marshall();
                    if (all != null && all.length >= payloadStart) {
                        int toCopy = Math.min(all.length - payloadStart, payloadLen);
                        if (toCopy > 0) {
                            System.arraycopy(all, payloadStart, bytes, 0, toCopy);
                        }
                    }
                }
                try { mSink.onEvent(code, bytes); } catch (Throwable ignored) {}
                if (reply != null) reply.writeNoException();
                return true;
            }
        };
    }

    public boolean start() {
        Intent it = new Intent(mServiceAction);
        it.setPackage(mServicePackage);
        boolean ok = mCtx.bindService(it, mConn, Context.BIND_AUTO_CREATE);
        if (!ok) mSink.onConnected(false);
        return ok;
    }

    public IBinder getServiceBinder() {
        return mService;
    }

    public void stop() {
        if (mBound) {
            try { mCtx.unbindService(mConn); } catch (Throwable ignored) {}
            mBound = false;
        }
    }

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mService = service;
            mBound = true;
            registerCallback();
            mSink.onConnected(true);
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
            mSink.onConnected(false);
        }
    };

    private void registerCallback() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(mServiceDescriptor);
            data.writeStrongBinder(mCallback);
            mService.transact(mAddCallbackTxnCode, data, reply, 0);
            try { reply.readException(); } catch (Throwable ignored) {}
        } catch (Throwable ignored) {
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
