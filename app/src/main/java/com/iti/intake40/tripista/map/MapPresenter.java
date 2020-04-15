package com.iti.intake40.tripista.map;

import com.iti.intake40.tripista.core.FireBaseCore;

public class MapPresenter implements MapContract.PresenterInterface {
    private FireBaseCore core;
    MapContract.ViewInterface map;

    public MapPresenter(FireBaseCore core, MapContract.ViewInterface map) {
        this.core = core;
        this.map = map;
    }

    @Override
    public void getTripById(String id) {
        core.getSpecificTrip(id,map);
    }
}
