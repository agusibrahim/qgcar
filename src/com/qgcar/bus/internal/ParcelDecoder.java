package com.qgcar.bus.internal;

import android.os.Parcel;

import com.qgcar.bus.AccState;
import com.qgcar.bus.AdasWarning;
import com.qgcar.bus.AlarmData;
import com.qgcar.bus.BatteryState;
import com.qgcar.bus.BusListener;
import com.qgcar.bus.CanBusCodes;
import com.qgcar.bus.DoorState;
import com.qgcar.bus.DoorStatus;
import com.qgcar.bus.DvrState;
import com.qgcar.bus.EngineFluidStatus;
import com.qgcar.bus.EngineState;
import com.qgcar.bus.FuelConsumption;
import com.qgcar.bus.FuelLevel;
import com.qgcar.bus.Gear;
import com.qgcar.bus.HevStatus;
import com.qgcar.bus.InstrumentCluster;
import com.qgcar.bus.Odometer;
import com.qgcar.bus.ParkState;
import com.qgcar.bus.RadarData;
import com.qgcar.bus.RainSensor;
import com.qgcar.bus.SeatBeltState;
import com.qgcar.bus.SeatBelts;
import com.qgcar.bus.SecondaryOdometer;
import com.qgcar.bus.SteeringAngle;
import com.qgcar.bus.TpmsInfo;
import com.qgcar.bus.TravellingInfo;
import com.qgcar.bus.VehicleInfoChanged;
import com.qgcar.bus.WheelSpeed;
import com.qgcar.bus.WindowStatus;
import com.qgcar.bus.LightStatus;
import com.qgcar.bus.AirCondition;

/**
 * Decode known CAN bus parcel payloads into typed callbacks. Field order
 * is taken straight from createFromParcel() of the corresponding vendor
 * Parcelables in com.qinggan.canbus, so this should match wire-format
 * exactly.
 *
 * Anything we haven't implemented falls through onRawEvent only.
 */
public final class ParcelDecoder {
    private ParcelDecoder() {}

    public static void dispatch(int code, byte[] payload, BusListener cb) {
        String name = CanBusCodes.nameOf(code);
        try { cb.onRawEvent(code, name, payload); } catch (Throwable ignored) {}

        if (payload == null || payload.length == 0) return;
        Parcel p = Parcel.obtain();
        try {
            p.unmarshall(payload, 0, payload.length);
            p.setDataPosition(0);
            switch (code) {
                case CanBusCodes.onAirConditionChanged:
                    decodeAirCondition(p, cb); break;
                case CanBusCodes.onDoorStatusChanged:
                    decodeDoor(p, cb); break;
                case CanBusCodes.onVehicleSpeedChanged:
                    try { cb.onVehicleSpeedChanged(p.readInt()); } catch (Throwable ignored) {} break;
                case CanBusCodes.onEngineSpeedChanged:
                    try { cb.onEngineSpeedChanged(p.readInt()); } catch (Throwable ignored) {} break;
                case CanBusCodes.onHandBrakeStatusChanged:
                    try { cb.onHandBrakeStatusChanged(p.readInt() == 1); } catch (Throwable ignored) {} break;
                case CanBusCodes.onAmbientTemperatureChanged:
                    try { cb.onAmbientTemperatureChanged(p.readInt()); } catch (Throwable ignored) {} break;
                case CanBusCodes.onIlluminationChanged:
                    try { cb.onIlluminationChanged(p.readInt()); } catch (Throwable ignored) {} break;
                case CanBusCodes.onEngineTemperatureChanged:
                    try { cb.onEngineTemperatureChanged(p.readInt()); } catch (Throwable ignored) {} break;
                case CanBusCodes.onAccStateChanged:
                    try { cb.onAccStateChanged(AccState.fromRaw(p.readInt())); } catch (Throwable ignored) {} break;
                case CanBusCodes.onEngineStatusChanged:
                    try { cb.onEngineStatusChanged(EngineState.fromRaw(p.readInt())); } catch (Throwable ignored) {} break;
                case CanBusCodes.onGearStatusChanged:
                    decodeGear(p, cb); break;
                case CanBusCodes.onSeatBeltChanged:
                    decodeSeatBelt(p, cb); break;
                case CanBusCodes.onWindowsStatusChanged:
                    decodeWindow(p, cb); break;
                case CanBusCodes.onFuelLevelChanged:
                    decodeFuel(p, cb); break;
                case CanBusCodes.onOdometerChanged:
                    decodeOdometer(p, cb); break;
                case CanBusCodes.onSWCAngleChanged:
                    decodeSwc(p, cb); break;
                case CanBusCodes.onLightStatusChanged:
                    decodeLight(p, cb); break;

                // ---- Extended decoders (additive, backward compatible) ----

                case CanBusCodes.onTPMSInfoChange:
                    decodeTpms(p, cb); break;
                case CanBusCodes.onRadarDataChanged:
                    decodeRadar(p, cb); break;
                case CanBusCodes.onBatteryStateChanged:
                    decodeBattery(p, cb); break;
                case CanBusCodes.onWheelSpeedChanged:
                    decodeWheelSpeed(p, cb); break;
                case CanBusCodes.onDVRStateChenaged:
                    decodeDvr(p, cb); break;
                case CanBusCodes.onParkStateChanged:
                    decodePark(p, cb); break;
                case CanBusCodes.onHEVSystemModelChanged:
                    decodeHev(p, cb); break;
                case CanBusCodes.onTravellingInfo:
                    decodeTravelling(p, cb); break;
                case CanBusCodes.onEngineFluidStatusChanged:
                case CanBusCodes.onBrakePadStatusChanged:
                case CanBusCodes.onBrakeFluidStatusChanged:
                case CanBusCodes.onWiperFluidStatusChanged:
                    decodeEngineFluid(p, cb, code); break;
                case CanBusCodes.onFuelConsumptionChanged:
                case CanBusCodes.onInstantaneousFuelConsumptionChanged:
                    decodeFuelConsumption(p, cb); break;
                case CanBusCodes.onRainFallLevelChanged:
                    decodeRain(p, cb); break;
                case CanBusCodes.onAlarmDataChanged:
                    decodeAlarm(p, cb); break;
                case CanBusCodes.onRealityWarningInfoChange:
                    decodeAdas(p, cb); break;
                case CanBusCodes.onVehicleInfoChanged:
                    decodeVehicleInfo(p, cb); break;
                case CanBusCodes.onInstrumentClusterInfoChanged:
                    decodeInstrumentCluster(p, cb); break;
                case CanBusCodes.onSecondaryOdometerChanged:
                    decodeSecondaryOdometer(p, cb); break;
                default:
                    break;
            }
        } catch (Throwable ignored) {
        } finally {
            p.recycle();
        }
    }

    private static void decodeDoor(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        // Field order from DoorStatus.createFromParcel: bonnet, fL, fLLock, fR,
        // fRLock, loadSpace, rL, rLLock, rR, rRLock.
        DoorState bonnet = readDoor(p);
        DoorState fL = readDoor(p);
        DoorState fLLock = readDoor(p);
        DoorState fR = readDoor(p);
        DoorState fRLock = readDoor(p);
        DoorState trunk = readDoor(p);
        DoorState rL = readDoor(p);
        DoorState rLLock = readDoor(p);
        DoorState rR = readDoor(p);
        DoorState rRLock = readDoor(p);
        DoorStatus ds = new DoorStatus(bonnet, fL, fLLock, fR, fRLock,
            trunk, rL, rLLock, rR, rRLock);
        try { cb.onDoorStatusChanged(ds); } catch (Throwable ignored) {}
    }

    private static void decodeGear(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        // GearState.writeToParcel writes ordinal() then value -- two ints.
        int ord = p.readInt();
        try { p.readInt(); } catch (Throwable ignored) {} // discard 'value'
        Gear g = Gear.fromOrdinal(ord);
        try { cb.onGearStatusChanged(g); } catch (Throwable ignored) {}
    }

    private static void decodeSeatBelt(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        int driver = p.readInt();
        int pass = p.readInt();
        SeatBelts sb = new SeatBelts(SeatBeltState.fromRaw(driver),
            SeatBeltState.fromRaw(pass));
        try { cb.onSeatBeltChanged(sb); } catch (Throwable ignored) {}
    }

    private static void decodeWindow(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        DoorState fL = readDoor(p);
        DoorState fR = readDoor(p);
        DoorState rL = readDoor(p);
        DoorState rR = readDoor(p);
        DoorState sunroof = readDoor(p);
        DoorState roll = readDoor(p);
        float sunPos = p.readFloat();
        float rollPos = p.readFloat();
        float fLPos = p.readFloat();
        float fRPos = p.readFloat();
        float rLPos = p.readFloat();
        float rRPos = p.readFloat();
        int tilt = p.readInt();
        WindowStatus w = new WindowStatus(fL, fR, rL, rR, sunroof, roll,
            sunPos, rollPos, fLPos, fRPos, rLPos, rRPos, tilt);
        try { cb.onWindowStatusChanged(w); } catch (Throwable ignored) {}
    }

    private static void decodeFuel(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        int capacity = p.readInt();
        int remain = p.readInt();
        float pct = p.readFloat();
        boolean shortage = p.readInt() == 1;
        float instant = p.readFloat();
        float avg = p.readFloat();
        float histAvg = p.readFloat();
        float resetAvg = p.readFloat();
        FuelLevel f = new FuelLevel(capacity, remain, pct, shortage,
            instant, avg, histAvg, resetAvg);
        try { cb.onFuelLevelChanged(f); } catch (Throwable ignored) {}
    }

    private static void decodeOdometer(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        float canTravel = p.readFloat();
        float evRange = p.readFloat();
        float endurance = p.readFloat();
        float power = p.readFloat();
        float chargeRemain = p.readFloat();
        float total = p.readFloat();
        Odometer odo = new Odometer(canTravel, evRange, endurance, power, chargeRemain, total);
        try { cb.onOdometerChanged(odo); } catch (Throwable ignored) {}
    }

    private static void decodeSwc(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        int dir = p.readInt();
        int angle = p.readInt();
        SteeringAngle a = new SteeringAngle(SteeringAngle.Direction.fromRaw(dir), angle);
        try { cb.onSWCAngleChanged(a); } catch (Throwable ignored) {}
    }

    private static void decodeLight(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        LightStatus ls = LightStatus.CREATOR.createFromParcel(p);
        try { cb.onLightStatusChanged(ls); } catch (Throwable ignored) {}
    }

    private static void decodeAirCondition(Parcel p, BusListener cb) {
        p.readInt(); // Skip presence token
        AirCondition ac = AirCondition.CREATOR.createFromParcel(p);
        try { cb.onAirConditionChanged(ac); } catch (Throwable ignored) {}
    }

    // ---- Extended decoder methods ----

    private static void decodeTpms(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float flP = p.readFloat();
        float frP = p.readFloat();
        float rlP = p.readFloat();
        float rrP = p.readFloat();
        float flT = p.readFloat();
        float frT = p.readFloat();
        float rlT = p.readFloat();
        float rrT = p.readFloat();
        int flS = p.readInt();
        int frS = p.readInt();
        int rlS = p.readInt();
        int rrS = p.readInt();
        TpmsInfo tpms = new TpmsInfo(flP, frP, rlP, rrP, flT, frT, rlT, rrT, flS, frS, rlS, rrS);
        try { cb.onTpmsInfoChanged(tpms); } catch (Throwable ignored) {}
    }

    private static void decodeRadar(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int fl = p.readInt();
        int fcl = p.readInt();
        int fcr = p.readInt();
        int fr = p.readInt();
        int rl = p.readInt();
        int rcl = p.readInt();
        int rcr = p.readInt();
        int rr = p.readInt();
        RadarData radar = new RadarData(fl, fcl, fcr, fr, rl, rcl, rcr, rr);
        try { cb.onRadarDataChanged(radar); } catch (Throwable ignored) {}
    }

    private static void decodeBattery(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float voltage = p.readFloat();
        int health = p.readInt();
        int charge = p.readInt();
        BatteryState bat = new BatteryState(voltage, health, charge);
        try { cb.onBatteryStateChanged(bat); } catch (Throwable ignored) {}
    }

    private static void decodeWheelSpeed(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float fl = p.readFloat();
        float fr = p.readFloat();
        float rl = p.readFloat();
        float rr = p.readFloat();
        WheelSpeed ws = new WheelSpeed(fl, fr, rl, rr);
        try { cb.onWheelSpeedChanged(ws); } catch (Throwable ignored) {}
    }

    private static void decodeDvr(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int rec = p.readInt();
        int storage = p.readInt();
        int error = p.readInt();
        DvrState dvr = new DvrState(rec, storage, error);
        try { cb.onDvrStateChanged(dvr); } catch (Throwable ignored) {}
    }

    private static void decodePark(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int active = p.readInt();
        int dir = p.readInt();
        int type = p.readInt();
        ParkState park = new ParkState(active, dir, type);
        try { cb.onParkStateChanged(park); } catch (Throwable ignored) {}
    }

    private static void decodeHev(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int mode = p.readInt();
        int soc = p.readInt();
        int flow = p.readInt();
        int recovery = p.readInt();
        int power = p.readInt();
        HevStatus hev = new HevStatus(mode, soc, flow, recovery, power);
        try { cb.onHevStatusChanged(hev); } catch (Throwable ignored) {}
    }

    private static void decodeTravelling(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float dist = p.readFloat();
        float dur = p.readFloat();
        float fuel = p.readFloat();
        float avgFuel = p.readFloat();
        float avgSpd = p.readFloat();
        TravellingInfo info = new TravellingInfo(dist, dur, fuel, avgFuel, avgSpd);
        try { cb.onTravellingInfoChanged(info); } catch (Throwable ignored) {}
    }

    private static void decodeEngineFluid(Parcel p, BusListener cb, int code) {
        p.readInt(); // presence token
        // Combined from multiple events — each event may carry different subset
        int oil = (code == CanBusCodes.onEngineFluidStatusChanged) ? p.readInt() : 0;
        int coolant = (code == CanBusCodes.onEngineFluidStatusChanged) ? p.readInt() : 0;
        int brakePad = (code == CanBusCodes.onBrakePadStatusChanged) ? p.readInt() : 0;
        int brakeFluid = (code == CanBusCodes.onBrakeFluidStatusChanged) ? p.readInt() : 0;
        int wiperFluid = (code == CanBusCodes.onWiperFluidStatusChanged) ? p.readInt() : 0;
        EngineFluidStatus fluid = new EngineFluidStatus(oil, coolant, brakePad, brakeFluid, wiperFluid);
        try { cb.onEngineFluidStatusChanged(fluid); } catch (Throwable ignored) {}
    }

    private static void decodeFuelConsumption(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float instant = p.readFloat();
        float avg = p.readFloat();
        float hist = p.readFloat();
        float resetAvg = p.readFloat();
        FuelConsumption fc = new FuelConsumption(instant, avg, hist, resetAvg);
        try { cb.onFuelConsumptionChanged(fc); } catch (Throwable ignored) {}
    }

    private static void decodeRain(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int level = p.readInt();
        RainSensor rain = new RainSensor(level);
        try { cb.onRainSensorChanged(rain); } catch (Throwable ignored) {}
    }

    private static void decodeAlarm(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int type = p.readInt();
        int state = p.readInt();
        AlarmData alarm = new AlarmData(type, state);
        try { cb.onAlarmDataChanged(alarm); } catch (Throwable ignored) {}
    }

    private static void decodeAdas(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int fcw = p.readInt();
        int aeb = p.readInt();
        int ldw = p.readInt();
        int bsd = p.readInt();
        int rcta = p.readInt();
        int tsrSpeed = p.readInt();
        int tsrUnit = p.readInt();
        AdasWarning adas = new AdasWarning(fcw, aeb, ldw, bsd, rcta, tsrSpeed, tsrUnit);
        try { cb.onAdasWarningChanged(adas); } catch (Throwable ignored) {}
    }

    private static void decodeVehicleInfo(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int type = p.readInt();
        int v1 = p.readInt();
        int v2 = p.readInt();
        int v3 = p.readInt();
        VehicleInfoChanged info = new VehicleInfoChanged(type, v1, v2, v3);
        try { cb.onVehicleInfoChanged(info); } catch (Throwable ignored) {}
    }

    private static void decodeInstrumentCluster(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        int bright = p.readInt();
        int theme = p.readInt();
        int dimmer = p.readInt();
        InstrumentCluster cluster = new InstrumentCluster(bright, theme, dimmer);
        try { cb.onInstrumentClusterChanged(cluster); } catch (Throwable ignored) {}
    }

    private static void decodeSecondaryOdometer(Parcel p, BusListener cb) {
        p.readInt(); // presence token
        float tripA = p.readFloat();
        float tripB = p.readFloat();
        SecondaryOdometer odo = new SecondaryOdometer(tripA, tripB);
        try { cb.onSecondaryOdometerChanged(odo); } catch (Throwable ignored) {}
    }

    private static DoorState readDoor(Parcel p) {
        return DoorState.fromRaw(p.readInt());
    }
}
