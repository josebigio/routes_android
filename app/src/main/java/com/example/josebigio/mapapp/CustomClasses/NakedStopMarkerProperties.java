package com.example.josebigio.mapapp.CustomClasses;

import com.example.josebigio.mapapp.model.Stop;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by josebigio on 7/1/15.
 */
public class NakedStopMarkerProperties extends MarkerProperties {

    private Stop stop;


    public NakedStopMarkerProperties(LatLng latLng,Stop stop){
        super(latLng);
        this.stop = stop;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
