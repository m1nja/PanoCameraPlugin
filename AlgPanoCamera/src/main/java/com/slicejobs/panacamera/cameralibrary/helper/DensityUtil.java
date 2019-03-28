package com.slicejobs.panacamera.cameralibrary.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DensityUtil {
    private static int[] deviceWidthHeight = new int[2];

    public DensityUtil() {
    }

    public static int[] getDeviceInfo(Context context) {
        if (deviceWidthHeight[0] == 0 && deviceWidthHeight[1] == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            deviceWidthHeight[0] = metrics.widthPixels;
            deviceWidthHeight[1] = metrics.heightPixels;
        }

        return deviceWidthHeight;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    @TargetApi(13)
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService("window");
        Display display = windowManager.getDefaultDisplay();
        if (VERSION.SDK_INT < 13) {
            return new Point(display.getWidth(), display.getHeight());
        } else {
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }
}
