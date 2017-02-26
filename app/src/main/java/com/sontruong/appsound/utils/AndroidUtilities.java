package com.sontruong.appsound.utils;

import com.sontruong.appsound.DemoApplication;

/**
 * Created by sontt on 26/02/2017.
 */

public class AndroidUtilities {
    public static float density = 1;
    static {
        density = DemoApplication.applicationContext.getResources().getDisplayMetrics().density;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }
}
