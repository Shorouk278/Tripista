package com.iti.intake40.tripista.trip;

import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;

import java.util.Calendar;

public class AddTripPresenter implements AddTripContract.PresenterInterface {
    private FireBaseCore core;
    private AddTripContract.ViewInterface addTrip;
    private Calendar targetCalender;
    private String status;
    private String type;

    public AddTripPresenter(FireBaseCore core, AddTripContract.ViewInterface addTrip) {
        this.core = core;
        this.addTrip = addTrip;
    }

    @Override
    public void replyByMessage(int message) {

    }

    @Override
    public void replyByError(int message) {

    }

    @Override
    public void addTrip(Trip trip, Calendar calendar) {
        targetCalender = calendar;
        core.addTrip(trip, this);
    }

    @Override
    public void setData(Trip trip) {
        addTrip.setAlarm(trip,targetCalender,status,type);
       }


    }

