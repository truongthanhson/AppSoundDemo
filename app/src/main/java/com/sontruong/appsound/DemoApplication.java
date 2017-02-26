package com.sontruong.appsound;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 26/02/2017.
 */

public class DemoApplication extends Application {
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }
}
