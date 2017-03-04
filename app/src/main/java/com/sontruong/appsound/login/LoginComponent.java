package com.sontruong.appsound.login;

import com.sontruong.appsound.di.AppComponent;
import com.sontruong.appsound.utils.FragmentScoped;

import dagger.Component;

/**
 * Created by sontt on 04/03/2017.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class,modules = LoginPresenterModule.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
