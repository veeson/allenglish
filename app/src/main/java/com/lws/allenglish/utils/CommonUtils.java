package com.lws.allenglish.utils;

/**
 * Created by Wilson on 2016/12/15.
 */

public class CommonUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 1000;

    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(SPACE_TIME);
    }

    public static boolean isFastDoubleClick(long spaceTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < spaceTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
