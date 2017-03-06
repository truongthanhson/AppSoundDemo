
package com.poptech.popap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.poptech.popap.login.*;
import com.poptech.popap.utils.SaveData;

public class LauncherActivity extends FragmentActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SaveData.getInstance(this).isLoggedIn()){
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, com.poptech.popap.login.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}

