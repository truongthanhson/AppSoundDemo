
package com.sontruong.appsound.login;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.sontruong.appsound.DemoApplication;
import com.sontruong.appsound.R;

import javax.inject.Inject;

public class LoginActivity extends FragmentActivity {
    @Inject
    LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentById(R.id.container);
        if(loginFragment == null){
            loginFragment = new LoginFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container, loginFragment);
            transaction.commit();
        }

        DaggerLoginComponent.builder()
                .appComponent(((DemoApplication)DemoApplication.applicationContext).getAppComponent())
                .loginPresenterModule(new LoginPresenterModule(loginFragment)).build().inject(this);
    }
}

