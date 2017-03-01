package com.sontruong.appsound.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Truong Thanh Son on 11/6/2015.
 */
public class SaveData {
    /**
     * The save data.
     */
    private static SaveData saveData = null;

    /**
     * The share preference.
     */
    private SharedPreferences sharePreference;

    /**
     * Instantiates a new save data.
     *
     * @param context the context
     */
    public SaveData(Context context) {
        sharePreference = context.getSharedPreferences("PhotoSound", Context.MODE_PRIVATE);
    }

    /**
     * Gets the single instance of SaveData.
     *
     * @param context the context
     * @return single instance of SaveData
     */
    public static SaveData getInstance(Context context) {
        if (saveData == null) {
            saveData = new SaveData(context);
        }
        return saveData;
    }

    public void reset() {
        sharePreference.edit().clear().commit();
        saveData = null;
    }

    /**
     * Sets the internet connecting.
     *
     * @param isConnected the new internet connecting
     */
    public void setInternetConnecting(boolean isConnected) {
        sharePreference.edit().putBoolean("network", isConnected).commit();
    }

    /**
     * Checks if is internet connecting.
     *
     * @return true, if is internet connecting
     */
    public boolean isInternetConnecting() {
        return sharePreference.getBoolean("network", false);
    }

    public void setLoggedIn(boolean isLogin) {
        sharePreference.edit().putBoolean("isLogin", isLogin).commit();
    }

    public boolean isLoggedIn() {
        return sharePreference.getBoolean("isLogin", false);
    }
}
