package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.NearbyPlaces;
import com.example.shawon.travelbd.ModelClass.Results;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Remote.Common;
import com.example.shawon.travelbd.Remote.IGoogleAPIService;
import com.example.shawon.travelbd.Utils.IsConnectedToInternet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlacesMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "NearbyPlacesMapView";
    private Context context = NearbyPlacesMapActivity.this;

    private GoogleApiClient mGoogleApiClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float DEFAULT_ZOOM = 13f;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private View mapView;
    private Location mCurrentLocation;

    private IGoogleAPIService mService;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private String placeTypes = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places_mapview);
        Log.d(TAG,"onCreate : Started");

        if (IsConnectedToInternet.isConnectedToInternet(context)) {
            mService = Common.getGoogleAPIService();
            getLocationPermission();
            getNearbyPlaces();
        }
        else {
            Toast.makeText(context,"Please check your internet connection.",Toast.LENGTH_SHORT).show();
        }
    }

    private void getNearbyPlaces() {
        Log.d(TAG,"getting Intent from previous activity.");

        if (getIntent().hasExtra(getString(R.string.place_types))) {
            String getIntent = getIntent().getStringExtra(getString(R.string.place_types));
            if (getIntent.length() != 0) {
                int line = 0;
                String s = "";
                for (int i = 0; i < getIntent.length(); i++) {
                    if (getIntent.charAt(i) == '|') {
                        ++line;
                        if (line == 1) {
                            latitude = Double.parseDouble(s);
                            s = "";
                        } else if (line == 2) {
                            longitude = Double.parseDouble(s);
                            s = "";
                        }
                    } else {
                        s += getIntent.charAt(i);
                    }
                }
                placeTypes = s;
            }
        }

        // Setting the url...
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+5000);
        googlePlacesUrl.append("&type="+placeTypes);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCl-djuKzjuj3TN6UVJNcob0PlpB4IwIs4");
        String url = googlePlacesUrl.toString();
        Log.d(TAG,"getNearbyPlaces : URL : "+url);

        mService.getNearByPlaces(url)
            .enqueue(new Callback<NearbyPlaces>() {
                @Override
                public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                    if (response.isSuccessful()){

                        LatLng latlng = new LatLng(latitude,longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM));
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(latlng);
                        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        mMap.addMarker(markerOption);

                        for (int i = 0; i<response.body().getResults().length; i++) {
                            Results googlePlaces = response.body().getResults()[i];

                            double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                            double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());
                            LatLng latLng = new LatLng(lat,lng);
                            String placeName = googlePlaces.getName();
                            String placeRating = googlePlaces.getRating();
                            if (placeRating == null) placeRating = "2.5";

                            if(placeTypes.equals("restaurant")
                                    ||placeTypes.equals("cafe")
                                    ||placeTypes.equals("park")
                                    ||placeTypes.equals("natural_feature")
                                    ||placeTypes.equals("movie_theater")
                                    ||placeTypes.equals("shopping_mall")
                                    ||placeTypes.equals("lodging")) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                markerOptions.snippet("Rating : "+placeRating+"/5");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(markerOptions).showInfoWindow();
                            }
                            else if(placeTypes.equals("atm")){
                                mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(placeName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                        .showInfoWindow();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<NearbyPlaces> call, Throwable t) {

                }
            });
    }

    private void initMap() {
        Log.d(TAG,"initMap : Initializing the map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearby_places_map);
        mapFragment.getMapAsync(NearbyPlacesMapActivity.this);
        mapView = mapFragment.getView();
    }

    private synchronized void buildGoogleApiClientForCurrentLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission : Getting location permissions...");

        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
                buildGoogleApiClientForCurrentLocation();
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult : Called");

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0){
                    for (int i=0; i<grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                        }
                        else {
                            mLocationPermissionGranted = true;
                            break;
                        }
                    }
                    if (mLocationPermissionGranted) {
                        Log.d(TAG,"onRequestPermissionsResult : Permission Granted");
                        initMap();
                        buildGoogleApiClientForCurrentLocation();
                    }
                    else{
                        Log.d(TAG,"onRequestPermissionsResult : Permission Failed");
                    }
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting the devices current location");

        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParms = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParms.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParms.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            layoutParms.setMargins(0,0,20,120);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"GoogleApiClient is Connected : Trying to get current location.");

        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                Log.d(TAG,"mGoogleApiClient : onConnected : Location Found");
                getDeviceLocation();
            }
            else {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
                if (mCurrentLocation == null) {
                    Log.d(TAG, "mGoogleApiClient : onConnected : Location is null");
                    Toast.makeText(context, "Unable to get current location. Please turn on your location!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"mGoogleApiClient : onConnected : Location Found");
                    getDeviceLocation();
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // later code
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady : GoogleMap is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }
}