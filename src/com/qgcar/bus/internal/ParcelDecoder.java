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
        if (p.readInt() == 0) return;
        DoorStatus ds = readDoorStatus(p);
        try { cb.onDoorStatusChanged(ds); } catch (Throwable ignored) {}
    }

    public static DoorStatus readDoorStatus(Parcel p) {
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
        return new DoorStatus(bonnet, fL, fLLock, fR, fRLock,
            trunk, rL, rLLock, rR, rRLock);
    }

    private static void decodeGear(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        Gear g = readGear(p);
        try { cb.onGearStatusChanged(g); } catch (Throwable ignored) {}
    }

    public static Gear readGear(Parcel p) {
        // GearState.writeToParcel writes ordinal() then value -- two ints.
        int ord = p.readInt();
        try { p.readInt(); } catch (Throwable ignored) {} // discard 'value'
        return Gear.fromOrdinal(ord);
    }

    private static void decodeSeatBelt(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        SeatBelts sb = readSeatBelts(p);
        try { cb.onSeatBeltChanged(sb); } catch (Throwable ignored) {}
    }

    public static SeatBelts readSeatBelts(Parcel p) {
        int driver = p.readInt();
        int pass = p.readInt();
        return new SeatBelts(SeatBeltState.fromRaw(driver),
            SeatBeltState.fromRaw(pass));
    }

    private static void decodeWindow(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        WindowStatus w = readWindowStatus(p);
        try { cb.onWindowStatusChanged(w); } catch (Throwable ignored) {}
    }

    public static WindowStatus readWindowStatus(Parcel p) {
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
        return new WindowStatus(fL, fR, rL, rR, sunroof, roll,
            sunPos, rollPos, fLPos, fRPos, rLPos, rRPos, tilt);
    }

    private static void decodeFuel(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        FuelLevel f = readFuelLevel(p);
        try { cb.onFuelLevelChanged(f); } catch (Throwable ignored) {}
    }

    public static FuelLevel readFuelLevel(Parcel p) {
        int capacity = p.readInt();
        int remain = p.readInt();
        float pct = p.readFloat();
        boolean shortage = p.readInt() == 1;
        float instant = p.readFloat();
        float avg = p.readFloat();
        float histAvg = p.readFloat();
        float resetAvg = p.readFloat();
        return new FuelLevel(capacity, remain, pct, shortage,
            instant, avg, histAvg, resetAvg);
    }

    private static void decodeOdometer(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        Odometer odo = readOdometer(p);
        try { cb.onOdometerChanged(odo); } catch (Throwable ignored) {}
    }

    public static Odometer readOdometer(Parcel p) {
        float canTravel = p.readFloat();
        float evRange = p.readFloat();
        float endurance = p.readFloat();
        float power = p.readFloat();
        float chargeRemain = p.readFloat();
        float total = p.readFloat();
        return new Odometer(canTravel, evRange, endurance, power, chargeRemain, total);
    }

    private static void decodeSwc(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        SteeringAngle a = readSteeringAngle(p);
        try { cb.onSWCAngleChanged(a); } catch (Throwable ignored) {}
    }

    public static SteeringAngle readSteeringAngle(Parcel p) {
        int dir = p.readInt();
        int angle = p.readInt();
        return new SteeringAngle(SteeringAngle.Direction.fromRaw(dir), angle);
    }

    private static void decodeLight(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        LightStatus ls = LightStatus.CREATOR.createFromParcel(p);
        try { cb.onLightStatusChanged(ls); } catch (Throwable ignored) {}
    }

    private static void decodeAirCondition(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        AirCondition ac = AirCondition.CREATOR.createFromParcel(p);
        try { cb.onAirConditionChanged(ac); } catch (Throwable ignored) {}
    }

    // ---- Extended decoder methods ----

    private static void decodeTpms(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        TpmsInfo tpms = readTpmsInfo(p);
        try { cb.onTpmsInfoChanged(tpms); } catch (Throwable ignored) {}
    }

    public static TpmsInfo readTpmsInfo(Parcel p) {
        // Exact order from com.qinggan.canbus.TPMSInfo$1.createFromParcel:
        // mSystemStatus, 4 warning ints (FL, FR, RL, RR),
        // 4 temperature floats (FL, FR, RL, RR), 4 pressure floats (FL, FR, RL, RR).
        int sysStatus = p.readInt();
        int flS = p.readInt();
        int frS = p.readInt();
        int rlS = p.readInt();
        int rrS = p.readInt();
        float flT = p.readFloat();
        float frT = p.readFloat();
        float rlT = p.readFloat();
        float rrT = p.readFloat();
        float flP = p.readFloat();
        float frP = p.readFloat();
        float rlP = p.readFloat();
        float rrP = p.readFloat();
        return new TpmsInfo(flP, frP, rlP, rrP, flT, frT, rlT, rrT, flS, frS, rlS, rrS);
    }

    private static void decodeRadar(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        RadarData radar = readRadarData(p);
        try { cb.onRadarDataChanged(radar); } catch (Throwable ignored) {}
    }

    public static RadarData readRadarData(Parcel p) {
        // RadarData$1.createFromParcel starts with readParcelable(RadarType):
        // String className + int ordinal + int value (when non-null), followed by
        // 4 distance levels and 4 distances.
        String radarTypeClass = p.readString();
        int radarTypeOrd = 0;
        if (radarTypeClass != null) {
            radarTypeOrd = p.readInt();
            p.readInt(); // value
        }
        int l1 = p.readInt();
        int l2 = p.readInt();
        int l3 = p.readInt();
        int l4 = p.readInt();
        int d1 = p.readInt();
        int d2 = p.readInt();
        int d3 = p.readInt();
        int d4 = p.readInt();
        // Use distance if populated, otherwise level
        int v1 = (d1 > 0 && d1 != 2147483647) ? d1 : l1;
        int v2 = (d2 > 0 && d2 != 2147483647) ? d2 : l2;
        int v3 = (d3 > 0 && d3 != 2147483647) ? d3 : l3;
        int v4 = (d4 > 0 && d4 != 2147483647) ? d4 : l4;
        if (radarTypeOrd == 1) { // Rear radar
            return new RadarData(0, 0, 0, 0, v1, v2, v3, v4);
        }
        return new RadarData(v1, v2, v3, v4, 0, 0, 0, 0);
    }

    private static void decodeBattery(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        BatteryState bat = readBatteryState(p);
        try { cb.onBatteryStateChanged(bat); } catch (Throwable ignored) {}
    }

    public static BatteryState readBatteryState(Parcel p) {
        // BatteryState$1.createFromParcel reads mVoltageLevel (int), mVoltageValue (int)
        int voltageLevel = p.readInt();
        int voltageRaw = p.readInt();
        float voltage = (voltageRaw > 100) ? (voltageRaw / 10.0f) : (float) voltageRaw;
        return new BatteryState(voltage, voltageLevel, voltageRaw);
    }

    private static void decodeWheelSpeed(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        WheelSpeed ws = readWheelSpeed(p);
        try { cb.onWheelSpeedChanged(ws); } catch (Throwable ignored) {}
    }

    public static WheelSpeed readWheelSpeed(Parcel p) {
        float fl = p.readFloat();
        float fr = p.readFloat();
        float rl = p.readFloat();
        float rr = p.readFloat();
        return new WheelSpeed(fl, fr, rl, rr);
    }

    private static void decodeDvr(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        DvrState dvr = readDvrState(p);
        try { cb.onDvrStateChanged(dvr); } catch (Throwable ignored) {}
    }

    public static DvrState readDvrState(Parcel p) {
        int rec = p.readInt();
        int wifi = p.readInt();
        int photo = p.readInt();
        return new DvrState(rec, wifi, photo);
    }

    private static void decodePark(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 7: onParkStateChanged(p.readInt(), p.readInt())
        int active = p.readInt();
        int dir = p.dataAvail() >= 4 ? p.readInt() : 0;
        int type = p.dataAvail() >= 4 ? p.readInt() : 0;
        ParkState park = new ParkState(active, dir, type);
        try { cb.onParkStateChanged(park); } catch (Throwable ignored) {}
    }

    private static void decodeHev(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 35: onHEVSystemModelChanged(p.readInt())
        int mode = p.readInt();
        int soc = p.dataAvail() >= 4 ? p.readInt() : 0;
        int flow = p.dataAvail() >= 4 ? p.readInt() : 0;
        int recovery = p.dataAvail() >= 4 ? p.readInt() : 0;
        int power = p.dataAvail() >= 4 ? p.readInt() : 0;
        HevStatus hev = new HevStatus(mode, soc, flow, recovery, power);
        try { cb.onHevStatusChanged(hev); } catch (Throwable ignored) {}
    }

    private static void decodeTravelling(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 38: TravellingInfoType + float
        if (p.readInt() != 0) {
            p.readInt(); // ordinal
            if (p.dataAvail() >= 8) p.readInt(); // value
        }
        float val = p.dataAvail() >= 4 ? p.readFloat() : 0f;
        TravellingInfo info = new TravellingInfo(val, 0f, 0f, 0f, 0f);
        try { cb.onTravellingInfoChanged(info); } catch (Throwable ignored) {}
    }

    private static void decodeEngineFluid(Parcel p, BusListener cb, int code) {
        int val = p.readInt();
        int oil = (code == CanBusCodes.onEngineFluidStatusChanged) ? val : 0;
        int coolant = 0;
        int brakePad = (code == CanBusCodes.onBrakePadStatusChanged && p.dataAvail() >= 4) ? p.readInt() : 0;
        int brakeFluid = (code == CanBusCodes.onBrakeFluidStatusChanged) ? val : 0;
        int wiperFluid = (code == CanBusCodes.onWiperFluidStatusChanged) ? val : 0;
        EngineFluidStatus fluid = new EngineFluidStatus(oil, coolant, brakePad, brakeFluid, wiperFluid);
        try { cb.onEngineFluidStatusChanged(fluid); } catch (Throwable ignored) {}
    }

    private static void decodeFuelConsumption(Parcel p, BusListener cb) {
        float instant = p.readFloat();
        float avg = p.dataAvail() >= 4 ? p.readFloat() : 0f;
        float hist = p.dataAvail() >= 4 ? p.readFloat() : 0f;
        float resetAvg = p.dataAvail() >= 4 ? p.readFloat() : 0f;
        FuelConsumption fc = new FuelConsumption(instant, avg, hist, resetAvg);
        try { cb.onFuelConsumptionChanged(fc); } catch (Throwable ignored) {}
    }

    private static void decodeRain(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 49: onRainFallLevelChanged(p.readInt())
        int level = p.readInt();
        RainSensor rain = new RainSensor(level);
        try { cb.onRainSensorChanged(rain); } catch (Throwable ignored) {}
    }

    private static void decodeAlarm(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 33: onAlarmDataChanged(p.readInt())
        int type = p.readInt();
        int state = p.dataAvail() >= 4 ? p.readInt() : 0;
        AlarmData alarm = new AlarmData(type, state);
        try { cb.onAlarmDataChanged(alarm); } catch (Throwable ignored) {}
    }

    private static void decodeAdas(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        int fcw = p.readInt();
        int aeb = p.dataAvail() >= 4 ? p.readInt() : 0;
        int ldw = p.dataAvail() >= 4 ? p.readInt() : 0;
        int bsd = p.dataAvail() >= 4 ? p.readInt() : 0;
        int rcta = p.dataAvail() >= 4 ? p.readInt() : 0;
        int tsrSpeed = p.dataAvail() >= 4 ? p.readInt() : 0;
        int tsrUnit = p.dataAvail() >= 4 ? p.readInt() : 0;
        AdasWarning adas = new AdasWarning(fcw, aeb, ldw, bsd, rcta, tsrSpeed, tsrUnit);
        try { cb.onAdasWarningChanged(adas); } catch (Throwable ignored) {}
    }

    private static void decodeVehicleInfo(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        int type = p.readInt();
        int v1 = p.dataAvail() >= 4 ? p.readInt() : 0;
        int v2 = p.dataAvail() >= 4 ? p.readInt() : 0;
        int v3 = p.dataAvail() >= 4 ? p.readInt() : 0;
        VehicleInfoChanged info = new VehicleInfoChanged(type, v1, v2, v3);
        try { cb.onVehicleInfoChanged(info); } catch (Throwable ignored) {}
    }

    private static void decodeInstrumentCluster(Parcel p, BusListener cb) {
        if (p.readInt() == 0) return;
        InstrumentCluster cluster = readInstrumentCluster(p);
        try { cb.onInstrumentClusterChanged(cluster); } catch (Throwable ignored) {}
    }

    public static InstrumentCluster readInstrumentCluster(Parcel p) {
        int bright = p.readInt();
        int theme = p.dataAvail() >= 4 ? p.readInt() : 0;
        int dimmer = p.dataAvail() >= 4 ? p.readInt() : 0;
        return new InstrumentCluster(bright, theme, dimmer);
    }

    private static void decodeSecondaryOdometer(Parcel p, BusListener cb) {
        // ICanBusServiceCallback$Stub case 30: onSecondaryOdometerChanged(p.readInt())
        float tripA = p.readInt();
        float tripB = p.dataAvail() >= 4 ? p.readFloat() : 0f;
        SecondaryOdometer odo = new SecondaryOdometer(tripA, tripB);
        try { cb.onSecondaryOdometerChanged(odo); } catch (Throwable ignored) {}
    }

    private static DoorState readDoor(Parcel p) {
        return DoorState.fromRaw(p.readInt());
    }
}
