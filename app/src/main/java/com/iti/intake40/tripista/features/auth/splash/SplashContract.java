package com.iti.intake40.tripista.features.auth.splash;

public interface SplashContract {
    interface ViewInterfce {
        void goToHome();

        void goToSignIn();
    }

    interface PresenterInterface {
        void checkCurrentUSer();

        void replayByChangeActivty(Boolean isExist);
    }

}
