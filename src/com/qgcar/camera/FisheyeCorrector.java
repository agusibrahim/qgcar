package com.qgcar.camera;

import android.graphics.Bitmap;

/**
 * Brown-Conrady inverse undistortion with diagonal normalization, anisotropic Y
 * scaling, and bilinear interpolation. Cheap enough to run per-frame on AVM streams.
 *
 * Tuned defaults: k1=-0.30, k2=0.0, fScale=1.0, asymY=1.0  (the "R-MILD" preset
 * that lined up best on the head-unit fisheye stream we tested against).
 */
public final class FisheyeCorrector {
    public static final FisheyeCorrector OFF      = new FisheyeCorrector( 0.00, 0.00, 1.0, 1.0);
    public static final FisheyeCorrector R_MILD   = new FisheyeCorrector(-0.30, 0.00, 1.0, 1.0);
    public static final FisheyeCorrector R_MED    = new FisheyeCorrector(-0.55, 0.05, 1.0, 1.0);
    public static final FisheyeCorrector R_STRONG = new FisheyeCorrector(-0.80, 0.15, 1.0, 1.0);

    private int srcW = -1, srcH = -1;
    private int outW = -1, outH = -1;
    private float[] mapX, mapY;
    private int[] srcBuf, dstBuf;
    private final Bitmap[] pingpong = new Bitmap[2];
    private int pingIdx = 0;

    private final double k1, k2, fScale, asymY;

    public FisheyeCorrector(double k1, double k2, double fScale, double asymY) {
        this.k1 = k1;
        this.k2 = k2;
        this.fScale = fScale;
        this.asymY = asymY;
    }

    public boolean isIdentity() { return k1 == 0.0 && k2 == 0.0; }

    private void rebuild(int w, int h) {
        srcW = w; srcH = h;
        outW = w; outH = h;
        int n = outW * outH;
        mapX = new float[n];
        mapY = new float[n];
        double cx = outW * 0.5, cy = outH * 0.5;
        double f = Math.sqrt(cx * cx + cy * cy) * fScale;
        for (int yo = 0; yo < outH; yo++) {
            for (int xo = 0; xo < outW; xo++) {
                double u = (xo - cx) / f;
                double v = ((yo - cy) / f) * asymY;
                double r2 = u * u + v * v;
                double d = 1.0 + k1 * r2 + k2 * r2 * r2;
                double xs = cx + u * d * f;
                double ys = cy + (v * d * f) / asymY;
                int idx = yo * outW + xo;
                if (xs >= 0 && xs <= srcW - 1 && ys >= 0 && ys <= srcH - 1) {
                    mapX[idx] = (float) xs;
                    mapY[idx] = (float) ys;
                } else {
                    mapX[idx] = -1f;
                    mapY[idx] = -1f;
                }
            }
        }
        srcBuf = new int[srcW * srcH];
        dstBuf = new int[outW * outH];
        pingpong[0] = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        pingpong[1] = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
    }

    public Bitmap apply(Bitmap src) {
        if (src == null) return null;
        if (isIdentity()) return src;
        int w = src.getWidth(), h = src.getHeight();
        if (mapX == null || w != srcW || h != srcH) rebuild(w, h);

        src.getPixels(srcBuf, 0, srcW, 0, 0, srcW, srcH);
        final int[] s = srcBuf, d = dstBuf;
        final int sW = srcW, sH = srcH, total = d.length;
        final float[] mX = mapX, mY = mapY;

        for (int i = 0; i < total; i++) {
            float xs = mX[i];
            if (xs < 0f) { d[i] = 0xFF000000; continue; }
            float ys = mY[i];
            int x0 = (int) xs;
            int y0 = (int) ys;
            int x1 = x0 + 1; if (x1 >= sW) x1 = x0;
            int y1 = y0 + 1; if (y1 >= sH) y1 = y0;
            float fx = xs - x0;
            float fy = ys - y0;
            int p00 = s[y0 * sW + x0];
            int p10 = s[y0 * sW + x1];
            int p01 = s[y1 * sW + x0];
            int p11 = s[y1 * sW + x1];

            float w00 = (1f - fx) * (1f - fy);
            float w10 = fx * (1f - fy);
            float w01 = (1f - fx) * fy;
            float w11 = fx * fy;

            int r = (int) (((p00 >> 16) & 0xff) * w00
                          + ((p10 >> 16) & 0xff) * w10
                          + ((p01 >> 16) & 0xff) * w01
                          + ((p11 >> 16) & 0xff) * w11);
            int g = (int) (((p00 >> 8) & 0xff) * w00
                          + ((p10 >> 8) & 0xff) * w10
                          + ((p01 >> 8) & 0xff) * w01
                          + ((p11 >> 8) & 0xff) * w11);
            int b = (int) ((p00 & 0xff) * w00
                          + (p10 & 0xff) * w10
                          + (p01 & 0xff) * w01
                          + (p11 & 0xff) * w11);
            d[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
        }

        pingIdx = (pingIdx + 1) & 1;
        Bitmap out = pingpong[pingIdx];
        out.setPixels(d, 0, outW, 0, 0, outW, outH);
        return out;
    }
}
