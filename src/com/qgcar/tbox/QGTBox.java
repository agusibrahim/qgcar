package com.qgcar.tbox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.qgcar.bus.internal.RawBinderProbe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client for the vehicle's Telematics Box ({@code com.qinggan.tbox.service / TBoxService},
 * service manager name {@code "dls_tbox_service"}).
 *
 * Provides access to vehicle VIN, IMEI, ICCID, IMSI, 4G cellular signal & operator,
 * in-car Wi-Fi hotspot SSID/password, E-Fence configuration, E-Call/B-Call/I-Call,
 * and raw T-Box communication.
 */
public final class QGTBox {

    private static final String SVC_MGR    = "dls_tbox_service";
    private static final String SVC_PKG    = "com.qinggan.tbox.service";
    private static final String SVC_ACTION = "com.qinggan.tbox.action.START_TBOX_SERVICE";
    private static final String DESCRIPTOR = "com.qinggan.tbox.ITBoxService";
    private static final String CB_DESC    = "com.qinggan.tbox.ITBoxListener";

    public static final int NET_NO_NETWORK = 0;
    public static final int NET_CONNECTING = 1;
    public static final int NET_2G         = 2;
    public static final int NET_3G         = 3;
    public static final int NET_4G         = 4;
    public static final int NET_AUTO       = 5;

    public static final int SIM_UNAVAILABLE = 0;
    public static final int SIM_INSERTED    = 1;

    private final Context mCtx;
    private final CopyOnWriteArrayList<TBoxListener> mListeners = new CopyOnWriteArrayList<>();
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
                case 1: {
                    data.enforceInterface(CB_DESC);
                    int netType = data.readInt();
                    String op = data.readString();
                    String ip = data.readString();
                    int status = data.readInt();
                    for (TBoxListener l : mListeners) {
                        try { l.onNetworkStateChanged(netType, op, ip, status); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 2: {
                    data.enforceInterface(CB_DESC);
                    int bars = data.readInt();
                    for (TBoxListener l : mListeners) {
                        try { l.onNetworkSignalStrengthChanged(bars); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 3: {
                    data.enforceInterface(CB_DESC);
                    int callType = data.readInt();
                    int callState = data.readInt();
                    int cbMode = data.readInt();
                    for (TBoxListener l : mListeners) {
                        try { l.onCallStateChanged(callType, callState, cbMode); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 4: {
                    data.enforceInterface(CB_DESC);
                    long tx = data.readLong();
                    long rx = data.readLong();
                    for (TBoxListener l : mListeners) {
                        try { l.onTxRxChanged(tx, rx); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 5: {
                    data.enforceInterface(CB_DESC);
                    String name = data.readString();
                    double lon = data.readDouble();
                    double lat = data.readDouble();
                    for (TBoxListener l : mListeners) {
                        try { l.onPoiNotify(name, lon, lat); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 6: {
                    data.enforceInterface(CB_DESC);
                    int status = data.readInt();
                    for (TBoxListener l : mListeners) {
                        try { l.onCheckFOTAResponse(status); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 7: {
                    data.enforceInterface(CB_DESC);
                    int state = data.readInt();
                    for (TBoxListener l : mListeners) {
                        try { l.onUpgradeStateChanged(state); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 8: {
                    data.enforceInterface(CB_DESC);
                    boolean ok = data.readInt() != 0;
                    for (TBoxListener l : mListeners) {
                        try { l.onUpgradeResult(ok); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 9: {
                    data.enforceInterface(CB_DESC);
                    boolean on = data.readInt() != 0;
                    for (TBoxListener l : mListeners) {
                        try { l.onDatacomSwitchStateChanged(on); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 10: {
                    data.enforceInterface(CB_DESC);
                    boolean on = data.readInt() != 0;
                    for (TBoxListener l : mListeners) {
                        try { l.onWlanSwitchStateChanged(on); } catch (Throwable ignored) {}
                    }
                    if (reply != null) reply.writeNoException();
                    return true;
                }
                case 11: {
                    data.enforceInterface(CB_DESC);
                    boolean conn = data.readInt() != 0;
                    for (TBoxListener l : mListeners) {
                        try { l.onTBoxConnectedStateChanged(conn); } catch (Throwable ignored) {}
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

    private QGTBox(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }

    public static QGTBox connect(Context ctx) {
        QGTBox t = new QGTBox(ctx);
        IBinder sys = RawBinderProbe.getSystemServiceBinder(SVC_MGR);
        if (sys != null) {
            t.mService = sys;
            t.tryRegisterCallback();
        }
        Intent it = new Intent(SVC_ACTION);
        it.setPackage(SVC_PKG);
        t.mBound = t.mCtx.bindService(it, t.mConn, Context.BIND_AUTO_CREATE);
        return t;
    }

    private void tryRegisterCallback() {
        if (mService == null || mCallbackRegistered) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeString(mCtx.getPackageName());
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

    public void addListener(TBoxListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(TBoxListener l) { mListeners.remove(l); }

    // ---- Vehicle & T-Box Identity ----

    /** Get Vehicle Identification Number (VIN) (TXN=29). */
    public String getVIN() { return transactReadString(29); }

    /** Get T-Box cellular IMEI (TXN=26). */
    public String getIMEI() { return transactReadString(26); }

    /** Get SIM card ICCID (TXN=22). */
    public String getICCID() { return transactReadString(22); }

    /** Get SIM card IMSI (TXN=17). */
    public String getIMSI() { return transactReadString(17); }

    /** Get SIM card phone number (MSISDN) (TXN=24). */
    public String getMSISDN() { return transactReadString(24); }

    /** Get Product Device Serial Number (PDSN) (TXN=25). */
    public String getPDSN() { return transactReadString(25); }

    /** Get T-Box serial number (TXN=23). */
    public String getSerialNumber() { return transactReadString(23); }

    /** Get T-Box ID (TXN=27). */
    public String getTBoxId() { return transactReadString(27); }

    /** Get T-Box supplier ID (TXN=28). */
    public String getSupplierId() { return transactReadString(28); }

    /** Get T-Box hardware version (TXN=21). */
    public String getHWVersion() { return transactReadString(21); }

    /** Get T-Box software version (TXN=20). */
    public String getSWVersion() { return transactReadString(20); }

    /** Get T-Box MCU version (TXN=18). */
    public String getMCUVersion() { return transactReadString(18); }

    /** Get T-Box MPU version (TXN=19). */
    public String getMPUVersion() { return transactReadString(19); }

    // ---- Cellular Network & SIM ----

    /** Check if T-Box hardware is connected to head-unit (TXN=31). */
    public boolean isTBoxConnected() { return transactReadInt(31, 0) != 0; }

    /** Get SIM card status (1=inserted, 0=unavailable) (TXN=16). */
    public int getSimCardStatus() { return transactReadInt(16, -1); }

    /** Get cellular network type (4=4G, 3=3G, 2=2G) (TXN=4). */
    public int getNetworkType() { return transactReadInt(4, -1); }

    /** Get cellular network connected status (TXN=5). */
    public int getNetworkConnected() { return transactReadInt(5, -1); }

    /** Get cellular network operator PLMN/name (TXN=6). */
    public String getNetworkOperator() { return transactReadString(6); }

    /** Get T-Box IP address (TXN=7). */
    public String getIPAddress() { return transactReadString(7); }

    /** Get cellular signal strength bars (0–5) (TXN=8). */
    public int getSignalStrength() { return transactReadInt(8, -1); }

    /** Get data activity TX/RX status (TXN=30). */
    public int getTxRxStatus() { return transactReadInt(30, -1); }

    /** Check if cellular data switch is enabled (TXN=38). */
    public boolean getDatacomSwitchStatus() { return transactReadInt(38, 0) != 0; }

    /** Enable or disable T-Box cellular data (TXN=37). */
    public boolean switchDatacom(boolean enable) { return transactWriteBool(37, enable); }

    // ---- In-Car Wi-Fi Hotspot (WLAN) ----

    /** Check if T-Box Wi-Fi hotspot is enabled (TXN=46). */
    public boolean getWlanSwitchStatus() { return transactReadInt(46, 0) != 0; }

    /** Enable or disable T-Box Wi-Fi hotspot (TXN=45). */
    public boolean switchWlan(boolean enable) { return transactWriteBool(45, enable); }

    /** Get in-car Wi-Fi hotspot SSID (TXN=51). */
    public String getWlanSsid() { return transactReadString(51); }

    /** Set in-car Wi-Fi hotspot SSID (TXN=50). */
    public boolean setWlanSsid(String ssid) { return transactWriteString(50, ssid); }

    /** Get in-car Wi-Fi hotspot password (TXN=49). */
    public String getWlanPassword() { return transactReadString(49); }

    /** Set in-car Wi-Fi hotspot password (TXN=48). */
    public boolean setWlanPassword(String password) { return transactWriteString(48, password); }

    /** Get connected hotspot client devices Bundle (TXN=47). */
    public Bundle getConnectedDevices() {
        if (mService == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(47, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0 ? Bundle.CREATOR.createFromParcel(reply) : null;
        } catch (Throwable t) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    // ---- E-Fence & Emergency Calls (E-Call / B-Call / I-Call) ----

    /** Check if electronic fence is enabled (TXN=40). */
    public boolean getEFenceEnable() { return transactReadInt(40, 0) != 0; }

    /** Get electronic fence range in meters/km (TXN=41). */
    public int getEFenceRange() { return transactReadInt(41, -1); }

    /** Get electronic fence G-sensor sensitivity (TXN=42). */
    public int getEFenceSensitivity() { return transactReadInt(42, -1); }

    /** Configure electronic fence (TXN=39). */
    public boolean setEFenceStatus(boolean enable, int range, int sensitivity) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(enable ? 1 : 0);
            data.writeInt(range);
            data.writeInt(sensitivity);
            mService.transact(39, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Get T-Box call type (TXN=9). */
    public int getCallType() { return transactReadInt(9, -1); }

    /** Get T-Box call status (TXN=10). */
    public int getCallStatus() { return transactReadInt(10, -1); }

    /** Trigger B-Call (roadside assistance) (TXN=33). */
    public boolean requestBCall() { return transactReadInt(33, 0) != 0; }

    /** Trigger I-Call (information call) (TXN=34). */
    public boolean requestICall() { return transactReadInt(34, 0) != 0; }

    /** Trigger E-Call (emergency SOS) (TXN=12). */
    public boolean requireECall() { return transactReadInt(12, 0) != 0; }

    /** Abort E-Call (TXN=14). */
    public boolean abortECall() { return transactReadInt(14, 0) != 0; }

    // ---- Raw T-Box Data ----

    /** Send string payload to T-Box (TXN=53). */
    public boolean sendStringDataToTBox(int type, String payload) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(type);
            data.writeString(payload);
            mService.transact(53, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0;
        } catch (Throwable t) {
            return false;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    /** Send byte array payload to T-Box (TXN=54). */
    public boolean sendByteArrayDataToTBox(int type, byte[] payload) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(type);
            data.writeByteArray(payload);
            mService.transact(54, data, reply, 0);
            reply.readException();
            return reply.readInt() != 0;
        } catch (Throwable t) {
            return false;
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
                data.writeString(mCtx.getPackageName());
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

    // ---- Internal helpers ----

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

    private String transactReadString(int txn) {
        if (mService == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mService.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readString();
        } catch (Throwable t) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private boolean transactWriteBool(int txn, boolean val) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(val ? 1 : 0);
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

    private boolean transactWriteString(int txn, String val) {
        if (mService == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeString(val);
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
}
