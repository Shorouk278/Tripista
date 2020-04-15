package com.iti.intake40.tripista.trip;

import com.iti.intake40.tripista.core.model.Trip;

import java.util.Calendar;

public interface AddTripContract {
    interface PresenterInterface {
        void replyByMessage(int message);

        void replyByError(int message);

        void addTrip(Trip trip , Calendar calendar);

        void setData (Trip trip );
    }

    interface ViewInterface {
        void sentMessage(int message);

        void sentError(int message);

        void setAlarm (Trip trip, Calendar calendar, String status,String type);



    }
}
