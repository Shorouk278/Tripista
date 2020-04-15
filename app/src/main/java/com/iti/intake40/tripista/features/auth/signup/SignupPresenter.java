package com.iti.intake40.tripista.features.auth.signup;


import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.UserModel;

public class SignupPresenter implements SignupContract.PresenterInterface {
    private FireBaseCore core;
    private SignupContract.ViewInterface signUp;

    public SignupPresenter(SignUp signUp, FireBaseCore core) {
        this.core = core;
        this.signUp = signUp;
    }

    @Override
    public void signup(UserModel model) {
        core.signUpEithEmailAndPassword(model, this);

    }

    @Override
    public void replyByMessage(int message) {
        signUp.sentMessage(message);
    }

    @Override
    public void replyByError(int message) {
        signUp.sentError(message);
    }

    @Override
    public void replayByChangeActivity() {
        signUp.changeActivity();
    }

}
