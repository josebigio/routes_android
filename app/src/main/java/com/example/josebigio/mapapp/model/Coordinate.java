package com.example.josebigio.mapapp.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Coordinate {

    @SerializedName("distance_traveled")
    @Expose
    private Double distanceTraveled;
    @Expose
    private Double lat;
    @Expose
    private Double lng;
    @Expose
    private Integer sequence;
    @SerializedName("shape_id")
    @Expose
    private Integer shapeId;

    /**
     *
     * @return
     * The distanceTraveled
     */
    public Double getDistanceTraveled() {
        return distanceTraveled;
    }

    /**
     *
     * @param distanceTraveled
     * The distance_traveled
     */
    public void setDistanceTraveled(Double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
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
     * The sequence
     */
    public Integer getSequence() {
        return sequence;
    }

    /**
     *
     * @param sequence
     * The sequence
     */
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    /**
     *
     * @return
     * The shapeId
     */
    public Integer getShapeId() {
        return shapeId;
    }

    /**
     *
     * @param shapeId
     * The shape_id
     */
    public void setShapeId(Integer shapeId) {
        this.shapeId = shapeId;
    }

}