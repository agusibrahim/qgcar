package com.qgcar.bus;

import android.os.Parcel;

/**
 * Turn-by-Turn navigation information for the vehicle's Instrument Cluster (MID).
 * Serializes to the exact wire format expected by {@code com.qinggan.canbus.NaviSrcInfo}.
 */
public final class NaviInfo {
    private static final String VENDOR_GUIDE_STATE_CLASS = "com.qinggan.canbus.GuideState";

    public GuideState guideState = GuideState.GUIDING;
    public int tbtInfoId;
    public int directionId;
    public int heading;
    public int destDistanceMeters;
    public int nextDistanceMeters;
    public int remainTimeSeconds;
    public int param;
    public String currentRoadName = "";
    public String turnRoadName = "";
    public String roundAboutAngle = "";
    public int roundAboutNum;
    public int roundAboutOut;
    public String destName = "";
    public boolean straight;

    public NaviInfo() {}

    public NaviInfo setGuideState(GuideState state) { this.guideState = state; return this; }
    public NaviInfo setTbtInfoId(int id) { this.tbtInfoId = id; return this; }
    public NaviInfo setDirectionId(int id) { this.directionId = id; return this; }
    public NaviInfo setHeading(int heading) { this.heading = heading; return this; }
    public NaviInfo setDestDistanceMeters(int m) { this.destDistanceMeters = m; return this; }
    public NaviInfo setNextDistanceMeters(int m) { this.nextDistanceMeters = m; return this; }
    public NaviInfo setRemainTimeSeconds(int s) { this.remainTimeSeconds = s; return this; }
    public NaviInfo setParam(int param) { this.param = param; return this; }
    public NaviInfo setCurrentRoadName(String name) { this.currentRoadName = name; return this; }
    public NaviInfo setTurnRoadName(String name) { this.turnRoadName = name; return this; }
    public NaviInfo setRoundAboutAngle(String angle) { this.roundAboutAngle = angle; return this; }
    public NaviInfo setRoundAboutNum(int num) { this.roundAboutNum = num; return this; }
    public NaviInfo setRoundAboutOut(int out) { this.roundAboutOut = out; return this; }
    public NaviInfo setDestName(String name) { this.destName = name; return this; }
    public NaviInfo setStraight(boolean straight) { this.straight = straight; return this; }

    public void writeToVendorParcel(Parcel p) {
        if (guideState == null) {
            p.writeString(null);
        } else {
            p.writeString(VENDOR_GUIDE_STATE_CLASS);
            p.writeInt(guideState.ordinal());
        }
        p.writeInt(tbtInfoId);
        p.writeInt(directionId);
        p.writeInt(heading);
        p.writeInt(destDistanceMeters);
        p.writeInt(nextDistanceMeters);
        p.writeInt(remainTimeSeconds);
        p.writeInt(param);
        p.writeString(currentRoadName);
        p.writeString(turnRoadName);
        p.writeString(roundAboutAngle);
        p.writeInt(roundAboutNum);
        p.writeInt(roundAboutOut);
        p.writeString(destName);
        p.writeInt(straight ? 1 : 0);
    }
}
