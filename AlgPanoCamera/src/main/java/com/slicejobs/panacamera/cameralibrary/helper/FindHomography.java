package com.slicejobs.panacamera.cameralibrary.helper;

public class FindHomography {
    public FindHomography() {
    }

    public static native boolean start(int var0, int var1);

    public static native boolean stop();

    public static native boolean gethomography(byte[] var0, int var1, int var2, int var3);

    public static native float[] getoverlaprect();

    public static native boolean setreference(byte[] var0, int var1, int var2, int var3, int var4);

    static {
        System.loadLibrary("find-homography");
    }
}
