package com.slicejobs.panacamera.cameralibrary.helper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.slicejobs.panacamera.cameralibrary.CameraSnap;
import com.socks.library.KLog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SystemUtil {
    private static final String TAG = "SystemUtil";

    public SystemUtil() {
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CameraSnap.mApp.getSystemService("connectivity");
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static String getAppVersionName() {
        String versionName = "";

        try {
            PackageManager pm = CameraSnap.mApp.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(CameraSnap.mApp.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "1.0.0";
            }
        } catch (Exception var3) {
            Log.e("VersionInfo", "Exception", var3);
        }

        return versionName;
    }

    public static int getAppVersionCode() {
        int versionCode = 0;

        try {
            PackageManager pm = CameraSnap.mApp.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(CameraSnap.mApp.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception var3) {
            Log.e("VersionInfo", "Exception", var3);
        }

        return versionCode;
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }

            String var3 = processName;
            return var3;
        } catch (Throwable var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

        return null;
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();

        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException var3) {
            KLog.e("SystemUtil", new Object[]{var3.getLocalizedMessage()});
            return new PackageInfo();
        }
    }

    public static DisplayMetrics getScreenWH(Context context) {
        new DisplayMetrics();
        DisplayMetrics dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)mContext.getSystemService("activity");
        List<RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (serviceList.size() <= 0) {
            return false;
        } else {
            for(int i = 0; i < serviceList.size(); ++i) {
                if (((RunningServiceInfo)serviceList.get(i)).service.getClassName().equals(className)) {
                    isRunning = true;
                    break;
                }
            }

            return isRunning;
        }
    }

    public static Point getDisplayMetrics(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        int mScreenWidth = mDisplayMetrics.widthPixels;
        int mScreenHeight = mDisplayMetrics.heightPixels;
        return new Point(mScreenWidth, mScreenHeight);
    }
}
