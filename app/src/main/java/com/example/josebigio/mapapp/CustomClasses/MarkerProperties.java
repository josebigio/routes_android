package com.example.josebigio.mapapp.CustomClasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

/**
 * Created by josebigio on 6/26/15.
 */
public class MarkerProperties {

    private LatLng latLng;



    public MarkerProperties(){

    }

    public MarkerProperties(LatLng latLng){
        this.latLng = latLng;
    }



    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }








}
