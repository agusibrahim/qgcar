package com.qgcar.bus.internal;

import android.os.Parcel;

import com.qgcar.bus.AccState;
import com.qgcar.bus.BusListener;
import com.qgcar.bus.CanBusCodes;
import com.qgcar.bus.DoorState;
import com.qgcar.bus.DoorStatus;
import com.qgcar.bus.EngineState;
import com.qgcar.bus.FuelLevel;
import com.qgcar.bus.Gear;
import com.qgcar.bus.Odometer;
import com.qgcar.bus.SeatBeltState;
import com.qgcar.bus.SeatBelts;
import com.qgcar.bus.SteeringAngle;
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

    private static DoorState readDoor(Parcel p) {
        return DoorState.fromRaw(p.readInt());
    }
}
