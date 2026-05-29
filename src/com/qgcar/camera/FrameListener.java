package com.qgcar.camera;

import android.graphics.Bitmap;

/**
 * Receives raw frames coming from one AVM channel. Frames are NV21/YUY2 bytes
 * straight from the vendor JNI stream callback.
 *
 * Use {@link QGCamera#yuvToBitmap(byte[], int, int, int)} to decode if you
 * just want pixels, or {@link #onBitmap(CameraChannel, Bitmap)} for the
 * convenience overload.
 */
public interface FrameListener {
    /** Raw YUV frame. Length is the actual bytes; data has a 3-byte vendor
     *  header that the converter helpers already strip. */
    default void onYuvFrame(CameraChannel channel, byte[] data, int len, int width, int height) {}

    /** Decoded bitmap. Default impl no-op; override one of the two methods. */
    default void onBitmap(CameraChannel channel, Bitmap bitmap) {}
}
