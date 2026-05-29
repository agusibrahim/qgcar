package com.qgcar.keys;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.qinggan.system.IKeyManagerCallback;
import com.qinggan.system.IKeyManagerService;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Bind to the vendor KeyManagerService and receive steering-wheel events
 * as a "global intercept" — runs ahead of all default handlers. Listeners
 * return {@code true} to swallow events.
 *
 * Connection is async: pass an {@link OnReady} callback or call
 * {@link #waitUntilReady(long)}.
 */
public final class QGKeys {

    private static final String SVC_PKG    = "com.qinggan.keymanager.service";
    private static final String SVC_ACTION = "com.qinggan.keymanager.action.KEY_MANAGER_SERVICE";

    public interface OnReady { void onReady(boolean connected); }

    private final Context mCtx;
    private final CopyOnWriteArrayList<KeyListener> mListeners = new CopyOnWriteArrayList<>();
    private final IBinder mClientToken = new Binder();
    private OnReady mOnReady;
    private final Object mReadyLock = new Object();
    private volatile boolean mReady;
    private volatile boolean mInterceptActive;
    private volatile boolean mPolicyDisabled;

    private IKeyManagerService mKms;
    private boolean mBound;

    private final IKeyManagerCallback.Stub mCb = new IKeyManagerCallback.Stub() {
        @Override public boolean onKeyEvent(android.view.KeyEvent e) {
            return dispatch(e);
        }
        @Override public boolean onEvent(android.view.KeyEvent e, String pkg, int type) {
            return dispatch(e);
        }
        private boolean dispatch(android.view.KeyEvent e) {
            if (e == null) return false;
            KeyEvent ev = new KeyEvent(e.getKeyCode(), e.getAction(), System.currentTimeMillis());
            boolean consumed = false;
            for (KeyListener l : mListeners) {
                try { if (l.onKey(ev)) consumed = true; }
                catch (Throwable ignored) {}
            }
            return consumed;
        }
    };

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mKms = IKeyManagerService.Stub.asInterface(service);
            mBound = true;
            tryRegisterIntercept();
            synchronized (mReadyLock) { mReady = true; mReadyLock.notifyAll(); }
            if (mOnReady != null) mOnReady.onReady(true);
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            mKms = null;
            mBound = false;
            mInterceptActive = false;
            synchronized (mReadyLock) { mReady = false; }
        }
    };

    private QGKeys(Context ctx) { mCtx = ctx.getApplicationContext(); }

    public static QGKeys connect(Context ctx) {
        return connect(ctx, null);
    }

    public static QGKeys connect(Context ctx, OnReady onReady) {
        QGKeys k = new QGKeys(ctx);
        k.mOnReady = onReady;
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        boolean ok = k.mCtx.bindService(it, k.mConn, Context.BIND_AUTO_CREATE);
        if (!ok && onReady != null) onReady.onReady(false);
        return k;
    }

    /** Block up to {@code timeoutMs} for the service to bind. Returns true if connected. */
    public boolean waitUntilReady(long timeoutMs) {
        synchronized (mReadyLock) {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (!mReady) {
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) return mReady;
                try { mReadyLock.wait(left); } catch (InterruptedException e) { return mReady; }
            }
            return mReady;
        }
    }

    public boolean isReady() { return mReady; }

    public void addListener(KeyListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(KeyListener l) { mListeners.remove(l); }

    /** Toggle the vendor's "default key policy" (volume, voice, etc.).
     *  When OFF, the system stops dispatching SWC keys to default handlers. */
    public void setPolicyDisabled(boolean disabled) {
        mPolicyDisabled = disabled;
        Intent it = new Intent("dls.enablekeypolicy");
        it.putExtra("enable", disabled ? 0 : 1);
        it.addCategory(Intent.CATEGORY_DEFAULT);
        mCtx.sendBroadcast(it);
    }

    public boolean isPolicyDisabled() { return mPolicyDisabled; }

    private void tryRegisterIntercept() {
        if (mKms == null) return;
        try {
            int r = mKms.registerInterceptListener(mClientToken, mCb,
                mCtx.getPackageName(), 1, 50);
            mInterceptActive = (r >= 0);
        } catch (RemoteException ignored) {}
    }

    public void disconnect() {
        if (mInterceptActive && mKms != null) {
            try { mKms.unregisterInterceptListener(mCb, mCtx.getPackageName()); }
            catch (Throwable ignored) {}
        }
        if (mBound) {
            try { mCtx.unbindService(mConn); } catch (Throwable ignored) {}
        }
        mBound = false;
        mInterceptActive = false;
    }
}
