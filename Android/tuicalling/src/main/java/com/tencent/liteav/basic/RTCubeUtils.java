package com.tencent.liteav.basic;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


public class RTCubeUtils {
    private static final String RTCUBE_PACKAGE_NAME = "com.tencent.trtc";

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return TextUtils.isEmpty(applicationName) ? "" : applicationName;
    }

    public static boolean isRTCubeApp(Context context) {
        return RTCUBE_PACKAGE_NAME.equals(context.getPackageName());
    }
}
