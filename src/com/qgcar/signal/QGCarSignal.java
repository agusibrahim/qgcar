package com.qgcar.signal;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;

import com.qgcar.bus.CarSignalCodes;
import com.qgcar.bus.internal.RawBinderProbe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client for {@code com.qinggan.carsignal.service / CarSignalService} ("qg.carsignal").
 * Exposes MCU hardware version, mainboard & LCD thermal status, 12V battery voltage,
 * antenna status (GPS/Tuner/DAB), keypad/LCD backlights, PA amplifier mute, RTC,
 * and vehicle hardware configuration.
 */
public final class QGCarSignal {

    private static final String SVC_MGR  = "qg.carsignal";
    private static final String SVC_PKG  = "com.qinggan.carsignal.service";
    private static final String SVC_ACT  = "com.qinggan.carsignal.CarSignalService";
    private static final String SVC_DESC = "com.qinggan.carsignal.ICarSignalService";
    private static final String CB_DESC  = "com.qinggan.carsignal.ICarSignalServiceCallBack";

    public static final int ACC_OFF   = 0;
    public static final int ACC_ON    = 1;
    public static final int ACC_RUN   = 2;
    public static final int ACC_CRANK = 3;
    public static final int ACC_SK    = 4;

    private final RawBinderProbe mProbe;
    private final CopyOnWriteArrayList<CarSignalListener> mListeners = new CopyOnWriteArrayList<>();

    private QGCarSignal(Context ctx) {
        mProbe = new RawBinderProbe(ctx, SVC_MGR, SVC_PKG, SVC_ACT, SVC_DESC, CB_DESC,
            CarSignalCodes.TXN_registerCallback,
            new RawBinderProbe.Sink() {
                @Override public void onEvent(int code, byte[] payload) {
                    dispatch(code, payload);
                }
            });
    }

    public static QGCarSignal connect(Context ctx) {
        QGCarSignal s = new QGCarSignal(ctx);
        s.mProbe.start();
        return s;
    }

    public void addListener(CarSignalListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(CarSignalListener l) { mListeners.remove(l); }

    private void dispatch(int code, byte[] payload) {
        String name = CarSignalCodes.nameOf(code);
        for (CarSignalListener l : mListeners) {
            try { l.onRawEvent(code, name, payload); } catch (Throwable ignored) {}
        }
        if (payload == null || payload.length < 4) return;
        Parcel p = Parcel.obtain();
        try {
            p.unmarshall(payload, 0, payload.length);
            p.setDataPosition(0);
            int v1 = p.readInt();
            int v2 = p.dataAvail() >= 4 ? p.readInt() : 0;
            for (CarSignalListener l : mListeners) {
                try {
                    switch (code) {
                        case CarSignalCodes.onKeyEvent: l.onKeyEvent(v1, v2); break;
                        case CarSignalCodes.onBatteryChanged: l.onBatteryChanged(v1); break;
                        case CarSignalCodes.onPowerStateChanged: l.onPowerStateChanged(v1); break;
                        case CarSignalCodes.onACCStateChanged: l.onAccStateChanged(v1); break;
                        case CarSignalCodes.onPowerKeyStateChanged: l.onPowerKeyStateChanged(v1); break;
                        case CarSignalCodes.onPADistortionStateChanged: l.onPADistortionStateChanged(v1); break;
                        case CarSignalCodes.onHeadLightStateChanged: l.onHeadLightStateChanged(v1); break;
                        case CarSignalCodes.onCameraStateChanged: l.onCameraStateChanged(v1); break;
                        case CarSignalCodes.onCarSafeStateChanged: l.onCarSafeStateChanged(v1); break;
                        case CarSignalCodes.onAVINStateChanged: l.onAVINStateChanged(v1); break;
                        case CarSignalCodes.onTouchScreenStateChanged: l.onTouchScreenStateChanged(v1); break;
                        case CarSignalCodes.onTemValueChanged: l.onTemValueChanged(v1); break;
                        case CarSignalCodes.onLightSensorChanged: l.onLightSensorChanged(v1); break;
                        case CarSignalCodes.onVolumeKeyChanged: l.onVolumeKeyChanged(v1); break;
                        case CarSignalCodes.onMuteKeyChanged: l.onMuteKeyChanged(v1); break;
                        case CarSignalCodes.onLightKeyChanged: l.onLightKeyChanged(v1); break;
                        case CarSignalCodes.onLCDTempChanged: l.onLCDTempChanged(v1); break;
                        case CarSignalCodes.onCanBusStateChanged: l.onCanBusStateChanged(v1); break;
                        case CarSignalCodes.onVoltageStateChanged: l.onVoltageStateChanged(v1); break;
                        case CarSignalCodes.onKeyChannelLine1ValueChanged: l.onKeyChannelLine1ValueChanged(v1); break;
                        case CarSignalCodes.onKeyChannelLine2ValueChanged: l.onKeyChannelLine2ValueChanged(v1); break;
                        case CarSignalCodes.OnBackLightStateChanged: l.onBackLightStateChanged(v1); break;
                        case CarSignalCodes.onMCUDiagRequest: l.onMCUDiagRequest(v1); break;
                        case CarSignalCodes.onBLEConnectStatusChanged: l.onBLEConnectStatusChanged(v1); break;
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {
        } finally {
            p.recycle();
        }
    }

    // ---- Thermal / Temperature APIs ----

    /** Get LCD temperature value (TXN=30). */
    public int getLCDTemp() { return transactInt(30, -1); }

    /** Check if mainboard is in high-temperature warning mode (TXN=48). */
    public boolean isInMainBoardHighTempMode() { return transactInt(48, 0) != 0; }

    /** Get mainboard high-temperature level (4 = high-temp icon shown on statusbar) (TXN=49). */
    public int getCurrentMBHighTempLevel() { return transactInt(49, -1); }

    /** Get current mainboard temperature value (TXN=69). */
    public int getCurrentMBHighTemp() { return transactInt(69, -1); }

    // ---- MCU & Vehicle Identity APIs ----

    /** Get MCU hardware version string (TXN=1). */
    public String getMCUHardwareVersion() { return transactString(1); }

    /** Get MCU software version string (TXN=2). */
    public String getMCUSoftwareVersion() { return transactString(2); }

    /** Get MCU software build date string (TXN=3). */
    public String getMCUSoftVersionDate() { return transactString(3); }

    /** Get CAN box version string (TXN=4). */
    public String getCanBoxVersion() { return transactString(4); }

    /** Get vehicle type ID (TXN=15). */
    public int getVehicleType() { return transactInt(15, -1); }

    /** Get MCU platform ID (0=toc007, 1=toc008, 2=toc008a) (TXN=23). */
    public int getMCUPlatform() { return transactInt(23, -1); }

    /** Get car type name string (TXN=64). */
    public String getCarType() { return transactString(64); }

    /** Get 10-element vehicle hardware configuration array (TXN=68). */
    public int[] getVehicleConfig() {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(68, data, reply, 0);
            reply.readException();
            return reply.createIntArray();
        } catch (Throwable t) {
            return null;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /** Update vehicle hardware configuration array (TXN=67). */
    public int setVehicleConfig(int[] config) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null || config == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            data.writeIntArray(config);
            binder.transact(67, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // ---- Power, Voltage & Battery APIs ----

    /** Get wakeup source code (TXN=5). */
    public int getWakeupSrc() { return transactInt(5, -1); }

    /** Get MCU power mode (TXN=17). */
    public int getMCUPowerMode() { return transactInt(17, -1); }

    /** Get 12V battery level code (TXN=18). */
    public int getBatteryLevel() { return transactInt(18, -1); }

    /** Get shutdown mode (0=door off, 1=ACC off) (TXN=22). */
    public int getShutdownMode() { return transactInt(22, -1); }

    /** Get voltage state (TXN=34). */
    public int getVoltageState() { return transactInt(34, -1); }

    /** Get AVN status (TXN=36). */
    public int getAVNStatus() { return transactInt(36, -1); }

    /** Get car key status (TXN=38). */
    public int getCarKeyStatus() { return transactInt(38, -1); }

    /** Power off the head-unit via MCU (TXN=58). */
    public boolean powerOff() { return transactVoid(58); }

    // ---- Backlight, Display, Touch & Audio Amplifier (PA) APIs ----

    /** Get ambient light sensor level (TXN=33). */
    public int getLightSensorLevel() { return transactInt(33, -1); }

    /** Get screen light status (TXN=39). */
    public int getScreenLightStatus() { return transactInt(39, -1); }

    /** Get headlamp status (TXN=40). */
    public int getHeadLampStatus() { return transactInt(40, -1); }

    /** Get side lamp status (TXN=41). */
    public int getSideLightStatus() { return transactInt(41, -1); }

    /** Get reverse gear signal state from MCU (TXN=42). */
    public int getReverseState() { return transactInt(42, -1); }

    /** Set physical keyboard backlight value (TXN=46). */
    public boolean setKeyBoardBackLightValue(int val) { return transactWriteInt(46, val); }

    /** Get physical keyboard backlight value (TXN=47). */
    public int getKeyBoardBackLightValue() { return transactInt(47, -1); }

    /** Set LCD board backlight value (TXN=52). */
    public boolean setLCDBoardLightValue(int val) { return transactWriteInt(52, val); }

    /** Get LCD board backlight value (TXN=53). */
    public int getLCDBoardLightValue() { return transactInt(53, -1); }

    /** Get PA amplifier mute state (TXN=54). */
    public int getPAMuteState() { return transactInt(54, -1); }

    /** Set PA amplifier mute state (TXN=55). */
    public boolean setPAMuteState(int state) { return transactWriteInt(55, state); }

    /** Get touchscreen state (TXN=56). */
    public int getTouchScreenState() { return transactInt(56, -1); }

    /** Set touchscreen enabled/disabled state (TXN=57). */
    public boolean setTouchScreenState(int state) { return transactWriteInt(57, state); }

    // ---- Antenna & Bus Status APIs ----

    /** Get radio tuner antenna state (TXN=19). */
    public int getTunerAnt() { return transactInt(19, -1); }

    /** Get GPS antenna state (TXN=20). */
    public int getGPSAnt() { return transactInt(20, -1); }

    /** Set GPS state (TXN=8). */
    public boolean setGPSState(int state) { return transactWriteInt(8, state); }

    /** Get DAB antenna state (TXN=28). */
    public int getDABAnt() { return transactInt(28, -1); }

    /** Get CAN bus state (TXN=21). */
    public int getCanBusState() { return transactInt(21, -1); }

    /** Get LIN bus state (TXN=27). */
    public int getLinBusState() { return transactInt(27, -1); }

    /** Get BLE connection status (TXN=70). */
    public int getBLEConnectStatus() { return transactInt(70, -1); }

    /** Get vehicle speed float from MCU (TXN=35). */
    public float getVehicleSpeed() {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return -1f;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(35, data, reply, 0);
            reply.readException();
            return reply.readFloat();
        } catch (Throwable t) {
            return -1f;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /** Get MCU RTC time in epoch seconds/ms (TXN=6). */
    public long getRTCTime() {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return -1L;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(6, data, reply, 0);
            reply.readException();
            return reply.readLong();
        } catch (Throwable t) {
            return -1L;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public void disconnect() {
        mProbe.stop();
        mListeners.clear();
    }

    // ---- Internal helpers ----

    private int transactInt(int txn, int defVal) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return defVal;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return defVal;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private String transactString(int txn) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(txn, data, reply, 0);
            reply.readException();
            return reply.readString();
        } catch (Throwable t) {
            return null;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private boolean transactWriteInt(int txn, int val) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            data.writeInt(val);
            binder.transact(txn, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private boolean transactVoid(int txn) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            binder.transact(txn, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
