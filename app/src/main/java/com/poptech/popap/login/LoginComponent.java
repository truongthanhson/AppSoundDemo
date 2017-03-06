package com.poptech.popap.login;

import com.poptech.popap.di.AppComponent;
import com.poptech.popap.utils.FragmentScoped;

import dagger.Component;

/**
 * Created by sontt on 04/03/2017.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class,modules = LoginPresenterModule.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
