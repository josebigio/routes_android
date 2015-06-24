package com.example.josebigio.mapapp;

import android.app.Activity;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.josebigio.mapapp.api.RoutesAPI;
import com.example.josebigio.mapapp.model.Coordinate;
import com.example.josebigio.mapapp.model.Route;
import com.example.josebigio.mapapp.model.Stop;
import com.example.josebigio.mapapp.model.StopPOJO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity implements LocationListener, GoogleMap.OnMarkerDragListener {

    static final String ROUTES_API="https://routes-app-pro.herokuapp.com";
    static final double RADIUS = 0.25;
    static final int LIMIT = 2700; //The range in seconds for when looking at upcoming buses
    static final int MAX_STOP_POJO = 3;

    GoogleMap googleMap;
    List<StopPOJO> stopPOJOsArray;
    StopPOJO stopPOJO;
    HashMap<Marker,Stop> markerStopHashMap;
    HashMap<Marker,LatLng> markerLatLngHashMap; //use for hackiness
    Location lastLocation;
    LatLng floatingMarkerPosition;

    @InjectView(R.id.stopsAroundButton) Button stopsAroundButton;
    Boolean alreadyZoomed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        alreadyZoomed = false;
        stopPOJOsArray = new ArrayList<>();
        markerStopHashMap = new HashMap<>(); // TODO: remove memory leaks
        markerLatLngHashMap = new HashMap<>(); //TODO: remove memory leaks
        createMapView();

    }


    /**
     * Initialises the mapview and zooms it to the user's current location
     */
    private void createMapView() {

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.mapView)).getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerDragListener(this);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria,true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            System.out.println("Location was not null");
            onLocationChanged(location);
        }
        else {
            System.out.println("location was null");
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed!! to: " + location);
        lastLocation = location;

        if(!alreadyZoomed){
            alreadyZoomed = true;
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        if(stopPOJO == null)
            updateStopPOJO(lastLocation, RADIUS,LIMIT);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateStopPOJO(Location location, double radius, int secondsLimit) {

        Date now = new Date();
        String time = new SimpleDateFormat("kk:mm:ss").format(now);
        Calendar c = Calendar.getInstance();
        int weekDay = c.get(Calendar.DAY_OF_WEEK);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ROUTES_API).build();
        RoutesAPI routes = restAdapter.create(RoutesAPI.class);
        routes.getRoutesAround(location.getLatitude()+"", location.getLongitude()+"", radius+"",time, weekDay+"", secondsLimit+"", new Callback<StopPOJO>() {
            @Override
            public void success(StopPOJO stopPOJOR, Response response) {
                stopPOJO = stopPOJOR;
                stopsAroundButton.setText("KABOOOM!!!");
//                StringBuffer resultString = new StringBuffer();
//                List<Stop> stopList = stopPOJOR.getStops();
//                for (Stop stop : stopList) {
//                    resultString.append("stop id: " + stop.getStopId() + "\n");
//                    List<Route> routeList = stop.getRoutes();
//                    for (Route route : routeList) {
//                        resultString.append("route name: " + route.getRouteName() + ", " + route.getArrivalTime() + ", headSign: " + route.getHeadsign() + ", number of coords: " + route.getCoordinates().size() + "\n");
//                    }
//                    resultString.append("distance: " + stop.getDistance() + "\n");
//                }
//
//                System.out.println(resultString);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getMessage());
            }
        });

    }

    @OnClick(R.id.stopsAroundButton)
    public void stopsAroundButtonPress(View view) {
        if(stopPOJO==null) return;
        googleMap.clear();
        drawStopPOJO(stopPOJO);
    }

    @OnClick(R.id.updateLocationButton)
    public void updateLocationButtonPress(View view) {
        stopsAroundButton.setText("wait...");
        updateStopPOJO(lastLocation, RADIUS, LIMIT);
    }

    public void drawStopPOJO(StopPOJO stopPOJO) {
        List<Stop> stopList = stopPOJO.getStops();
        for (Stop stop : stopList) {
            List<Route> routes = stop.getRoutes();
            if(routes == null) return;
            StringBuffer headSigns = new StringBuffer();
            for(Route route: routes)
                headSigns.append(route.getHeadsign()+": " + route.getArrivalTime() + "\n");

            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getLat(), stop.getLng()))
                    .title(stop.getStopId() + "")
                    .snippet(headSigns.toString())
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_stop)));
            markerStopHashMap.put(m, stop);
            markerLatLngHashMap.put(m,m.getPosition());
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        Stop stop = markerStopHashMap.get(marker);
        if(stop == null)
            return;

        for(Route r:stop.getRoutes()){
            PolylineOptions polylineOptions = new PolylineOptions();
            System.out.println("color: #" + r.getColor());
            polylineOptions.color(Color.parseColor("#"+r.getColor()));
            for(Coordinate coordinate: r.getCoordinates()){
                polylineOptions.add(new LatLng(coordinate.getLat(),coordinate.getLng()));
            }
            googleMap.addPolyline(polylineOptions);
        }

        marker.setPosition(markerLatLngHashMap.get(marker));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.setPosition(markerLatLngHashMap.get(marker));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.setPosition(markerLatLngHashMap.get(marker));
    }
}
