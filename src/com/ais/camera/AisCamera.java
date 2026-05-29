package com.ais.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reconstructed from /system/priv-app/Launcher-release-signed/Launcher-release-signed.apk
 * Class name + package MUST match exactly because libaiscamera_jni.so registers
 * its JNI methods against this fully qualified name in JNI_OnLoad.
 */
public class AisCamera {
    private static final String TAG = "AisCamera";
    private static AisCamera sInstance;
    private static boolean sHaveRegist = false;

    private final Map<Integer, Map<CHANNEL_ID, List<IStreamCallBack>>> mStreamListenerMap = new HashMap<>();

    public enum CHANNEL_ID {
        CHANNEL_ID_0,
        CHANNEL_ID_1,
        CHANNEL_ID_2,
        CHANNEL_ID_3,
        CHANNEL_ID_INVALID
    }

    public interface IStreamCallBack {
        void onStream(byte[] data, int len, int width, int height);
    }

    public interface IReInitCallBack {
        void onDataCallbackReinit();
        void onDataCallbackDeinit();
    }

    private IReInitCallBack mIReInitCallBack;

    private native boolean aiscamera_open(int csi, int maxChannel);
    private native boolean aiscamera_close(int csi);
    private native void aiscamera_register_stream_callback();
    private native void set_camera_black_white_mode(int csi, int channel, boolean enable);
    private native boolean get_camera_black_white_mode(int csi, int channel);
    private native void set_camera_infrared_mode(int csi, int channel, boolean enable);
    private native boolean get_camera_infrared_mode(int csi, int channel);

    static {
        try {
            Log.i(TAG, "loading libaiscamera_jni");
            System.loadLibrary("aiscamera_jni");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "could not load jni: " + e.getMessage());
        }
    }

    public static synchronized AisCamera getInstance() {
        if (sInstance == null) sInstance = new AisCamera();
        return sInstance;
    }

    public boolean openCamera() { return openCamera(1, 4); }
    public boolean closeCamera() { return closeCamera(1); }

    public boolean openCamera(int csi, int maxChannel) {
        boolean ok = aiscamera_open(csi, maxChannel);
        if (ok && !sHaveRegist) {
            aiscamera_register_stream_callback();
            sHaveRegist = true;
        }
        return ok;
    }

    public boolean closeCamera(int csi) { return aiscamera_close(csi); }

    public boolean registerStreamCallBack(CHANNEL_ID ch, IStreamCallBack cb) {
        return registerStreamCallBack(1, ch, cb);
    }

    public boolean registerStreamCallBack(int csi, CHANNEL_ID ch, IStreamCallBack cb) {
        synchronized (mStreamListenerMap) {
            Map<CHANNEL_ID, List<IStreamCallBack>> chMap = mStreamListenerMap.get(csi);
            if (chMap == null) {
                chMap = new HashMap<>();
                mStreamListenerMap.put(csi, chMap);
            }
            List<IStreamCallBack> list = chMap.get(ch);
            if (list == null) {
                list = new ArrayList<>();
                chMap.put(ch, list);
            }
            list.add(cb);
        }
        return true;
    }

    public boolean unRegisterStreamCallBack(CHANNEL_ID ch, IStreamCallBack cb) {
        return unRegisterStreamCallBack(1, ch, cb);
    }

    public boolean unRegisterStreamCallBack(int csi, CHANNEL_ID ch, IStreamCallBack cb) {
        synchronized (mStreamListenerMap) {
            Map<CHANNEL_ID, List<IStreamCallBack>> chMap = mStreamListenerMap.get(csi);
            if (chMap == null) return false;
            List<IStreamCallBack> list = chMap.get(ch);
            if (list == null) return false;
            return list.remove(cb);
        }
    }

    public void registCameraStatusCallback(IReInitCallBack cb) { mIReInitCallBack = cb; }

    /**
     * Called from JNI for every frame. Signature must match exactly
     * (jbyte[], jint, jint, jint, jint, jint).
     */
    protected void mStreamArrived(byte[] data, int len, int csiNum, int channelIdx,
                                  int width, int height) {
        CHANNEL_ID ch = idxToChannel(channelIdx);
        synchronized (mStreamListenerMap) {
            Map<CHANNEL_ID, List<IStreamCallBack>> chMap = mStreamListenerMap.get(csiNum);
            if (chMap == null) return;
            List<IStreamCallBack> list = chMap.get(ch);
            if (list == null) return;
            for (IStreamCallBack cb : list) cb.onStream(data, len, width, height);
        }
    }

    protected void mReinitCallback() {
        if (mIReInitCallBack != null) mIReInitCallBack.onDataCallbackReinit();
    }

    protected void mDeinitCallback() {
        if (mIReInitCallBack != null) mIReInitCallBack.onDataCallbackDeinit();
    }

    private static CHANNEL_ID idxToChannel(int i) {
        switch (i) {
            case 0: return CHANNEL_ID.CHANNEL_ID_0;
            case 1: return CHANNEL_ID.CHANNEL_ID_1;
            case 2: return CHANNEL_ID.CHANNEL_ID_2;
            case 3: return CHANNEL_ID.CHANNEL_ID_3;
            default: return CHANNEL_ID.CHANNEL_ID_INVALID;
        }
    }

    public static byte[] subBytes(byte[] src, int from, int len) {
        byte[] out = new byte[len];
        for (int i = from; i < (from + len) - 3; i++) out[i - from] = src[i];
        return out;
    }

    public Bitmap yuvConvertToBitmap(byte[] data, int len, int w, int h) {
        byte[] payload = subBytes(data, 3, len);
        YuvImage yuv = new YuvImage(payload, 20 /* YUY2 */, w, h, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, w, h), 100, bos);
        byte[] jpeg = bos.toByteArray();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, opts);
    }
}
