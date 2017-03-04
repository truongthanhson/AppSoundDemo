package com.sontruong.appsound;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.sontruong.appsound.di.AppComponent;
import com.sontruong.appsound.di.AppModule;
import com.sontruong.appsound.di.DaggerAppComponent;

/**
 * Created by Administrator on 26/02/2017.
 */

public class DemoApplication extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        applicationHandler = new Handler(applicationContext.getMainLooper());
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(applicationContext)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
