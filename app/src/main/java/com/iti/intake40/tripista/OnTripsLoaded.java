package com.iti.intake40.tripista;

import com.iti.intake40.tripista.core.model.Trip;

import java.util.List;

public interface OnTripsLoaded {
    void onTripsLoaded(List<Trip> trips);
}
