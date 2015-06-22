
package com.example.josebigio.mapapp.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class StopPOJO {

    @Expose
    private List<Stop> stops = new ArrayList<Stop>();

    /**
     * 
     * @return
     *     The stops
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * 
     * @param stops
     *     The stops
     */
    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

}
