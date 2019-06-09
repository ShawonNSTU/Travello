package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.example.shawon.travelbd.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by SHAWON on 6/9/2019.
 */

public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private double currLongitude;
    private double currLatitude;
    private String currPlaceName;
    private GoogleApiClient googleApiClient;
    String getIntent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        if (getIntent() != null) {
            getIntent = getIntent().getStringExtra("Location");
            char[] arr = getIntent.toCharArray();
            String s = "";
            int cnt = 1;
            for (int i = 0; i < getIntent.length(); i++) {
                if (arr[i] == ' ' && cnt<=2) {
                    if (cnt == 1) {
                        currLatitude = Double.parseDouble(s);
                    } else if (cnt == 2) {
                        currLongitude = Double.parseDouble(s);
                    }
                    s = "";
                    ++cnt;
                } else s += arr[i];
            }
            currPlaceName = s;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(DirectionActivity.this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void moveMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(currPlaceName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                .showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,21.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        moveMap(currLatitude, currLongitude);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null)
            googleApiClient.disconnect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}