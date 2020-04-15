package com.iti.intake40.tripista.features.auth.splash;

import com.iti.intake40.tripista.core.FireBaseCore;

public class SplashPresenter implements SplashContract.PresenterInterface {
    private FireBaseCore core;
    private SplashContract.ViewInterfce splash;


    public SplashPresenter(FireBaseCore core, SplashContract.ViewInterfce splash) {
        this.core = core;
        this.splash = splash;
    }

    @Override
    public void checkCurrentUSer() {
        core.checkCurrentUser(this);
    }

    @Override
    public void replayByChangeActivty(Boolean isExist) {
        if (isExist) {
            splash.goToHome();
        } else {
            splash.goToSignIn();

        }
    }
}
