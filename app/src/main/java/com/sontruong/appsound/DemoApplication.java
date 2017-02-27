package com.sontruong.appsound;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 26/02/2017.
 */

public class DemoApplication extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        applicationHandler = new Handler(applicationContext.getMainLooper());
    }
}
