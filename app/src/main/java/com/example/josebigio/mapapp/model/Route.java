
package com.example.josebigio.mapapp.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Route {

    @SerializedName("arrival_time")
    @Expose
    private String arrivalTime;
    @Expose
    private List<Coordinate> coordinates = new ArrayList<Coordinate>();
    @Expose
    private String headsign;
    @SerializedName("route_name")
    @Expose
    private Integer routeName;
    @Expose
    private String color;

    /**
     *
     * @return
     * The arrivalTime
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     *
     * @param arrivalTime
     * The arrival_time
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     *
     * @return
     * The coordinates
     */
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    /**
     *
     * @param coordinates
     * The coordinates
     */
    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     *
     * @return
     * The headsign
     */
    public String getHeadsign() {
        return headsign;
    }

    /**
     *
     * @param headsign
     * The headsign
     */
    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    /**
     *
     * @return
     * The color
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @param color
     * The color
     */
    public void setColor(String color) {
        this.color = headsign;
    }

    /**
     *
     * @return
     * The routeName
     */
    public Integer getRouteName() {
        return routeName;
    }

    /**
     *
     * @param routeName
     * The route_name
     */
    public void setRouteName(Integer routeName) {
        this.routeName = routeName;
    }

}