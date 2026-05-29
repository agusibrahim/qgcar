package com.qgcar.bus;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;

import com.qgcar.bus.internal.ParcelDecoder;
import com.qgcar.bus.internal.RawBinderProbe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * High-level CAN bus client. Listens to {@code com.qinggan.canbus.service /
 * CanBusService} via raw Binder and decodes well-known events into typed
 * callbacks.
 *
 * Usage:
 * <pre>{@code
 *   QGBus bus = QGBus.connect(ctx);
 *   bus.addListener(new BusListener() {
 *       @Override public void onDoorStatusChanged(DoorStatus s) { ... }
 *       @Override public void onGearStatusChanged(Gear g)       { ... }
 *       @Override public void onVehicleSpeedChanged(int kmh)    { ... }
 *   });
 *   ...
 *   bus.disconnect();
 * }</pre>
 */
public final class QGBus {

    private static final String SVC_PKG  = "com.qinggan.canbus.service";
    private static final String SVC_ACT  = "com.qinggan.canbus.CanBusService";
    private static final String SVC_DESC = "com.qinggan.canbus.ICanBusService";
    private static final String CB_DESC  = "com.qinggan.canbus.ICanBusServiceCallback";

    private final RawBinderProbe mProbe;
    private final CopyOnWriteArrayList<BusListener> mListeners = new CopyOnWriteArrayList<>();
    private volatile AirCondition mLatestAC;
    private final BusListener mFanout = new BusListener() {
        @Override public void onDoorStatusChanged(DoorStatus s)        { for (BusListener l : mListeners) l.onDoorStatusChanged(s); }
        @Override public void onAirConditionChanged(AirCondition ac)    {
            mLatestAC = ac;
            for (BusListener l : mListeners) l.onAirConditionChanged(ac);
        }
        @Override public void onLightStatusChanged(LightStatus s)       { for (BusListener l : mListeners) l.onLightStatusChanged(s); }
        @Override public void onVehicleSpeedChanged(int kmh)            { for (BusListener l : mListeners) l.onVehicleSpeedChanged(kmh); }
        @Override public void onEngineSpeedChanged(int rpm)             { for (BusListener l : mListeners) l.onEngineSpeedChanged(rpm); }
        @Override public void onGearStatusChanged(Gear g)               { for (BusListener l : mListeners) l.onGearStatusChanged(g); }
        @Override public void onHandBrakeStatusChanged(boolean e)       { for (BusListener l : mListeners) l.onHandBrakeStatusChanged(e); }
        @Override public void onAmbientTemperatureChanged(int c)        { for (BusListener l : mListeners) l.onAmbientTemperatureChanged(c); }
        @Override public void onSeatBeltChanged(SeatBelts s)            { for (BusListener l : mListeners) l.onSeatBeltChanged(s); }
        @Override public void onWindowStatusChanged(WindowStatus w)     { for (BusListener l : mListeners) l.onWindowStatusChanged(w); }
        @Override public void onFuelLevelChanged(FuelLevel f)           { for (BusListener l : mListeners) l.onFuelLevelChanged(f); }
        @Override public void onOdometerChanged(Odometer o)             { for (BusListener l : mListeners) l.onOdometerChanged(o); }
        @Override public void onSWCAngleChanged(SteeringAngle a)        { for (BusListener l : mListeners) l.onSWCAngleChanged(a); }
        @Override public void onIlluminationChanged(int level)          { for (BusListener l : mListeners) l.onIlluminationChanged(level); }
        @Override public void onEngineTemperatureChanged(int c)         { for (BusListener l : mListeners) l.onEngineTemperatureChanged(c); }
        @Override public void onAccStateChanged(AccState s)            { for (BusListener l : mListeners) l.onAccStateChanged(s); }
        @Override public void onEngineStatusChanged(EngineState s)      { for (BusListener l : mListeners) l.onEngineStatusChanged(s); }
        @Override public void onRawEvent(int code, String n, byte[] p)  { for (BusListener l : mListeners) l.onRawEvent(code, n, p); }

        // ---- Extended fanout (additive, backward compatible) ----
        @Override public void onTpmsInfoChanged(TpmsInfo t)          { for (BusListener l : mListeners) l.onTpmsInfoChanged(t); }
        @Override public void onRadarDataChanged(RadarData r)        { for (BusListener l : mListeners) l.onRadarDataChanged(r); }
        @Override public void onBatteryStateChanged(BatteryState b)  { for (BusListener l : mListeners) l.onBatteryStateChanged(b); }
        @Override public void onWheelSpeedChanged(WheelSpeed w)      { for (BusListener l : mListeners) l.onWheelSpeedChanged(w); }
        @Override public void onAdasWarningChanged(AdasWarning a)    { for (BusListener l : mListeners) l.onAdasWarningChanged(a); }
        @Override public void onHevStatusChanged(HevStatus h)        { for (BusListener l : mListeners) l.onHevStatusChanged(h); }
        @Override public void onParkStateChanged(ParkState p2)       { for (BusListener l : mListeners) l.onParkStateChanged(p2); }
        @Override public void onDvrStateChanged(DvrState d)          { for (BusListener l : mListeners) l.onDvrStateChanged(d); }
        @Override public void onTravellingInfoChanged(TravellingInfo i){ for (BusListener l : mListeners) l.onTravellingInfoChanged(i); }
        @Override public void onFuelConsumptionChanged(FuelConsumption f){ for (BusListener l : mListeners) l.onFuelConsumptionChanged(f); }
        @Override public void onRainSensorChanged(RainSensor r2)     { for (BusListener l : mListeners) l.onRainSensorChanged(r2); }
        @Override public void onAlarmDataChanged(AlarmData a2)       { for (BusListener l : mListeners) l.onAlarmDataChanged(a2); }
        @Override public void onVehicleInfoChanged(VehicleInfoChanged v){ for (BusListener l : mListeners) l.onVehicleInfoChanged(v); }
        @Override public void onInstrumentClusterChanged(InstrumentCluster c){ for (BusListener l : mListeners) l.onInstrumentClusterChanged(c); }
        @Override public void onSecondaryOdometerChanged(SecondaryOdometer o){ for (BusListener l : mListeners) l.onSecondaryOdometerChanged(o); }
        @Override public void onEngineFluidStatusChanged(EngineFluidStatus e){ for (BusListener l : mListeners) l.onEngineFluidStatusChanged(e); }
    };

    private QGBus(Context ctx) {
        mProbe = new RawBinderProbe(ctx, SVC_PKG, SVC_ACT, SVC_DESC, CB_DESC,
            CanBusCodes.TXN_addCallback,
            new RawBinderProbe.Sink() {
                @Override public void onEvent(int code, byte[] payload) {
                    ParcelDecoder.dispatch(code, payload, mFanout);
                }
            });
    }

    public static QGBus connect(Context ctx) {
        QGBus b = new QGBus(ctx);
        b.mProbe.start();
        return b;
    }

    public void addListener(BusListener l)    { if (l != null) mListeners.add(l); }
    public void removeListener(BusListener l) { mListeners.remove(l); }

    /**
     * Set specific Air Conditioning state.
     * @param state the parameter enum to set.
     * @param value the integer value to apply.
     * @return true if binder transaction succeeded.
     */
    public boolean setAirConditionState(AirConditionState state, int value) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            // Write AirConditionState (non-null Penanda = 1, ordinal, state)
            data.writeInt(1);
            data.writeInt(state.ordinal());
            data.writeInt(state.getState());
            // Write value
            data.writeInt(value);
            // TRANSACTION_setAirConditionState = 31
            binder.transact(31, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Set specific general Vehicle state (e.g. windows, sunroof, seat ventilation).
     * @param state the parameter enum to set.
     * @param value the integer value to apply.
     * @return true if binder transaction succeeded.
     */
    public boolean setVehicleState(VehicleState state, int value) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            // Write VehicleState (non-null Penanda = 1, ordinal, value)
            data.writeInt(1);
            data.writeInt(state.ordinal());
            data.writeInt(state.getValue());
            // Write value
            data.writeInt(value);
            // TRANSACTION_setVehicleState = 55
            binder.transact(55, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Get the value of a specific general Vehicle parameter.
     * @param state the parameter enum to query.
     * @return the parameter value, or -1 if query failed.
     */
    public int getVehicleState(VehicleState state) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            // Write VehicleState (non-null Penanda = 1, ordinal, value)
            data.writeInt(1);
            data.writeInt(state.ordinal());
            data.writeInt(state.getValue());
            // TRANSACTION_getVehicleState = 54
            binder.transact(54, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Get the value of a specific Air Conditioning parameter.
     * @param state the parameter enum to query.
     * @return the parameter value, or -1 if query failed.
     */
    public int getAirConditionState(AirConditionState state) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return -1;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            // Write AirConditionState (non-null Penanda = 1, ordinal, state)
            data.writeInt(1);
            data.writeInt(state.ordinal());
            data.writeInt(state.getState());
            // TRANSACTION_getAirConditionState = 33
            binder.transact(33, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } catch (Throwable t) {
            return -1;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Fetch the full status of the vehicle Air Conditioning system.
     * @return full AirCondition state, or null if fetch failed.
     */
    public AirCondition getAirCondition() {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return null;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            // TRANSACTION_getAirCondition = 30
            binder.transact(30, data, reply, 0);
            reply.readException();
            if (reply.readInt() != 0) {
                AirCondition ac = AirCondition.CREATOR.createFromParcel(reply);
                mLatestAC = ac;
                return ac;
            }
            return null;
        } catch (Throwable t) {
            return null;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Get the latest cached AirCondition state.
     */
    public AirCondition getLatestAirCondition() {
        return mLatestAC;
    }

    /**
     * Turn AC power ON or OFF.
     */
    public boolean setAcPower(boolean on) {
        return setAirConditionState(AirConditionState.AC_POWER_SWITCH, on ? 1 : 0);
    }

    /**
     * Set driver (left) temperature directly (e.g. 24.5f).
     */
    public boolean setLeftTemperature(float temp) {
        if (temp < 17.0f) temp = 17.0f;
        if (temp > 33.0f) temp = 33.0f;
        return setAirConditionState(AirConditionState.AC_LEFT_TEMP, (int)(temp * 10));
    }

    /**
     * Set passenger (right) temperature directly (e.g. 24.5f).
     */
    public boolean setRightTemperature(float temp) {
        if (temp < 17.0f) temp = 17.0f;
        if (temp > 33.0f) temp = 33.0f;
        return setAirConditionState(AirConditionState.AC_RIGHT_TEMP, (int)(temp * 10));
    }

    /**
     * Set blower/wind speed directly (1 to 8).
     */
    public boolean setWindSpeed(int speed) {
        if (speed < 1) speed = 1;
        if (speed > 8) speed = 8;
        return setAirConditionState(AirConditionState.AC_BLOWER, speed);
    }

    /**
     * Adjust driver (left) temperature by delta (e.g. +0.5 or -0.5).
     */
    public boolean adjustLeftTemperature(float delta) {
        AirCondition ac = mLatestAC;
        if (ac == null) ac = getAirCondition();
        if (ac == null) return false;
        return setLeftTemperature(ac.getAirLeftTemperature() + delta);
    }

    /**
     * Adjust blower/wind speed by delta (e.g. +1 or -1).
     */
    public boolean adjustWindSpeed(int delta) {
        AirCondition ac = mLatestAC;
        if (ac == null) ac = getAirCondition();
        if (ac == null) return false;
        return setWindSpeed(ac.getAirWindSpeed() + delta);
    }

    /**
     * Open or close a specific vehicle window (full open/close only).
     * Note: Window percent control (0-100%) is NOT supported by this vehicle's CanBus.
     * Only full open (1) and full close (0) commands are confirmed working.
     * @param windowIndex 0 for Driver Front-Left, 1 for Passenger Front-Right, 2 for Rear-Left, 3 for Rear-Right.
     * @param open true to open, false to close.
     * @return true if binder transaction succeeded.
     */
    public boolean setWindowOpen(int windowIndex, boolean open) {
        VehicleState state;
        switch (windowIndex) {
            case 0: state = VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH; break;
            case 1: state = VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH; break;
            case 2: state = VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH; break;
            case 3: state = VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH; break;
            default: return false;
        }
        return setVehicleState(state, open ? 1 : 0);
    }

    /**
     * Set open percentage of a specific vehicle window.
     * WARNING: NOT CONFIRMED WORKING on Wuling headunit.
     * The launcher only uses open/close switch, not percent.
     * @param windowIndex 0 for Driver Front-Left, 1 for Passenger Front-Right, 2 for Rear-Left, 3 for Rear-Right.
     * @param percent 0 (fully closed) to 100 (fully open).
     * @return true if binder transaction succeeded.
     */
    public boolean setWindowPercent(int windowIndex, int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        VehicleState state;
        switch (windowIndex) {
            case 0: state = VehicleState.DRIVER_POWER_WINDOW_CONTROL_PERCENT; break;
            case 1: state = VehicleState.PASSENGER_POWER_WINDOW_CONTROL_PERCENT; break;
            case 2: state = VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_PERCENT; break;
            case 3: state = VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_PERCENT; break;
            default: return false;
        }
        return setVehicleState(state, percent);
    }

    /**
     * Open or close the power sunroof.
     * Value 1 = opens to preset position (~50% / tilted).
     * For FULL open, use setSunroofFullOpen() which uses Scene Mode 13 (Sky Mode).
     * @param open true to open (preset), false to close.
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofOpen(boolean open) {
        return setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH, open ? 1 : 0);
    }

    /**
     * Open sunroof to the FULL open position using the vehicle's Sky Scene Mode (mode 13).
     * This is the only confirmed way to fully open the sunroof.
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofFullOpen() {
        return setVehicleSceneMode(13);
    }

    /**
     * Close sunroof by resetting to base scene mode (mode 0).
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofClose() {
        return setVehicleSceneMode(0);
    }

    /**
     * Set open percentage of the power sunroof.
     * WARNING: NOT CONFIRMED WORKING on Wuling headunit.
     * The launcher uses scene modes (0/13), not percent.
     * @param percent 0 (fully closed) to 100 (fully open).
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofPercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        return setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT, percent);
    }

    /**
     * Tilt open or close the sunroof.
     * @param open true to tilt open, false to tilt close.
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofTiltOpen(boolean open) {
        return setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_TILT, open ? 1 : 0);
    }

    /**
     * Open or close the sunroof roll shade (tirai sunroof).
     * @param open true to open, false to close.
     * @return true if binder transaction succeeded.
     */
    public boolean setSunroofShadeOpen(boolean open) {
        return setVehicleState(VehicleState.SUNROOF_ROLLSHADE_CONTROL, open ? 1 : 0);
    }

    /**
     * Set vehicle scene mode. Used to trigger preset vehicle behaviours.
     * Known modes: 0=Base/Off, 1=Cool, 6=Rain/Snow(close windows), 7=Smoke(open windows), 13=Sky(full sunroof open).
     * @param mode the scene mode integer.
     * @return true if binder transaction succeeded.
     */
    public boolean setVehicleSceneMode(int mode) {
        IBinder binder = mProbe.getServiceBinder();
        if (binder == null) return false;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SVC_DESC);
            data.writeInt(mode);
            // TRANSACTION_setVehicleSceneMode = 69
            binder.transact(69, data, reply, 0);
            reply.readException();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public void disconnect() {
        mProbe.stop();
        mListeners.clear();
    }
}
