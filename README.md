# qgcar — Vendor Car Hardware Library

A lightweight Java library exposing car-specific hardware on Qinggan
(Roewe / SAIC / MSM8953-based) head-units to ordinary Android apps:

- **Camera (AVM)** — 4 surround-view cameras via the Qualcomm AIS bridge,
  with built-in fisheye correction.
- **Around View Monitor (AVM)** — show/hide the vendor's panoramic overlay,
  query reverse & parking-sensor state, forward touch events.
- **Steering-wheel keys** — intercept and override SWC buttons (VOICE,
  VOL+/-, MEDIA NEXT/PREV, etc.) before the default vendor handler.
- **CAN bus events** — door/lock status, gear, vehicle speed, RPM, ambient
  temp, lights, windows, seat-belts, fuel, odometer, steering angle,
  air-conditioning, and 50+ other vehicle signals.
- **Vehicle control** — write back to the CAN bus: windows, sunroof,
  AC temperature/blower, seat heating, ambient lights, scene modes, and
  200+ enumerated vehicle state parameters.

**Compatibility:** tested on Roewe / Wuling-branded head-units with Qualcomm
MSM8953 SoC and Qinggan vendor stack. Anything with the same
`KeyManagerService`, `CanBusService`, `SystemPolicyService`, and
`/vendor/lib/libaiscamera_jni.so` should work.

The library does not embed any native code or framework jars — it talks to
existing vendor services and binds against `libaiscamera_jni.so` already
present on the device.

---

## Add to your app

Drop `build/qgcar.jar` into `libs/` and reference it from your build.

For ad-hoc Android projects (no Gradle), just include the source — there
are no resources to merge.

In your manifest, target SDK 23 so the linker namespace lets you load the
vendor JNI lib:

```xml
<uses-sdk android:minSdkVersion="21" android:targetSdkVersion="23"/>

<queries>
    <package android:name="com.qinggan.keymanager.service" />
    <package android:name="com.qinggan.canbus.service" />
    <package android:name="com.qinggan.carsignal.service" />
    <package android:name="com.qinggan.systempolicy" />
</queries>
```

---

## Camera

```java
QGCamera cam = QGCamera.open();             // CSI 0, 4 channels
cam.setFisheyeCorrector(FisheyeCorrector.R_MILD);

cam.subscribe(CameraChannel.CH0, new FrameListener() {
    @Override public void onBitmap(CameraChannel ch, Bitmap bmp) {
        runOnUiThread(() -> imageView.setImageBitmap(bmp));
    }
});

// when frames go green / sliced:
cam.resetStream();

// later:
cam.close();
```

`FisheyeCorrector` presets:

| preset                | k1     | notes                            |
|-----------------------|--------|----------------------------------|
| `OFF`                 | 0      | raw fisheye                      |
| `R_MILD` (default)    | -0.30  | safe; minor barrel removal       |
| `R_MED`               | -0.55  | flatter; edges blur a bit        |
| `R_STRONG`            | -0.80  | very flat; obvious edge stretch  |

You can also build your own with custom `(k1, k2, fScale, asymY)`.

---

## Around View Monitor (AVM)

```java
QGAVM avm = QGAVM.connect(ctx);

avm.addListener(new AvmListener() {
    @Override public void onAvmStateChanged(int state) {
        // STATE_IN=1, STATE_OUT=0
    }
    @Override public void onReverseChanged(int state) {
        // 1=in reverse, 0=not
    }
    @Override public void onPdcStateChanged(int state) {
        // parking sensor: 1=active, 0=inactive
    }
});

avm.show();                              // show AVM overlay
avm.hide();                              // hide AVM overlay
avm.showAVM(QGAVM.AVM_SHOW_REVERSE);    // show in reverse mode

// forward touch to AVM surface
avm.touchEvent(QGAVM.TOUCH_DOWN, x, y, 0, 0, 0);

// calibration, brightness, contrast
avm.sendMsg(QGAVM.MSG_SET_BRIGHT, value, 0);

// later:
avm.disconnect();
```

AVM show modes: `AVM_HIDE=0`, `AVM_SHOW=1`, `AVM_SHOW_REVERSE=2`,
`AVM_HIDE_FOR_SPEED=3`, `AVM_NO_VIDEO=255`.

---

## Steering-wheel keys

```java
QGKeys keys = QGKeys.connect(this, connected -> {
    if (connected) {
        keys.setPolicyDisabled(true);   // optional, suppress default handlers
    }
});

keys.addListener(event -> {
    if (event.code == QGKeyCode.VOICE && event.isDown()) {
        startActivity(new Intent(this, MyVoiceActivity.class));
        return true;   // swallow it; vendor voice service won't fire
    }
    return false;
});

// later:
keys.disconnect();
```

Listener callback returns `true` to consume the event before it reaches
the default handler. Common keycodes are in `QGKeyCode`:

| Constant          | Value | Button                        |
|-------------------|-------|-------------------------------|
| `VOICE`           | 130   | Voice assistant               |
| `MEDIA_NEXT`      | 3     | Media next                    |
| `MEDIA_PREV`      | 4     | Media previous                |
| `MEDIA_PLAY`      | 8     | Play                          |
| `MEDIA_PAUSE`     | 9     | Pause                         |
| `MUTE`            | 5     | Mute                          |
| `PHONE`           | 128   | Phone accept                  |
| `HANGUP`          | 176   | Phone hang-up                 |
| `HOME`            | 32    | Home                          |
| `BACK`            | 33    | Back                          |
| `MODE`            | 64    | Mode switch                   |
| `SRC`             | 65    | Source switch                  |
| `SWS_LEFT`        | 49    | Scroll-wheel left             |
| `SWS_RIGHT`       | 50    | Scroll-wheel right            |
| `SWS_UP_WHEEL`    | 51    | Scroll-wheel up               |
| `SWS_DOWN_WHEEL`  | 52    | Scroll-wheel down             |

---

## CAN bus — reading events

```java
QGBus bus = QGBus.connect(this);
bus.addListener(new BusListener() {
    @Override public void onDoorStatusChanged(DoorStatus s) {
        if (s.anyDoorOpen()) showWarning();
    }
    @Override public void onGearStatusChanged(Gear g) {
        if (g == Gear.REVERSE) startAvm();
    }
    @Override public void onVehicleSpeedChanged(int kmh) { /* ... */ }
    @Override public void onEngineSpeedChanged(int rpm)  { /* ... */ }
    @Override public void onSeatBeltChanged(SeatBelts sb) {
        if (sb.driver == SeatBeltState.UNBUCKLED) chime();
    }
    @Override public void onAirConditionChanged(AirCondition ac) {
        Log.d("car", "AC left=" + ac.getAirLeftTemperature()
            + " right=" + ac.getAirRightTemperature()
            + " wind=" + ac.getAirWindSpeed());
    }
    @Override public void onLightStatusChanged(LightStatus ls) {
        Log.d("car", "Turn signals: " + ls.getTurnSignalState());
    }
    @Override public void onWindowStatusChanged(WindowStatus w) {
        Log.d("car", "Sunroof: " + w.getSunroofState());
    }
    @Override public void onFuelLevelChanged(FuelLevel f) { /* ... */ }
    @Override public void onOdometerChanged(Odometer o) { /* ... */ }
    @Override public void onSWCAngleChanged(SteeringAngle a) {
        Log.d("car", "wheel " + a.signedDeg() + "°");
    }

    // extended events
    @Override public void onTpmsInfoChanged(TpmsInfo tpms) {
        Log.d("car", "FL tire=" + tpms.flPressure + "kPa/" + tpms.flTemp + "°C");
    }
    @Override public void onRadarDataChanged(RadarData r) {
        Log.d("car", "rear-left=" + r.rl + " rear-right=" + r.rr);
    }
    @Override public void onAdasWarningChanged(AdasWarning a) {
        if (a.fcw != 0) showFcwWarning();
    }
    @Override public void onHevStatusChanged(HevStatus h) {
        Log.d("car", "EV SOC=" + h.soc + "%");
    }

    // catch-all for anything not yet decoded
    @Override public void onRawEvent(int code, String name, byte[] payload) {
        Log.d("car", "raw " + name + " (" + code + ") len=" + payload.length);
    }
});

// later:
bus.disconnect();
```

### Decoded events (typed callbacks)

#### Core events

| Callback                       | Payload type       | Notes                                |
|--------------------------------|--------------------|--------------------------------------|
| `onDoorStatusChanged`          | `DoorStatus`       | per-door + per-lock `DoorState` enum |
| `onGearStatusChanged`          | `Gear`             | P / R / N / D / B / S / Unknown      |
| `onVehicleSpeedChanged`        | `int kmh`          |                                      |
| `onEngineSpeedChanged`         | `int rpm`          |                                      |
| `onHandBrakeStatusChanged`     | `boolean engaged`  |                                      |
| `onAmbientTemperatureChanged`  | `int celsius`      |                                      |
| `onSeatBeltChanged`            | `SeatBelts`        | driver + passenger                   |
| `onWindowStatusChanged`        | `WindowStatus`     | per-window state + position + sunroof state |
| `onFuelLevelChanged`           | `FuelLevel`        | capacity, remain, %, instant/avg     |
| `onOdometerChanged`            | `Odometer`         | trip + total + EV cruising range     |
| `onSWCAngleChanged`            | `SteeringAngle`    | direction + degrees                  |
| `onIlluminationChanged`        | `int level`        |                                      |
| `onEngineTemperatureChanged`   | `int celsius`      |                                      |
| `onAccStateChanged`            | `AccState`         | ACC key-on state                     |
| `onEngineStatusChanged`        | `EngineState`      |                                      |
| `onAirConditionChanged`        | `AirCondition`     | full HVAC status (60+ fields)        |
| `onLightStatusChanged`         | `LightStatus`      | all exterior lights + turn signal state |

#### Extended events

| Callback                       | Payload type         | Notes                                |
|--------------------------------|----------------------|--------------------------------------|
| `onTpmsInfoChanged`            | `TpmsInfo`           | pressure + temp per wheel + status   |
| `onRadarDataChanged`           | `RadarData`          | 8-zone parking sensor distances      |
| `onBatteryStateChanged`        | `BatteryState`       | voltage, health, charge level        |
| `onWheelSpeedChanged`          | `WheelSpeed`         | per-wheel speed (float)              |
| `onAdasWarningChanged`         | `AdasWarning`        | FCW, AEB, LDW, BSD, RCTA, TSR       |
| `onHevStatusChanged`           | `HevStatus`          | HEV/EV mode, SOC, energy flow       |
| `onParkStateChanged`           | `ParkState`          | parking assist active/direction/type |
| `onDvrStateChanged`            | `DvrState`           | dashcam recording/storage/error      |
| `onTravellingInfoChanged`      | `TravellingInfo`     | trip dist, duration, fuel, avg speed |
| `onFuelConsumptionChanged`     | `FuelConsumption`    | instant/avg/historical consumption   |
| `onEngineFluidStatusChanged`   | `EngineFluidStatus`  | oil, coolant, brake/wiper fluid      |
| `onRainSensorChanged`          | `RainSensor`         | rainfall intensity level             |
| `onAlarmDataChanged`           | `AlarmData`          | vehicle alarm type + state           |
| `onVehicleInfoChanged`         | `VehicleInfoChanged` | generic vehicle info events          |
| `onInstrumentClusterChanged`   | `InstrumentCluster`  | brightness, theme, dimmer            |
| `onSecondaryOdometerChanged`   | `SecondaryOdometer`  | trip A + trip B                      |

`onRawEvent` still fires for every transaction as a catch-all.

---

## CAN bus — vehicle control (write)

### Window & sunroof

```java
QGBus bus = QGBus.connect(ctx);

// Windows — open / close (confirmed working)
bus.setWindowOpen(0, true);   // 0=Driver FL, 1=Passenger FR, 2=RL, 3=RR
bus.setWindowOpen(0, false);  // close driver window

// Sunroof — scene-mode control (confirmed working)
bus.setSunroofFullOpen();     // uses Sky Scene Mode (mode 13)
bus.setSunroofClose();        // uses base scene mode (mode 0)
bus.setSunroofTiltOpen(true); // tilt open
bus.setSunroofShadeOpen(true);// roll shade

// Percent control (NOT confirmed — vendor launcher only uses switch/scene)
bus.setWindowPercent(0, 50);  // ⚠ may not work on all HU
bus.setSunroofPercent(50);    // ⚠ may not work on all HU
```

### Air conditioning

```java
// Full AC state
AirCondition ac = bus.getAirCondition();
float leftTemp = ac.getAirLeftTemperature();

// Convenience methods
bus.setAcPower(true);                 // AC on
bus.setLeftTemperature(24.5f);        // driver zone (17–33 °C)
bus.setRightTemperature(22.0f);       // passenger zone
bus.setWindSpeed(5);                  // blower 1–8
bus.adjustLeftTemperature(+0.5f);     // relative adjust
bus.adjustWindSpeed(-1);              // relative adjust

// Low-level — any AirConditionState enum
bus.setAirConditionState(AirConditionState.AC_FRONT_DEFROST_SWITCH, 1);
bus.setAirConditionState(AirConditionState.AC_REAR_DEFROST_SWITCH, 1);
bus.setAirConditionState(AirConditionState.AC_RECIRC_AIR, 2);   // internal loop
```

### Vehicle state (200+ parameters)

```java
// Low-level set/get for any VehicleState enum
bus.setVehicleState(VehicleState.TRUNK_RELEASE, 1);
bus.setVehicleState(VehicleState.ATMOSPHERE_LAMP_GRADS, 3);
bus.setVehicleState(VehicleState.MAIN_SCREEN_BRIGHTNESS, 80);

int val = bus.getVehicleState(VehicleState.FRONT_WIPER_OUTPUT_STATUS);
```

### Scene modes

```java
// Trigger preset vehicle behaviours
bus.setVehicleSceneMode(0);   // Base / Off
bus.setVehicleSceneMode(1);   // Cool
bus.setVehicleSceneMode(6);   // Rain / Snow — auto-close windows
bus.setVehicleSceneMode(7);   // Smoke — open windows for ventilation
bus.setVehicleSceneMode(13);  // Sky — full sunroof open
```

---

## Why does this work?

Most of these capabilities aren't available through the standard Android
API. The vendor builds private services for:

- `com.qinggan.keymanager.service` — pre-dispatch key intercept
- `com.qinggan.canbus.service` — vehicle bus events + vehicle state control
- `com.qinggan.carsignal.service` — alternate signal channel
- `com.qinggan.systempolicy` — AVM overlay control (SystemPolicyService)
- `tvdecoderserver_h` — AVM camera frame broker via `libaiscamera_jni.so`

This library binds to these services using their AIDL descriptors and
listens to the relevant transactions. SELinux on these head-units is
typically Permissive, the vendor lib is world-readable in `/vendor/lib`,
and `targetSdkVersion=23` keeps the linker namespace lenient enough to
load the JNI bridge from a normal user app.

---

## Building

```bash
./build.sh   # produces build/qgcar.jar
```

Requires Android SDK with `platforms/android-29/android.jar` reachable
via `$ANDROID_HOME` (default: `~/Library/Android/sdk`).

---

## License

This library is provided as-is for research and personal use on compatible
head-units. The AIDL stubs and JNI bindings are derived from reverse-
engineering vendor firmware and are not covered by any upstream license.
