package com.slicejobs.panacamera.cameralibrary.helper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import java.util.ArrayList;

public class ServiceUtils {
    public ServiceUtils() {
    }

    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (!"".equals(ServiceName) && ServiceName != null) {
            ActivityManager myManager = (ActivityManager)context.getSystemService("activity");
            ArrayList<RunningServiceInfo> runningService = (ArrayList)myManager.getRunningServices(30);

            for(int i = 0; i < runningService.size(); ++i) {
                if (((RunningServiceInfo)runningService.get(i)).service.getClassName().toString().equals(ServiceName)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
