# qgcar — Vendor Car Hardware Library

A lightweight Java library exposing car-specific hardware and system services on Qinggan / Pateo (Wuling / Roewe / SAIC / MSM8953-based) head-units to ordinary Android apps:

- **Camera (`QGCamera`)** — 4 surround-view cameras via the Qualcomm AIS bridge (`libaiscamera_jni.so`), with built-in fisheye correction.
- **Around View Monitor (`QGAVM`)** — show/hide the vendor's panoramic 360° overlay, query reverse & parking-sensor state, forward touch events.
- **Steering-Wheel Keys (`QGKeys`)** — intercept and override SWC buttons (`VOICE`, `VOL+/-`, `MEDIA NEXT/PREV`, etc.) before the default vendor handler.
- **CAN Bus Events & Synchronous Getters (`QGBus`)** — real-time callbacks and instant synchronous getters for door/lock status, gear, vehicle speed, RPM, ambient/engine/cabin temperature, lights, windows, seat-belts, fuel, odometer, steering angle, TPMS tire pressure/temp, radar, battery, ADAS, HEV/EV, and full 60+ field HVAC state.
- **Instrument Cluster / MID Push (`QGBus`)** — push Turn-by-Turn navigation directions (`NaviInfo`), media track/artist metadata (`MediaClusterInfo`), and phone call state (`PhoneClusterInfo`) from 3rd-party apps directly to the car's speedometer screen.
- **Vehicle & HVAC Control (`QGBus`)** — write back to the CAN bus: windows, sunroof, AC temperature/blower/defrost, seat heating/ventilation, ambient lights, scene modes (`SceneMode`), and 200+ `VehicleState` parameters.
- **MCU & Hardware Signals (`QGCarSignal`)** — query & listen to `CarSignalService`: mainboard/LCD temperature & overheat state, 12V battery voltage, MCU firmware version, wakeup source, GPS/Tuner/DAB antenna status, physical keypad/LCD backlights, PA amplifier mute, and hardware `VehicleConfig`.
- **Display & Backlight (`QGDisplay`)** — control screen brightness (0–100), day/night/auto dimmer, screen off/on, physical button backlights, and touch regions via `SystemPolicyService`.
- **DSP & Audio Policy (`QGAudio`)** — control 5-band Equalizer, cabin Balance/Fader soundstage, Subwoofer, Loudness, Arkamys 3D / DTS modes, speed-compensated volume, reverse audio ducking, Baby-in-Car mode, and per-stream volumes via `AudioPolicyService`.
- **Telematics Box (`QGTBox`)** — read vehicle VIN, IMEI, ICCID, IMSI, 4G signal strength, cellular operator, IP address, control in-car Wi-Fi hotspot SSID/password, E-Fence, and E-Call/B-Call via `TBoxService`.
- **Power Management (`QGPower`)** — monitor & control AVN power state, screensaver (clock lockscreen), screen-off, and standby display keep-alive via `PowerManagerService`.
- **Hardware FM/AM Tuner (`QGTuner`)** — control the hardware radio tuner chip: band switching, frequency tuning, station seeking, and signal quality metrics via `TunerService`.

**Compatibility:** tested on Wuling / Roewe / Baojun head-units with Qualcomm MSM8953 SoC and Qinggan vendor stack. The library does not embed any native code or framework jars — it resolves vendor services via `android.os.ServiceManager` and standard Android `bindService` calls.

---

## Add to your app

Drop `build/qgcar.jar` into `libs/` and reference it from your build.

In your `AndroidManifest.xml`, target SDK 23 so the linker namespace lets you load `/vendor/lib/libaiscamera_jni.so` (if using `QGCamera`), and declare package visibility if running on newer Android versions:

```xml
<uses-sdk android:minSdkVersion="21" android:targetSdkVersion="23"/>

<queries>
    <package android:name="com.qinggan.keymanager.service" />
    <package android:name="com.qinggan.canbus.service" />
    <package android:name="com.qinggan.carsignal.service" />
    <package android:name="com.qinggan.systempolicy" />
    <package android:name="com.qinggan.audiopolicy.service" />
    <package android:name="com.qinggan.tbox.service" />
    <package android:name="com.qinggan.powermanager" />
    <package android:name="com.qinggan.tuner.service" />
</queries>
```

---

## 1. Camera (`QGCamera`)

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

| Preset                | `k1`   | Notes                            |
|-----------------------|--------|----------------------------------|
| `OFF`                 | `0`    | Raw fisheye                      |
| `R_MILD` (default)    | `-0.30`| Safe; minor barrel removal       |
| `R_MED`               | `-0.55`| Flatter; edges blur a bit        |
| `R_STRONG`            | `-0.80`| Very flat; obvious edge stretch  |

---

## 2. Around View Monitor (`QGAVM`)

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

avm.disconnect();
```

---

## 3. Steering-Wheel Keys (`QGKeys`)

```java
QGKeys keys = QGKeys.connect(this, connected -> {
    if (connected) {
        keys.setPolicyDisabled(true);   // optional: suppress default vendor handlers
    }
});

keys.addListener(event -> {
    if (event.code == QGKeyCode.VOICE && event.isDown()) {
        startActivity(new Intent(this, MyVoiceActivity.class));
        return true;   // swallow event; vendor voice assistant won't fire
    }
    return false;
});

keys.disconnect();
```

Common keycodes in `QGKeyCode`:

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
| `SRC`             | 65    | Source switch                 |
| `SWS_LEFT`        | 49    | Scroll-wheel left             |
| `SWS_RIGHT`       | 50    | Scroll-wheel right            |
| `SWS_UP_WHEEL`    | 51    | Scroll-wheel up               |
| `SWS_DOWN_WHEEL`  | 52    | Scroll-wheel down             |

---

## 4. CAN Bus — Events, Synchronous Getters & Control (`QGBus`)

### 4.1 Real-Time Event Listener

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
    @Override public void onAmbientTemperatureChanged(int celsius) { /* ... */ }
    @Override public void onAirConditionChanged(AirCondition ac) {
        float outTemp  = ac.getAirTempOutCar();      // Outside temp shown on statusbar
        float inTemp   = ac.getAirTempInCar();       // Cabin temp
        float leftTemp = ac.getAirLeftTemperature(); // Driver AC target temp
        int windSpeed  = ac.getAirWindSpeed();       // Blower speed (0-8)
    }
    @Override public void onTpmsInfoChanged(TpmsInfo tpms) {
        Log.d("car", "FL tire=" + tpms.flPressure + "kPa / " + tpms.flTemp + "°C");
    }
    @Override public void onLightStatusChanged(LightStatus ls) { /* ... */ }
    @Override public void onWindowStatusChanged(WindowStatus w) { /* ... */ }
    @Override public void onFuelLevelChanged(FuelLevel f) { /* ... */ }
    @Override public void onOdometerChanged(Odometer o) { /* ... */ }
    @Override public void onSWCAngleChanged(SteeringAngle a) { /* ... */ }
});
```

#### Core & Extended Decoded Events (`BusListener`)

| Callback                       | Payload type         | Notes                                |
|--------------------------------|----------------------|--------------------------------------|
| `onDoorStatusChanged`          | `DoorStatus`         | Per-door + per-lock `DoorState` enum |
| `onGearStatusChanged`          | `Gear`               | P / R / N / D / B / S / Unknown      |
| `onVehicleSpeedChanged`        | `int kmh`            | Vehicle speed                        |
| `onEngineSpeedChanged`         | `int rpm`            | Engine RPM                           |
| `onHandBrakeStatusChanged`     | `boolean engaged`    | Handbrake engaged                    |
| `onAmbientTemperatureChanged`  | `int celsius`        | Outside temperature                  |
| `onSeatBeltChanged`            | `SeatBelts`          | Driver + passenger                   |
| `onWindowStatusChanged`        | `WindowStatus`       | Per-window state + position + sunroof|
| `onFuelLevelChanged`           | `FuelLevel`          | Capacity, remain, %, instant/avg     |
| `onOdometerChanged`            | `Odometer`           | Trip + total + EV cruising range     |
| `onSWCAngleChanged`            | `SteeringAngle`      | Direction + degrees                  |
| `onIlluminationChanged`        | `int level`          | Dashboard illumination               |
| `onEngineTemperatureChanged`   | `int celsius`        | Coolant temperature                  |
| `onAccStateChanged`            | `AccState`           | ACC key-on state                     |
| `onEngineStatusChanged`        | `EngineState`        | Engine running status                |
| `onAirConditionChanged`        | `AirCondition`       | Full HVAC status (60+ fields)        |
| `onLightStatusChanged`         | `LightStatus`        | Exterior lights + turn signals       |
| `onTpmsInfoChanged`            | `TpmsInfo`           | Pressure (kPa) + temp (°C) per wheel |
| `onRadarDataChanged`           | `RadarData`          | 8-zone parking sensor distances      |
| `onBatteryStateChanged`        | `BatteryState`       | 12V voltage, level, raw state        |
| `onWheelSpeedChanged`          | `WheelSpeed`         | Per-wheel speed (`float`)            |
| `onAdasWarningChanged`         | `AdasWarning`        | FCW, AEB, LDW, BSD, RCTA, TSR        |
| `onHevStatusChanged`           | `HevStatus`          | HEV/EV mode, SOC, energy flow        |
| `onParkStateChanged`           | `ParkState`          | Parking assist active/direction/type |
| `onDvrStateChanged`            | `DvrState`           | Dashcam recording/wifi/photo         |
| `onTravellingInfoChanged`      | `TravellingInfo`     | Trip dist, duration, fuel, avg speed |
| `onFuelConsumptionChanged`     | `FuelConsumption`    | Instant/avg/historical consumption   |
| `onEngineFluidStatusChanged`   | `EngineFluidStatus`  | Oil, coolant, brake/wiper fluid      |
| `onRainSensorChanged`          | `RainSensor`         | Rainfall intensity level             |
| `onAlarmDataChanged`           | `AlarmData`          | Vehicle alarm type + state           |
| `onVehicleInfoChanged`         | `VehicleInfoChanged` | Generic vehicle info events          |
| `onInstrumentClusterChanged`   | `InstrumentCluster`  | Brightness, theme, dimmer            |
| `onSecondaryOdometerChanged`   | `SecondaryOdometer`  | Trip A + trip B                      |
| `onRawEvent`                   | `int, String, byte[]`| Catch-all for all 58 callback codes  |

### 4.2 Synchronous Getters (Poll Immediately Without Waiting for Events)

```java
int speedKmh       = bus.getVehicleSpeed();
int rpm            = bus.getEngineSpeed();
Gear gear          = bus.getGearStatus();
AccState acc       = bus.getAccStatus();
int outsideTempC   = bus.getAmbientTemperature();
int coolantTempC   = bus.getEngineTemperature();
boolean handbrake  = bus.isHandBrakeEngaged();

AirCondition ac    = bus.getAirCondition();
float statusbarTmp = ac.getAirTempOutCar(); // Exact source used by SystemUI StatusBar
DoorStatus doors   = bus.getDoorStatus();
WindowStatus win   = bus.getWindowStatus();
LightStatus lights = bus.getLightStatus();
TpmsInfo tpms      = bus.getTpmsInfo();
Odometer odo       = bus.getOdometer();
FuelLevel fuel     = bus.getFuelLevel();
BatteryState bat   = bus.getBatteryState();
SteeringAngle swc  = bus.getSWCAngle();
RadarData frontRad = bus.getFrontRadarData();
RadarData rearRad  = bus.getRearRadarData();

// Force CAN service to re-broadcast all current states to listeners:
bus.queryVehicleState();
```

### 4.3 Push Turn-by-Turn Navigation, Media & Phone Info to Instrument Cluster (MID / Speedometer)

```java
// 1. Push Turn-by-Turn navigation directions to the speedometer screen
NaviInfo nav = new NaviInfo()
    .setGuideState(GuideState.GUIDING)
    .setDirectionId(2)                  // turn arrow icon ID
    .setCurrentRoadName("Jl. Sudirman")
    .setTurnRoadName("Jl. Thamrin")
    .setNextDistanceMeters(250)         // 250m to next turn
    .setDestDistanceMeters(4200)        // 4.2km to destination
    .setRemainTimeSeconds(480)          // 8 mins remaining
    .setDestName("Bundaran HI");
bus.updateNaviInfo(nav);

// 2. Push 3rd-party music player metadata (e.g. Spotify / YouTube Music) to MID
MediaClusterInfo media = new MediaClusterInfo("Bohemian Rhapsody", "Queen", "A Night at the Opera")
    .setSrcType(MediaClusterInfo.SrcType.NETWORK_MUSIC)
    .setMediaState(MediaClusterInfo.MediaState.PLAY);
bus.updateMediaSrcInfo(media);
bus.updateMediaPlayInfo(12, 3, 45);     // total tracks, current track, progress %

// 3. Push phone call status to MID
PhoneClusterInfo call = new PhoneClusterInfo("08123456789", "Budi", PhoneClusterInfo.CallState.CONNECTED)
    .setDurationSeconds(65);
bus.updatePhoneInfo(call);
```

### 4.4 HVAC, Window, Sunroof & Vehicle Control

```java
// Air Conditioning
bus.setAcPower(true);
bus.setLeftTemperature(24.0f);          // 17.0 - 33.0 °C
bus.setRightTemperature(22.5f);
bus.setWindSpeed(4);                    // 1 - 8
bus.adjustLeftTemperature(+0.5f);
bus.setAirConditionState(AirConditionState.AC_FRONT_DEFROST_SWITCH, 1);
bus.setAirConditionState(AirConditionState.AC_RECIRC_AIR, 2); // internal recirculation

// Windows & Sunroof
bus.setWindowOpen(0, true);             // 0=FL, 1=FR, 2=RL, 3=RR
bus.setSunroofFullOpen();               // SceneMode.SKY (mode 13)
bus.setSunroofClose();                  // SceneMode.BASE (mode 0)
bus.setSunroofTiltOpen(true);
bus.setSunroofShadeOpen(true);

// Scene Modes & 200+ VehicleState parameters
bus.setVehicleSceneMode(SceneMode.COOL);
bus.setVehicleState(VehicleState.TRUNK_RELEASE, 1);
int wiper = bus.getVehicleState(VehicleState.FRONT_WIPER_OUTPUT_STATUS);
```

---

## 5. MCU & Hardware Signals (`QGCarSignal`)

Accesses `com.qinggan.carsignal.service` (`qg.carsignal`) for head-unit thermal sensors, 12V voltage, MCU versions, antenna states, and hardware configuration:

```java
QGCarSignal sig = QGCarSignal.connect(this);

// Thermal sensors (used by SystemUI & Launcher for overheat warning icon)
int mbTemp       = sig.getCurrentMBHighTemp();
int mbTempLevel  = sig.getCurrentMBHighTempLevel(); // 4 = overheat warning
int lcdTemp      = sig.getLCDTemp();
boolean overHeat = sig.isInMainBoardHighTempMode();

// MCU & Vehicle Identity
String mcuHw     = sig.getMCUHardwareVersion();
String mcuSw     = sig.getMCUSoftwareVersion();
String mcuDate   = sig.getMCUSoftVersionDate();
String carType   = sig.getCarType();
int vehicleType  = sig.getVehicleType();
int[] vehConfig  = sig.getVehicleConfig();

// Power, Voltage & Antennas
int battLevel    = sig.getBatteryLevel();
int voltageState = sig.getVoltageState();
int wakeupSrc    = sig.getWakeupSrc();
int gpsAnt       = sig.getGPSAnt();
int tunerAnt     = sig.getTunerAnt();

// Listen for real-time MCU signal changes
sig.addListener(new CarSignalListener() {
    @Override public void onTemValueChanged(int tempLevel) {
        if (tempLevel == 4) Log.w("car", "Head-unit mainboard high temperature!");
    }
    @Override public void onLCDTempChanged(int lcdTemp) { /* ... */ }
    @Override public void onVoltageStateChanged(int state) { /* ... */ }
});

sig.disconnect();
```

---

## 6. Display, Brightness & Keypad Backlight (`QGDisplay`)

Accesses `com.qinggan.systempolicy` (`qg.system_policy`) for screen brightness, day/night dimmer, screen power, and physical button backlights:

```java
QGDisplay display = QGDisplay.connect(this);

int brightness = display.getBackLight();            // 0 - 100
display.setBackLight(80);                           // set brightness to 80%

int dimmer = display.getDimmer();                   // 0=night, 1=day, 2=auto
display.setDimmer(QGDisplay.BRIGHTNESS_MODE_NIGHT, QGDisplay.FLAG_DEFAULT);

// Turn head-unit screen off / on (like Control Center "Close Screen")
display.setScreenOff(true);
boolean isOff = display.isScreenOff();

// Physical keypad / defrost button backlights
display.setKeyPadBacklightLevel(5);
display.openKeypadBackLight();

display.addListener(new DisplayListener() {
    @Override public boolean onBackLightChanged(int brightness, int flag) {
        Log.d("car", "Brightness changed to " + brightness);
        return false;
    }
});

display.disconnect();
```

---

## 7. DSP, Equalizer & Audio Policy (`QGAudio`)

Accesses `com.qinggan.audiopolicy.service` (`qg.audio_policy`) to control the vehicle's hardware DSP, Equalizer, Balance/Fader, Subwoofer, and automotive audio policies:

```java
QGAudio audio = QGAudio.connect(this);

// Equalizer (EQ)
int eqPreset = audio.getEQModeIndex();
audio.setEQMode(1);
int band0 = audio.getBandValue(0);
audio.setBandValue(0, 3);                           // boost bass band

// Cabin Balance (Left/Right) & Fader (Front/Rear)
int[] balFader = audio.getBalanceFaderLevel();      // [balance, fader]
audio.setBalanceFaderLevel(0, -2);                  // shift soundstage slightly rear

// 3D Soundstage, Loudness & Subwoofer
audio.setArkarmysMode(QGAudio.ARKAMYS_DRIVER);
audio.setLoudness(1);
audio.setSubwooferMode(1);

// Speed-compensated volume & Reverse gear audio ducking
audio.setSpeedCoefMode(QGAudio.SPEED_VOL_MIDDLE);
audio.setReversePolicy(QGAudio.REVERSE_POLICY_VOLUME_DOWN);

// Baby-in-Car quiet mode
audio.setBabyInCar(true);

audio.disconnect();
```

---

## 8. Telematics Box — VIN, 4G Cellular & Hotspot (`QGTBox`)

Accesses `com.qinggan.tbox.service` (`dls_tbox_service`) to read vehicle/modem identity and control the built-in 4G T-Box & Wi-Fi hotspot:

```java
QGTBox tbox = QGTBox.connect(this);

// Vehicle & Modem Identity
String vin    = tbox.getVIN();                      // Vehicle Identification Number (Nomor Rangka)
String imei   = tbox.getIMEI();
String iccid  = tbox.getICCID();
String msisdn = tbox.getMSISDN();
String pdsn   = tbox.getPDSN();

// 4G Cellular Status
boolean connected = tbox.isTBoxConnected();
int signalBars    = tbox.getSignalStrength();       // 0 - 5 bars
int netType       = tbox.getNetworkType();          // QGTBox.NET_4G = 4
String operator   = tbox.getNetworkOperator();
String ipAddr     = tbox.getIPAddress();

// In-Car Wi-Fi Hotspot Control
boolean hotspotOn = tbox.getWlanSwitchStatus();
String ssid       = tbox.getWlanSsid();
String password   = tbox.getWlanPassword();
tbox.setWlanSsid("Wuling-Almaz-WiFi");
tbox.setWlanPassword("12345678");
tbox.switchWlan(true);

tbox.addListener(new TBoxListener() {
    @Override public void onNetworkSignalStrengthChanged(int bars) {
        Log.d("tbox", "4G signal bars: " + bars);
    }
});

tbox.disconnect();
```

---

## 9. Power Management (`QGPower`)

Accesses `com.qinggan.powermanager` (`qg.power_manager`) for AVN power state, clock screensaver, and standby display control:

```java
QGPower power = QGPower.connect(this);

int avnStatus = power.getAVNStatus();               // AVN_STATUS_RUN_ON = 0
power.enterScreenSaver();                           // show clock screensaver
power.exitScreenSaver();

// Keep custom app visible during standby mode
power.requestDisplayInStandby(QGPower.STANDBY_PRI_HIGH);

power.addListener(new PowerListener() {
    @Override public void onAVNStatusChanged(int status) {
        Log.d("power", "AVN status: " + status);
    }
});

power.disconnect();
```

---

## 10. Hardware FM/AM Radio Tuner (`QGTuner`)

Accesses `com.qinggan.tuner.service` (`qg.tuner_service`) to control the hardware radio chip directly:

```java
QGTuner tuner = QGTuner.connect(this);

tuner.setBand(QGTuner.BAND_FM);
tuner.play(9870);                                   // tune to 98.7 MHz FM
tuner.seekUp();                                     // seek next station

TunerStatus status = tuner.getTunerStatus();
if (status != null) {
    Log.d("radio", "Freq=" + status.frequency + " signal=" + status.level + " stereo=" + status.stereo);
}

tuner.disconnect();
```

---

## Building

```bash
./build.sh   # compiles all classes and produces build/qgcar.jar
```

Requires Android SDK with `platforms/android-29/android.jar` reachable via `$ANDROID_HOME` (default: `~/Library/Android/sdk`).

---

## License

This library is provided as-is for research and personal use on compatible head-units. The AIDL stubs and JNI bindings are derived from reverse-engineering vendor firmware and are not covered by any upstream license.
