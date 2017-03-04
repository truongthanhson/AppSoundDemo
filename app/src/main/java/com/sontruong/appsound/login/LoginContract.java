package com.sontruong.appsound.login;

import com.sontruong.appsound.BasePresenter;
import com.sontruong.appsound.BaseView;

/**
 * Created by sontt on 04/03/2017.
 */

public class LoginContract {

    interface View extends BaseView<Presenter>{
        void showErrorPassword(String error);
        void showErrorUsernam(String error);
        void onLoginSuccessful();
    }

    interface Presenter extends BasePresenter{
        void login(String userName, String password);
    }
}
