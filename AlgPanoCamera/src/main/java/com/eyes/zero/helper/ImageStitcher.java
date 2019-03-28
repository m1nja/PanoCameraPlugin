package com.eyes.zero.helper;

import android.graphics.Bitmap;

public class ImageStitcher {
    public ImageStitcher() {
    }

    public static native boolean start();

    public static native boolean stop();

    public static native int track(byte[] var0, int var1, int var2, int var3, int var4, int var5, int var6, String var7, int var8, int var9);

    public static native float[] getoverlaprect();

    public static native void renderPanorama(Bitmap var0);

    public static native float[] getStitchingParameter();

    static {
        System.loadLibrary("imagestitcher");
    }
}
