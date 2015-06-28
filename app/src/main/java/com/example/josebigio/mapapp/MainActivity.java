package com.example.josebigio.mapapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.josebigio.mapapp.CustomClasses.ClusterMarkerProperties;
import com.example.josebigio.mapapp.CustomClasses.StopMarkerProperties;
import com.example.josebigio.mapapp.Utilities.ColorGenerator;
import com.example.josebigio.mapapp.api.RoutesAPI;
import com.example.josebigio.mapapp.model.Coordinate;
import com.example.josebigio.mapapp.model.Route;
import com.example.josebigio.mapapp.model.Stop;
import com.example.josebigio.mapapp.model.StopPOJO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity implements LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    static final String TAG = "MAIN";


    static final String ROUTES_API="https://routes-app-pro.herokuapp.com";
    static final double RADIUS = 0.35;
    static final int LIMIT = 2700; //The range in seconds for when looking at upcoming buses
    static final double MIN_DIST_BETWEEN_ARROWS = 0.5;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;


    GoogleMap googleMap;
    List<StopPOJO> stopPOJOsArray;
    HashMap<Marker,StopMarkerProperties> stopMarkerHashmap;
    HashMap<Marker,ClusterMarkerProperties> clusterMarkerHashmap;


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
        stopMarkerHashmap = new HashMap<>();
        clusterMarkerHashmap = new HashMap<>();

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
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stars_black_24dp)));

        List<Stop> stopList = stopPOJO.getStops();
        List<Marker> markers = new ArrayList<>();
        clusterMarkerHashmap.put(cluster, new ClusterMarkerProperties(markers));


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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp)));

            stopMarkerHashmap.put(m,new StopMarkerProperties(stop,m.getPosition(),null));
            markers.add(m);
        }

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        //check typeOfMarker
        if(stopMarkerHashmap.containsKey(marker)) {
            Log.d(TAG,"Handle marker long detected");
            handleMarkerLongClick(marker);
        }else{
            Log.d(TAG, "Handle clust long detected");
            handleClusterLongClick(marker);
        }

    }

    /*
        Draws the polilines for all the routes in that stop.
        If they where already drawn, then this cleans them
     */
    private void handleMarkerLongClick(Marker marker){

        Stop stop = stopMarkerHashmap.get(marker).getStop();
        if(stop == null)
            return;

        List<Polyline> oldPolylineList = stopMarkerHashmap.get(marker).getPolylines();
        if(oldPolylineList!=null) { //the user just wants to remove them :)
            for(Polyline polyline: oldPolylineList){
                polyline.remove();
            }
            stopMarkerHashmap.get(marker).setPolylines(null);
            List<Marker> arrowsToRemove = stopMarkerHashmap.get(marker).getDirectionalArrows();
            if(arrowsToRemove!=null){
                for(Marker markerToRemove:arrowsToRemove){
                    markerToRemove.remove();
                }
            }
            stopMarkerHashmap.get(marker).setDirectionalArrows(null);

            marker.setPosition(stopMarkerHashmap.get(marker).getLatLng());
            return;
        }

        List<Polyline> polylinesList = drawPolylinesForStop(stop);
        List<Marker> directionsList = drawArrowsForStop(stop);
        stopMarkerHashmap.get(marker).setPolylines(polylinesList);
        stopMarkerHashmap.get(marker).setDirectionalArrows(directionsList);

        marker.setPosition(stopMarkerHashmap.get(marker).getLatLng());
    }

    /*
        Remove everything related to this cluster
     */
    private void handleClusterLongClick(Marker cluster){

        List<Marker> markersToRemove = clusterMarkerHashmap.get(cluster).getStopMarkers();
        for(Marker m:markersToRemove){

            List<Polyline> polylines = stopMarkerHashmap.get(m).getPolylines();
            if(polylines!=null){
                for(Polyline p: polylines){
                    p.remove();
                }
            }

            List<Marker> arrowsToRemove = stopMarkerHashmap.get(m).getDirectionalArrows();
            if(arrowsToRemove!=null){
                for(Marker markerToRemove:arrowsToRemove){
                    markerToRemove.remove();
                }
            }
            m.remove();
            stopMarkerHashmap.remove(m);

        }
        clusterMarkerHashmap.remove(cluster);
        cluster.remove();
    }

    private List<Polyline> drawPolylinesForStop(Stop stop){

        List<Polyline> result = new ArrayList<>();

        for(Route r:stop.getRoutes()){
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(ColorGenerator.getColorForRoute(r.getHeadsign()));
            for(Coordinate coordinate: r.getCoordinates()){
                polylineOptions.add(new LatLng(coordinate.getLat(),coordinate.getLng()));
            }
            result.add(googleMap.addPolyline(polylineOptions));
        }

        return result;
    }

    private List<Marker> drawArrowsForStop(Stop stop) {
        List<Marker> result = new ArrayList<>();



        for(Route r:stop.getRoutes()){
            MarkerOptions markerOptions = new MarkerOptions();
            Iterator<Coordinate> frontIterator = r.getCoordinates().iterator();
            Iterator<Coordinate> backIterator = r.getCoordinates().iterator();
            frontIterator.next();
            double distSinceLastArrow = 0;
            while(frontIterator.hasNext()){
                Coordinate originC = backIterator.next();
                Coordinate destC = frontIterator.next();
                double googleAngle = SphericalUtil.computeHeading(new LatLng(originC.getLat(), originC.getLng()), new LatLng(destC.getLat(), destC.getLng()));
                distSinceLastArrow+=(destC.getDistanceTraveled() - originC.getDistanceTraveled());
                BitmapDescriptor arrow = getArrowWithColor(ColorGenerator.getColorForRoute(r.getHeadsign()));
                if(distSinceLastArrow>MIN_DIST_BETWEEN_ARROWS){
                    result.add(googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(originC.getLat(), originC.getLng()))
                            .rotation((float) googleAngle)
                            .icon(arrow)));
                    distSinceLastArrow = 0;
                }


            }
        }

        return result;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(stopMarkerHashmap.containsKey(marker))
            marker.setPosition(stopMarkerHashmap.get(marker).getLatLng());
        else
            marker.setPosition(clusterMarkerHashmap.get(marker).getLatLng());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(stopMarkerHashmap.containsKey(marker))
            marker.setPosition(stopMarkerHashmap.get(marker).getLatLng());
        else
            marker.setPosition(clusterMarkerHashmap.get(marker).getLatLng());
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
        routes.getRoutesAround(latLng.latitude + "", latLng.longitude + "", RADIUS + "", time, weekDay + "", LIMIT + "", new Callback<StopPOJO>() {
            @Override
            public void success(StopPOJO stopPOJOR, Response response) {
                drawStopPOJO(stopPOJOR, latLng);
                isLoadingAPICall = false;
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api call failed");
            }
        });

    }

    private  BitmapDescriptor getArrowWithColor(int color) {

        Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_up_black_24dp).copy(Bitmap.Config.ARGB_8888, true);
        int [] allpixels = new int [ arrowBitmap.getHeight()*arrowBitmap.getWidth()];

        arrowBitmap.getPixels(allpixels, 0, arrowBitmap.getWidth(), 0, 0, arrowBitmap.getWidth(), arrowBitmap.getHeight());

        for(int i =0; i<arrowBitmap.getHeight()*arrowBitmap.getWidth();i++){

            if( allpixels[i] == Color.BLACK)
                allpixels[i] = color;
        }

        arrowBitmap.setPixels(allpixels, 0, arrowBitmap.getWidth(), 0, 0, arrowBitmap.getWidth(), arrowBitmap.getHeight());
        return BitmapDescriptorFactory.fromBitmap(arrowBitmap);
    }
}
