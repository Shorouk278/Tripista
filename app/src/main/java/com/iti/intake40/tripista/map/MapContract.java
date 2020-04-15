package com.iti.intake40.tripista.map;

import com.iti.intake40.tripista.core.model.Trip;

public interface MapContract {
    interface PresenterInterface {
       void getTripById(String id);
    }

    interface ViewInterface {
        void setTripData(Trip trip);
    }
}
