package com.iti.intake40.tripista.features.auth.signup;

import com.iti.intake40.tripista.core.model.UserModel;

public interface SignupContract {
    interface PresenterInterface {

        void signup(UserModel model);

        void replyByMessage(int message);

        void replyByError(int message);

        void replayByChangeActivity();
    }

    interface ViewInterface {
        void sentMessage(int message);

        void sentError(int message);

        void changeActivity();
    }

}

