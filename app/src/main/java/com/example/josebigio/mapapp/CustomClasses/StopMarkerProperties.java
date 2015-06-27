package com.example.josebigio.mapapp.CustomClasses;

import com.example.josebigio.mapapp.model.Stop;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

/**
 * Created by josebigio on 6/26/15.
 */
public class StopMarkerProperties extends MarkerProperties {

    private List<Polyline> polylines;
    private Stop stop;

    public StopMarkerProperties(Stop stop, LatLng latlng, List<Polyline> polylines){
        super(latlng);
        this.stop = stop;
        this.polylines = polylines;
    }

    public StopMarkerProperties(List<Polyline> polylines){
        this.polylines = polylines;
    }

    public List<Polyline> getPolylines() {
        return polylines;
    }

    public void setPolylines(List<Polyline> polylines) {
        this.polylines = polylines;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
