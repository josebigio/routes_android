package com.example.josebigio.mapapp;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josebigio.mapapp.api.RoutesAPI;
import com.example.josebigio.mapapp.model.Route;
import com.example.josebigio.mapapp.model.Stop;
import com.example.josebigio.mapapp.model.StopPOJO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends Activity implements LocationListener {

    static final String ROUTES_API="https://routes-app-pro.herokuapp.com";
    static final double RADIUS = 0.25;
    static final int LIMIT = 2700; //The range in seconds for when looking at upcoming buses

    GoogleMap googleMap;
    StopPOJO stopPOJO;

    @InjectView(R.id.stopsAroundButton) Button stopsAroundButton;
    Boolean alreadyZoomed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        alreadyZoomed = false;
        createMapView();

    }


    /**
     * Initialises the mapview and zooms it to the user's current location
     */
    private void createMapView() {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
        googleMap.setMyLocationEnabled(true);


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
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        if(!alreadyZoomed){
            alreadyZoomed = true;
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }


        Date now = new Date();
        String time = new SimpleDateFormat("kk:mm:ss").format(now);
        System.out.println("Time: " + time);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        System.out.println("Day: " + day);


        updateStopPOJO(location, RADIUS, time, day, LIMIT);
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

    public void updateStopPOJO(Location location, double radius, String time, int weekDay, int secondsLimit) {


        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ROUTES_API).build();
        RoutesAPI routes = restAdapter.create(RoutesAPI.class);
        routes.getRoutesAround(location.getLatitude()+"", location.getLongitude()+"", radius+"",time, weekDay+"", secondsLimit+"", new Callback<StopPOJO>() {
            @Override
            public void success(StopPOJO stopPOJOR, Response response) {
                //we get json object from github server to our POJO or model class
                stopPOJO = stopPOJOR;
                StringBuffer resultString = new StringBuffer();
//                List<Stop> stopList = stopPOJOR.getStops();
//                for (Stop stop : stopList) {
//                    resultString.append("stop id: " + stop.getStopId() + "\n");
//                    List<Route> routeList = stop.getRoutes();
//                    for (Route route : routeList) {
//                        resultString.append("route name: " + route.getRouteName() + ", " + route.getArrivalTime() + ", headSign: " + route.getHeadsign() +"\n");
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
    public void drawAnnotationsOfBusStops(View view) {
       if(stopPOJO==null) return;

        googleMap.clear();
        List<Stop> stopList = stopPOJO.getStops();
        for (Stop stop : stopList) {
            List<Route> routes = stop.getRoutes();
            StringBuffer headSigns = new StringBuffer();
            for(Route route: routes)
                headSigns.append(route.getHeadsign()+": " + route.getArrivalTime() + "\n");
            System.out.println("headisngs: " + headSigns);
            googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(stop.getLat(),stop.getLng()))
            .title(stop.getStopId()+"")
            .snippet(headSigns.toString()));
        }



    }
}
