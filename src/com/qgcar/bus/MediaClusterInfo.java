package com.qgcar.bus;

import android.os.Parcel;

/**
 * Media metadata sent to the vehicle's Instrument Cluster (MID / Speedometer).
 * Serializes to the exact wire format expected by {@code com.qinggan.canbus.MediaSrcInfo}.
 */
public final class MediaClusterInfo {

    public enum SrcType {
        OFF(0), FM(1), AM(2), DISC(3), TV(4), NAVI(5), PHONE(6), IPOD(7),
        AUX(8), USB(9), SD(10), DVBT(11), A2DP(12), OTHER(13), CDC(14),
        NETWORK(36), QQ(37), IPDA_LINK(38), NETWORK_MUSIC(39), NETWORK_RADIO(40);

        public final int value;
        SrcType(int value) { this.value = value; }
    }

    public enum MediaType {
        TUNER(1), SIMPLE_AUDIO(16), ENHANCED_AUDIO(17), IPOD(18),
        LOCAL_VIDEO(32), DVD(33), OTHER_VIDEO(34), AUX(48), PHONE(64);

        public final int value;
        MediaType(int value) { this.value = value; }
    }

    public enum MediaState {
        CLEAN(0), PLAY(1), PAUSE(2), STOP(3), SCANNING(4), BUFFERING(5);

        public final int value;
        MediaState(int value) { this.value = value; }
    }

    public enum PlayMode {
        SEQUENCE(0), PREVIEW(1), RANDOM(2), SINGLE(3);

        public final int value;
        PlayMode(int value) { this.value = value; }
    }

    public SrcType srcType = SrcType.NETWORK_MUSIC;
    public MediaType mediaType = MediaType.ENHANCED_AUDIO;
    public float frequency;
    public MediaState mediaState = MediaState.PLAY;
    public PlayMode playMode = PlayMode.SEQUENCE;
    public String songName = "";
    public String artist = "";
    public String albumName = "";
    public String clearMedia = "";
    public boolean favorite;

    public MediaClusterInfo() {}

    public MediaClusterInfo(String songName, String artist, String albumName) {
        this.songName = songName;
        this.artist = artist;
        this.albumName = albumName;
    }

    public MediaClusterInfo setSrcType(SrcType t) { this.srcType = t; return this; }
    public MediaClusterInfo setMediaType(MediaType t) { this.mediaType = t; return this; }
    public MediaClusterInfo setFrequency(float f) { this.frequency = f; return this; }
    public MediaClusterInfo setMediaState(MediaState s) { this.mediaState = s; return this; }
    public MediaClusterInfo setPlayMode(PlayMode m) { this.playMode = m; return this; }
    public MediaClusterInfo setSongName(String s) { this.songName = s; return this; }
    public MediaClusterInfo setArtist(String s) { this.artist = s; return this; }
    public MediaClusterInfo setAlbumName(String s) { this.albumName = s; return this; }
    public MediaClusterInfo setClearMedia(String s) { this.clearMedia = s; return this; }
    public MediaClusterInfo setFavorite(boolean f) { this.favorite = f; return this; }

    public void writeToVendorParcel(Parcel p) {
        if (srcType == null) {
            p.writeString(null);
        } else {
            p.writeString("com.qinggan.canbus.MediaSrcType");
            p.writeInt(srcType.ordinal());
            p.writeInt(srcType.value);
        }
        if (mediaType == null) {
            p.writeString(null);
        } else {
            p.writeString("com.qinggan.canbus.MediaType");
            p.writeInt(mediaType.ordinal());
            p.writeInt(mediaType.value);
        }
        p.writeFloat(frequency);
        if (mediaState == null) {
            p.writeString(null);
        } else {
            p.writeString("com.qinggan.canbus.MediaState");
            p.writeInt(mediaState.ordinal());
            p.writeInt(mediaState.value);
        }
        if (playMode == null) {
            p.writeString(null);
        } else {
            p.writeString("com.qinggan.canbus.MediaPlayMode");
            p.writeInt(playMode.ordinal());
            p.writeInt(playMode.value);
        }
        p.writeString(songName);
        p.writeString(artist);
        p.writeString(albumName);
        p.writeString(clearMedia);
        p.writeInt(favorite ? 1 : 0);
    }
}
