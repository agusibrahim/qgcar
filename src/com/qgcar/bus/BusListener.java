package com.qgcar.bus;

/**
 * Listener for CAN bus events.
 *
 * High-level methods deliver decoded data for events we know how to parse.
 * {@link #onRawEvent} is a catch-all that fires for every transaction
 * (including unparsed ones) so you can dump bytes during exploration.
 *
 * All methods have a default no-op so override only what you need.
 */
public interface BusListener {
    default void onDoorStatusChanged(DoorStatus status) {}
    default void onLightStatusChanged(LightStatus status) {}
    default void onVehicleSpeedChanged(int kmh) {}
    default void onEngineSpeedChanged(int rpm) {}
    default void onGearStatusChanged(Gear gear) {}
    default void onHandBrakeStatusChanged(boolean engaged) {}
    default void onAmbientTemperatureChanged(int celsius) {}
    default void onSeatBeltChanged(SeatBelts belts) {}
    default void onWindowStatusChanged(WindowStatus windows) {}
    default void onFuelLevelChanged(FuelLevel fuel) {}
    default void onOdometerChanged(Odometer odo) {}
    default void onSWCAngleChanged(SteeringAngle angle) {}
    default void onIlluminationChanged(int level) {}
    default void onEngineTemperatureChanged(int celsius) {}
    default void onAccStateChanged(AccState state) {}
    default void onEngineStatusChanged(EngineState state) {}
    default void onAirConditionChanged(AirCondition ac) {}

    /** Fires for every callback transaction the service makes. {@code payload}
     *  is the parcel body after the AIDL interface token. */
    default void onRawEvent(int code, String name, byte[] payload) {}

    // ---- Extended callbacks (additive, backward compatible) ----

    default void onTpmsInfoChanged(TpmsInfo tpms) {}
    default void onRadarDataChanged(RadarData radar) {}
    default void onBatteryStateChanged(BatteryState battery) {}
    default void onWheelSpeedChanged(WheelSpeed speed) {}
    default void onAdasWarningChanged(AdasWarning adas) {}
    default void onHevStatusChanged(HevStatus hev) {}
    default void onParkStateChanged(ParkState park) {}
    default void onDvrStateChanged(DvrState dvr) {}
    default void onTravellingInfoChanged(TravellingInfo info) {}
    default void onFuelConsumptionChanged(FuelConsumption fc) {}
    default void onRainSensorChanged(RainSensor rain) {}
    default void onAlarmDataChanged(AlarmData alarm) {}
    default void onVehicleInfoChanged(VehicleInfoChanged info) {}
    default void onInstrumentClusterChanged(InstrumentCluster cluster) {}
    default void onSecondaryOdometerChanged(SecondaryOdometer odo) {}
    default void onEngineFluidStatusChanged(EngineFluidStatus fluid) {}
}
