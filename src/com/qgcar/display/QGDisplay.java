package com.qgcar.display;

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
 * Control head-unit screen brightness, day/night dimmer, screen on/off,
 * physical keypad backlights, and touch areas via {@code SystemPolicyService}
 * ("qg.system_policy" / {@code com.qinggan.system.ISystemPolicy}).
 */
public final class QGDisplay {

    private static final String SVC_MGR    = "qg.system_policy";
    private static final String SVC_PKG    = "com.qinggan.systempolicy";
    private static final String SVC_ACTION = "com.qinggan.systempolicy.SystemPolicyService";
    private static final String DESCRIPTOR = "com.qinggan.system.ISystemPolicy";
    private static final String CB_DESC    = "com.qinggan.system.IBackLightCallback";

    public static final int BRIGHTNESS_MODE_NIGHT = 0;
    public static final int BRIGHTNESS_MODE_DAY   = 1;
    public static final int BRIGHTNESS_MODE_AUTO  = 2;

    public static final int FLAG_DEFAULT    = 0;
    public static final int FLAG_NOT_NOTIFY = 1;

    private final Context mCtx;
    private final CopyOnWriteArrayList<DisplayListener> mListeners = new CopyOnWriteArrayList<>();
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
            switch (code) {
                case 1: { // onBackLightChange(int, int)
                    data.enforceInterface(CB_DESC);
                    int bright = data.readInt();
                    int flag = data.readInt();
                    boolean handled = false;
                    for (DisplayListener l : mListeners) {
                        try { handled |= l.onBackLightChanged(bright, flag); } catch (Throwable ignored) {}
                    }
                    if (reply != null) {
                        reply.writeNoException();
                        reply.writeInt(handled ? 1 : 0);
                    }
                    return true;
                }
                case 2: { // onDimmerChange(int, int)
                    data.enforceInterface(CB_DESC);
                    int dimmer = data.readInt();
                    int flag = data.readInt();
                    for (DisplayListener l : mListeners) {
                        try { l.onDimmerChanged(dimmer, flag); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
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

    private QGDisplay(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGDisplay connect(Context ctx) {
        QGDisplay d = new QGDisplay(ctx);
        IBinder sys = RawBinderProbe.getSystemServiceBinder(SVC_MGR);
        if (sys != null) {
            d.mService = sys;
            d.tryRegisterCallback();
        }
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        d.mBound = d.mCtx.bindService(it, d.mConn, Context.BIND_AUTO_CREATE);
        return d;
    }

    private void tryRegisterCallback() {
        if (mService == null || mCallbackRegistered) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeStrongBinder(mCallback);
            mService.transact(13, data, reply, 0); // TRANSACTION_registerBackLightCallback = 13
            reply.readException();
            mCallbackRegistered = true;
        } catch (Throwable ignored) {
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public void addListener(DisplayListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(DisplayListener l) { mListeners.remove(l); }

    /** Get current screen backlight brightness (0–100) (TXN=9). */
    public int getBackLight() { return transactReadInt(9, -1); }

    /** Get maximum screen backlight value (TXN=7). */
    public int getMaxBackLight() { return transactReadInt(7, 100); }

    /** Get minimum screen backlight value (TXN=8). */
    public int getMinBackLight() { return transactReadInt(8, 0); }

    /** Set screen backlight brightness (0–100) with default notification flag (TXN=3). */
    public boolean setBackLight(int brightness) {
        return setBackLight(brightness, FLAG_DEFAULT);
    }

    /** Set screen backlight brightness (0–100) and notification flag (TXN=3). */
    public boolean setBackLight(int brightness, int flag) {
        return transactTwoIntBool(3, brightness, flag);
    }

    /** Get current dimmer mode (0=night, 1=day, 2=auto) (TXN=11). */
    public int getDimmer() { return transactReadInt(11, -1); }

    /** Set dimmer mode (TXN=4). */
    public boolean setDimmer(int dimmer, int flag) {
        return transactTwoIntBool(4, dimmer, flag);
    }

    /** Turn head-unit display screen off or on (TXN=5). */
    public boolean setScreenOff(boolean off) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(off ? 1 : 0);
            mService.transact(5, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Check whether the screen is currently off (TXN=6). */
    public boolean isScreenOff() {
        return transactReadInt(6, 0) != 0;
    }

    /** Check whether the LCD panel is overheating (TXN=10). */
    public boolean isLcdHot() {
        return transactReadInt(10, 0) != 0;
    }

    /** Reset display policy settings (TXN=12). */
    public boolean reset() {
        return transactVoid(12);
    }

    /** Get physical keypad backlight level (TXN=29). */
    public int getKeyPadBacklightLevel() { return transactReadInt(29, -1); }

    /** Set physical keypad backlight level (TXN=30). */
    public int setKeyPadBacklightLevel(int level) {
        return transactOneIntInt(30, level);
    }

    /** Turn on physical keypad backlight (TXN=31). */
    public boolean openKeypadBackLight() { return transactVoid(31); }

    /** Turn off physical keypad backlight (TXN=32). */
    public boolean closeKeypadBacklight() { return transactVoid(32); }

    /** Turn on front defrost button backlight (TXN=33). */
    public boolean openFrontDefrostKeypadBacklight() { return transactVoid(33); }

    /** Turn off front defrost button backlight (TXN=34). */
    public boolean closeFrontDefrostKeypadBacklight() { return transactVoid(34); }

    /** Turn on rear defrost button backlight (TXN=35). */
    public boolean openRearDefrostKeypadBacklight() { return transactVoid(35); }

    /** Turn off rear defrost button backlight (TXN=36). */
    public boolean closeRearDefrostKeypadBacklight() { return transactVoid(36); }

    /** Re-enable a previously disabled touch area (TXN=24). */
    public int enableTouchArea(int areaId) { return transactOneIntInt(24, areaId); }

    /** Disable a specific rectangular touch area on the screen (TXN=25). */
    public int disableTouchArea(int areaId, int x, int y, int width, int height) {
        if (mService == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(areaId);
            data.writeInt(x);
            data.writeInt(y);
            data.writeInt(width);
            data.writeInt(height);
            mService.transact(25, data, reply, 0);
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
        if (mCallbackRegistered && mService != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeStrongBinder(mCallback);
                mService.transact(14, data, reply, 0); // TRANSACTION_unregisterBackLightCallback = 14
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

    private int transactOneIntInt(int txn, int arg) {
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

    private boolean transactTwoIntBool(int txn, int a1, int a2) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(a1);
            data.writeInt(a2);
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
