package com.example.josebigio.mapapp.api;


import com.example.josebigio.mapapp.model.StopPOJO;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by josebigio on 6/21/15.
 */
public interface RoutesAPI {
    @GET("/api/getroutesandcoordinates")
    public void getRoutesAround(@Query("lat") String lat,@Query("lng") String lng, @Query("radius") String radius, @Query(value="time",encodeValue = false) String time,@Query("weekday") String weekday,@Query("limit") String limit,  Callback<StopPOJO> response);

    @GET("/api/getroutes")
    public Observable<StopPOJO> getRoutesAround(@Query("lat") String lat,@Query("lng") String lng, @Query("radius") String radius, @Query(value="time",encodeValue = false) String time,@Query("weekday") String weekday,@Query("limit") String limit);
}
