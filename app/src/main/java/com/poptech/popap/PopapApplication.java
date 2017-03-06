package com.poptech.popap;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.di.AppComponent;
import com.poptech.popap.di.AppModule;
import com.poptech.popap.di.DaggerAppComponent;

/**
 * Created by Administrator on 26/02/2017.
 */

public class PopapApplication extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        applicationHandler = new Handler(applicationContext.getMainLooper());
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(applicationContext)).build();
        PopapDatabase.getInstance(applicationContext).checkAndCreateDatabase();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
