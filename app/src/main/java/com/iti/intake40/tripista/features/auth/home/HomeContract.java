package com.iti.intake40.tripista.features.auth.home;

import com.google.firebase.auth.FirebaseUser;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.core.model.UserModel;

import java.util.List;

public interface HomeContract {
    interface PresenterInterface {
        void replyByMessage(int message);

        void replyByError(int message);

        void replayByChangeFragment(FirebaseUser user);

        void replayChangeActivity();

        void signOut();

        void setUserInfo(UserModel model);

        void fetchUserInFo();

        void fetchUserInfoByPhone(String number);

        List<Trip> getUserTrips();
    }

    interface ViewInterface {
        void sentMessage(int message);

        void sentError(int message);

        void changeActivity();

        void showUserInfo(UserModel model);
    }
}


