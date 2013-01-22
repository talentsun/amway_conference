package com.thebridgestudio.amwayconference.utils;

import android.os.Build;

public class SystemUtil {

    public static long USED_STORAGE;
    public static long LEFT_STORAGE;

    private static String WANDOU_VERSION_NAME = null;

    public static boolean aboveApi5() {
        return getApiLevel() >= 5;
    }

    public static boolean aboveApi8() {
        return getApiLevel() >= 8;
    }

    public static boolean aboveApi9() {
        return getApiLevel() >= 9;
    }

    // 4.0
    public static boolean aboveApi14() {
        return getApiLevel() >= 14;
    }

    public static boolean aboveApi13() {
        return getApiLevel() >= 13;
    }

    // 3.0+
    public static boolean aboveApi11() {
        return getApiLevel() >= 11;
    }

    private static int apiLevel;
    static {
        apiLevel = Integer.parseInt(Build.VERSION.SDK);
    }

    public static int getApiLevel() {
        return apiLevel;
    }

}
