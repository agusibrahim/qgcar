package com.qgcar.bus;

import android.os.Parcel;
import android.os.Parcelable;

public final class LightStatus implements Parcelable {
    public static final Parcelable.Creator<LightStatus> CREATOR = new Parcelable.Creator<LightStatus>() {
        @Override
        public LightStatus[] newArray(int i) {
            return new LightStatus[i];
        }

        @Override
        public LightStatus createFromParcel(Parcel parcel) {
            LightStatus lightStatus = new LightStatus();
            lightStatus.directionL = parcel.readInt();
            lightStatus.directionR = parcel.readInt();
            lightStatus.fogFront = parcel.readInt();
            lightStatus.fogRear = parcel.readInt();
            lightStatus.mainBeam = parcel.readInt();
            lightStatus.sideLight = parcel.readInt();
            lightStatus.ebdLamp = parcel.readInt();
            lightStatus.dippedBeam = parcel.readInt();
            lightStatus.positionLamp = parcel.readInt();
            lightStatus.dayLight = parcel.readInt();
            lightStatus.stopLight = parcel.readInt();
            lightStatus.reversLight = parcel.readInt();
            lightStatus.cautionLight = parcel.readInt();
            lightStatus.headLight = parcel.readInt();
            lightStatus.headLightSwicthStatus = parcel.readInt();
            lightStatus.directionLightSwitchStatus = parcel.readInt();
            lightStatus.cautionLightSwitchStatus = parcel.readInt();
            lightStatus.lowOrHighSwitchStatus = parcel.readInt();
            return lightStatus;
        }
    };

    public int directionL;
    public int directionR;
    public int fogFront;
    public int fogRear;
    public int mainBeam;
    public int sideLight;
    public int ebdLamp;
    public int dippedBeam;
    public int positionLamp;
    public int dayLight;
    public int stopLight;
    public int reversLight;
    public int cautionLight;
    public int headLight;
    public int headLightSwicthStatus;
    public int directionLightSwitchStatus;
    public int cautionLightSwitchStatus;
    public int lowOrHighSwitchStatus;

    public LightStatus() {}

    public LightStatus(int directionL, int directionR, int fogFront, int fogRear, int mainBeam,
                       int sideLight, int ebdLamp, int dippedBeam, int positionLamp, int dayLight,
                       int stopLight, int reversLight, int cautionLight, int headLight,
                       int headLightSwicthStatus, int directionLightSwitchStatus,
                       int cautionLightSwitchStatus, int lowOrHighSwitchStatus) {
        this.directionL = directionL;
        this.directionR = directionR;
        this.fogFront = fogFront;
        this.fogRear = fogRear;
        this.mainBeam = mainBeam;
        this.sideLight = sideLight;
        this.ebdLamp = ebdLamp;
        this.dippedBeam = dippedBeam;
        this.positionLamp = positionLamp;
        this.dayLight = dayLight;
        this.stopLight = stopLight;
        this.reversLight = reversLight;
        this.cautionLight = cautionLight;
        this.headLight = headLight;
        this.headLightSwicthStatus = headLightSwicthStatus;
        this.directionLightSwitchStatus = directionLightSwitchStatus;
        this.cautionLightSwitchStatus = cautionLightSwitchStatus;
        this.lowOrHighSwitchStatus = lowOrHighSwitchStatus;
    }

    public enum TurnSignalState {
        OFF,
        LEFT,
        RIGHT,
        HAZARD,
        UNKNOWN
    }

    public TurnSignalState getTurnSignalState() {
        boolean isLeftOn = (directionL == 1 && directionR == 0);
        boolean isRightOn = (directionL == 0 && directionR == 1);
        boolean isHazardOn = (directionL == 1 && directionR == 1);

        if (isHazardOn) return TurnSignalState.HAZARD;
        if (isLeftOn) return TurnSignalState.LEFT;
        if (isRightOn) return TurnSignalState.RIGHT;
        if (directionL == 0 && directionR == 0) return TurnSignalState.OFF;
        return TurnSignalState.UNKNOWN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.directionL);
        parcel.writeInt(this.directionR);
        parcel.writeInt(this.fogFront);
        parcel.writeInt(this.fogRear);
        parcel.writeInt(this.mainBeam);
        parcel.writeInt(this.sideLight);
        parcel.writeInt(this.ebdLamp);
        parcel.writeInt(this.dippedBeam);
        parcel.writeInt(this.positionLamp);
        parcel.writeInt(this.dayLight);
        parcel.writeInt(this.stopLight);
        parcel.writeInt(this.reversLight);
        parcel.writeInt(this.cautionLight);
        parcel.writeInt(this.headLight);
        parcel.writeInt(this.headLightSwicthStatus);
        parcel.writeInt(this.directionLightSwitchStatus);
        parcel.writeInt(this.cautionLightSwitchStatus);
        parcel.writeInt(this.lowOrHighSwitchStatus);
    }

    @Override
    public String toString() {
        return String.format("LightStatus{dirL=%d dirR=%d turnState=%s hazard=%d headLight=%d}",
            directionL, directionR, getTurnSignalState(), cautionLight, headLight);
    }
}
