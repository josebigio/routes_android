
package com.example.josebigio.mapapp.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Route {

    @SerializedName("arrival_time")
    @Expose
    private String arrivalTime;
    @Expose
    private String headsign;
    @SerializedName("route_name")
    @Expose
    private Integer routeName;

    /**
     * 
     * @return
     *     The arrivalTime
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * 
     * @param arrivalTime
     *     The arrival_time
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * 
     * @return
     *     The headsign
     */
    public String getHeadsign() {
        return headsign;
    }

    /**
     * 
     * @param headsign
     *     The headsign
     */
    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    /**
     * 
     * @return
     *     The routeName
     */
    public Integer getRouteName() {
        return routeName;
    }

    /**
     * 
     * @param routeName
     *     The route_name
     */
    public void setRouteName(Integer routeName) {
        this.routeName = routeName;
    }

}
