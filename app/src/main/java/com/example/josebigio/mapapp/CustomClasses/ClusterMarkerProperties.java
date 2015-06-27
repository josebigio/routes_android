package com.example.josebigio.mapapp.CustomClasses;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by josebigio on 6/26/15.
 */
public class ClusterMarkerProperties extends MarkerProperties {

    private List<Marker> stopMarkers;

    public ClusterMarkerProperties(List<Marker> stopMarkers) {
        this.stopMarkers = stopMarkers;
    }

    public List<Marker> getStopMarkers() {
        return stopMarkers;
    }

    public void setStopMarkers(List<Marker> stopMarkers) {
        this.stopMarkers = stopMarkers;
    }
}
