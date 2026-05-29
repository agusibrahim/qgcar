package com.qgcar.camera;

import android.graphics.Bitmap;
import com.ais.camera.AisCamera;
import com.ais.camera.AisCamera.CHANNEL_ID;
import com.ais.camera.AisCamera.IStreamCallBack;

import java.util.EnumMap;

/**
 * High-level wrapper around the vendor {@link AisCamera} JNI bridge.
 *
 * The vendor library lives at /vendor/lib/libaiscamera_jni.so on supported
 * head-units. The class {@code com.ais.camera.AisCamera} exists in our project
 * with the exact same package + name because JNI_OnLoad inside the .so binds
 * native methods against that fully-qualified name; renaming would break it.
 *
 * Typical usage:
 * <pre>{@code
 *   QGCamera cam = QGCamera.open();              // CSI 0, 4 channels
 *   cam.setFisheyeCorrector(FisheyeCorrector.R_MILD);
 *   cam.subscribe(CameraChannel.CH0, listener);  // raw YUV + bitmap
 *   ...
 *   cam.close();
 * }</pre>
 */
public final class QGCamera {

    private final AisCamera mDelegate;
    private final int mCsi;
    private final EnumMap<CameraChannel, IStreamCallBack> mActive =
        new EnumMap<>(CameraChannel.class);
    private volatile FisheyeCorrector mCorrector = FisheyeCorrector.OFF;

    private QGCamera(AisCamera delegate, int csi) {
        mDelegate = delegate;
        mCsi = csi;
    }

    /** Try CSI 0 first (the one tvdecoderserver streams to), fall back to 1. */
    public static QGCamera open() throws CameraOpenException {
        return open(4);
    }

    public static QGCamera open(int maxChannels) throws CameraOpenException {
        AisCamera ais = AisCamera.getInstance();
        for (int csi : new int[]{0, 1}) {
            try {
                if (ais.openCamera(csi, maxChannels)) return new QGCamera(ais, csi);
            } catch (Throwable ignored) {}
        }
        throw new CameraOpenException("AisCamera.openCamera returned false on csi 0 and 1");
    }

    public int csi() { return mCsi; }

    public void setFisheyeCorrector(FisheyeCorrector c) {
        mCorrector = (c != null) ? c : FisheyeCorrector.OFF;
    }

    public FisheyeCorrector getFisheyeCorrector() { return mCorrector; }

    public void subscribe(final CameraChannel ch, final FrameListener listener) {
        if (listener == null) return;
        unsubscribe(ch);
        IStreamCallBack cb = new IStreamCallBack() {
            @Override
            public void onStream(byte[] data, int len, int width, int height) {
                try {
                    listener.onYuvFrame(ch, data, len, width, height);
                    Bitmap raw = mDelegate.yuvConvertToBitmap(data, len, width, height);
                    Bitmap out = mCorrector.apply(raw);
                    if (out != null) listener.onBitmap(ch, out);
                } catch (Throwable t) {
                    // listener bugs shouldn't kill the stream
                }
            }
        };
        mDelegate.registerStreamCallBack(mCsi, toAis(ch), cb);
        mActive.put(ch, cb);
    }

    public void unsubscribe(CameraChannel ch) {
        IStreamCallBack cb = mActive.remove(ch);
        if (cb != null) {
            try { mDelegate.unRegisterStreamCallBack(mCsi, toAis(ch), cb); }
            catch (Throwable ignored) {}
        }
    }

    /** Soft reset: drop all subscriptions, close, reopen, and re-attach.
     *  Use when frames go green / sliced / corrupted — equivalent to
     *  force-stop+relaunch on the user side. */
    public synchronized void resetStream() {
        EnumMap<CameraChannel, IStreamCallBack> snapshot = new EnumMap<>(mActive);
        for (CameraChannel ch : snapshot.keySet()) unsubscribe(ch);
        try { mDelegate.closeCamera(mCsi); } catch (Throwable ignored) {}
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        mDelegate.openCamera(mCsi, 4);
        for (java.util.Map.Entry<CameraChannel, IStreamCallBack> e : snapshot.entrySet()) {
            mDelegate.registerStreamCallBack(mCsi, toAis(e.getKey()), e.getValue());
            mActive.put(e.getKey(), e.getValue());
        }
    }

    public Bitmap yuvToBitmap(byte[] data, int len, int width, int height) {
        return mDelegate.yuvConvertToBitmap(data, len, width, height);
    }

    public synchronized void close() {
        for (CameraChannel ch : new java.util.ArrayList<>(mActive.keySet())) unsubscribe(ch);
        try { mDelegate.closeCamera(mCsi); } catch (Throwable ignored) {}
    }

    private static CHANNEL_ID toAis(CameraChannel c) {
        switch (c) {
            case CH0: return CHANNEL_ID.CHANNEL_ID_0;
            case CH1: return CHANNEL_ID.CHANNEL_ID_1;
            case CH2: return CHANNEL_ID.CHANNEL_ID_2;
            case CH3: return CHANNEL_ID.CHANNEL_ID_3;
            default:  return CHANNEL_ID.CHANNEL_ID_INVALID;
        }
    }

    public static class CameraOpenException extends RuntimeException {
        CameraOpenException(String m) { super(m); }
    }
}
