package com.example.josebigio.mapapp;

import android.app.Activity;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.josebigio.mapapp.Utilities.ColorGenerator;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity implements LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    static final String TAG = "MAIN";


    static final String ROUTES_API="https://routes-app-pro.herokuapp.com";
    static final double RADIUS = 0.25;
    static final int LIMIT = 2700; //The range in seconds for when looking at upcoming buses

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;


    GoogleMap googleMap;
    List<StopPOJO> stopPOJOsArray;
    HashMap<Marker,Stop> markerStopHashMap;
    HashMap<Marker,List<Polyline>> markerPolylinesHashMap;
    HashMap<Marker,LatLng> markerLatLngHashMap; //used for hackiness
    HashMap<Marker,LatLng> clusterLatLngHashMap;
    HashMap<Marker,List<Marker>>clusterMarkersHashMap;


    Boolean alreadyZoomed;
    Boolean isLoadingAPICall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        alreadyZoomed = false;
        isLoadingAPICall = false;
        stopPOJOsArray = new ArrayList<>();
        markerStopHashMap = new HashMap<>(); // TODO: remove memory leaks
        markerLatLngHashMap = new HashMap<>(); //TODO: remove memory leaks
        markerPolylinesHashMap = new HashMap<>(); //TODO: remove memory leaks
        clusterMarkersHashMap = new HashMap<>(); //TODO: remove memory leaks
        clusterLatLngHashMap = new HashMap<>(); //TODO: remove memory leaks
        createMapView();

    }


    /**
     * Initialises the mapview and zooms it to the user's current location
     */
    private void createMapView() {

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.mapView)).getMap();

        if(googleMap==null){
            Log.e(TAG,"GOOGLE MAPS WAS NULL???");
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapLongClickListener(this);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria,true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null)
            onLocationChanged(location);

        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(!alreadyZoomed){
            alreadyZoomed = true;
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

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


    public void drawStopPOJO(StopPOJO stopPOJO, LatLng latLng) {

        //The cluster first
        Marker cluster = googleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(latLng));
        List<Stop> stopList = stopPOJO.getStops();
        List<Marker> markers = new ArrayList<>();
        clusterMarkersHashMap.put(cluster,markers);

        for (Stop stop : stopList) {
            List<Route> routes = stop.getRoutes();
            if(routes == null) continue;
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
            markerPolylinesHashMap.put(m,null);

            markers.add(m);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        //check typeOfMarker
        if(markerStopHashMap.containsKey(marker)) {
            Log.d(TAG,"Handle marker long detected");
            handleMarkerLongClick(marker);
        }else{
            Log.d(TAG,"Handle clust long detected");
            handleClusterLongClick(marker);
        }

    }

    /*
        Draws the polilines for all the routes in that stop.
        If they where already drawn, then this cleans them
     */
    private void handleMarkerLongClick(Marker marker){

        Stop stop = markerStopHashMap.get(marker);
        if(stop == null)
            return;

        List<Polyline> oldPolylineList = markerPolylinesHashMap.get(marker);
        if(oldPolylineList!=null) { //the user just wants to remove them :)
            for(Polyline polyline: oldPolylineList){
                polyline.remove();
            }
            markerPolylinesHashMap.put(marker,null);
            marker.setPosition(markerLatLngHashMap.get(marker)); //to make the marker stay
            return;
        }

        List<Polyline> polylinesList = drawPolylinesForStop(stop);
        markerPolylinesHashMap.put(marker,polylinesList);

        marker.setPosition(markerLatLngHashMap.get(marker)); //to make the marker stay
    }

    /*
        Remove everything related to this cluster
     */
    private void handleClusterLongClick(Marker cluster){

        List<Marker> markersToRemove = clusterMarkersHashMap.get(cluster);
        for(Marker m:markersToRemove){

            List<Polyline> polylines = markerPolylinesHashMap.get(m);
            if(polylines!=null){
                for(Polyline p: polylines){
                    p.remove();
                }
            }
            m.remove();
            markerStopHashMap.remove(m);
            markerLatLngHashMap.remove(m);
            markerPolylinesHashMap.remove(m);
        }
        clusterMarkersHashMap.remove(cluster);
        cluster.remove();
    }

    private List<Polyline> drawPolylinesForStop(Stop stop){

        List<Polyline> result = new ArrayList<>();

        for(Route r:stop.getRoutes()){
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(ColorGenerator.getColorForRoute(r.getRouteName()));
            for(Coordinate coordinate: r.getCoordinates()){
                polylineOptions.add(new LatLng(coordinate.getLat(),coordinate.getLng()));
            }
            result.add(googleMap.addPolyline(polylineOptions));
        }

        return result;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.setPosition(markerLatLngHashMap.get(marker));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.setPosition(markerLatLngHashMap.get(marker));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG,"LongClick detected");
        drawNewCluster(latLng);
    }

    private void drawNewCluster(final LatLng latLng){
        if(isLoadingAPICall)
            return;

        isLoadingAPICall = true;
        progressBar.setVisibility(View.VISIBLE);

        Date now = new Date();
        String time = new SimpleDateFormat("kk:mm:ss").format(now);
        Calendar c = Calendar.getInstance();
        int weekDay = c.get(Calendar.DAY_OF_WEEK);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ROUTES_API).build();
        RoutesAPI routes = restAdapter.create(RoutesAPI.class);
        routes.getRoutesAround(latLng.latitude+"", latLng.longitude+"", RADIUS+"",time, weekDay+"", LIMIT+"", new Callback<StopPOJO>() {
            @Override
            public void success(StopPOJO stopPOJOR, Response response) {
                stopPOJOsArray.add(stopPOJOR); //do we need this?
                drawStopPOJO(stopPOJOR,latLng);
                isLoadingAPICall = false;
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
               Log.e(TAG,"Api call failed");
            }
        });

    }
}
