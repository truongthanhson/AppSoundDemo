package com.poptech.popap.utils;

import com.poptech.popap.PopapApplication;

/**
 * Created by sontt on 26/02/2017.
 */

public class AndroidUtilities {
    public static float density = 1;
    static {
        density = PopapApplication.applicationContext.getResources().getDisplayMetrics().density;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            PopapApplication.applicationHandler.post(runnable);
        } else {
            PopapApplication.applicationHandler.postDelayed(runnable, delay);
        }
    }
}
