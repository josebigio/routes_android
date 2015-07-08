
package com.example.josebigio.mapapp.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Generated;

import com.example.josebigio.mapapp.Utilities.ColorGenerator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Stop {

    @Expose
    private Boolean data;
    @Expose
    private Double distance;
    @Expose
    private Double lat;
    @Expose
    private Double lng;
    @Expose
    private List<Route> routes = new ArrayList<Route>();
    @SerializedName("stop_id")
    @Expose
    private Integer stopId;


    /**
     *
     * @return
     * The distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     *
     * @param routes
     * The routes
     */
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     *
     * @return
     * The stopId
     */
    public Integer getStopId() {
        return stopId;
    }

    /**
     *
     * @param stopId
     * The stop_id
     */
    public void setStopId(Integer stopId) {
        this.stopId = stopId;
    }

    /**
     *
     * @return
     * If it has data
     */
    public Boolean getData() {
        return data;
    }

    /**
     *
     * @param data boolean
     * The data
     */
    public void setData(Boolean data) {
        this.data = data;
    }

    public int[] getStopColors(){
        List<Route> routeList = getRoutes();
        HashSet<String> set = new HashSet();
        for (Route route : routes) {
            set.add(route.getHeadsign());
        }

        int colors[] = new int[set.size()];
        int i = 0;
        for(String headSign: set){
            colors[i]= ColorGenerator.getColorForRoute(headSign);
            i++;
        }
        if (colors.length==0)
            colors = new int[]{Color.WHITE};

        return colors;
    }



}
