
package com.sontruong.appsound;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.sontruong.appsound.utils.AnimationUtils;
import com.sontruong.appsound.utils.Constants;
import com.sontruong.appsound.utils.SaveData;
import com.sontruong.appsound.utils.Utils;

import java.io.File;

public class LoginActivity extends FragmentActivity implements RippleView.OnRippleCompleteListener {
    private TextView mUserName;

    private EditText mPassword;

    private RippleView mLoginButton;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            String pathPhoto = Environment.getExternalStorageDirectory() + Constants.PATH_APP + Constants.PATH_PHOTO;
            String pathSound = Environment.getExternalStorageDirectory() + Constants.PATH_APP + Constants.PATH_SOUND;
            File filePhoto = new File(pathPhoto);
            File fileSound = new File(pathSound);
            if (!filePhoto.exists()) {
                Utils.forceMkdir(filePhoto);
            }
            if (!fileSound.exists()) {
                Utils.forceMkdir(fileSound);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SaveData.getInstance(this).setLoggedIn(false);

        mUserName = (TextView) findViewById(R.id.user_name_id);
        mPassword = (EditText) findViewById(R.id.password_id);
        mLoginButton = (RippleView) findViewById(R.id.login_ripple_id);
        mLoginButton.setOnRippleCompleteListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount == 0) {
            finish();
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (username.isEmpty()) {
            mUserName.setError(getString(R.string.login_error_username));
            AnimationUtils.shake(this.getApplicationContext(), mUserName);
        } else if (password.isEmpty()) {
            mPassword.setError(getString(R.string.login_error_password));
            AnimationUtils.shake(this.getApplicationContext(), mPassword);
        } else {
            SaveData.getInstance(this).setLoggedIn(true);
            Intent intent = new Intent(LoginActivity.this, PhotoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoginActivity.this.startActivity(intent);
            finish();
        }
    }
}

