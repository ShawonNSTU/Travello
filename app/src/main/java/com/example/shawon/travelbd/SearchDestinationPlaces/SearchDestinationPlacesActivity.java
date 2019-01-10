package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.IsConnectedToInternet;
import com.example.shawon.travelbd.Utils.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHAWON on 1/3/2019.
 */

public class SearchDestinationPlacesActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "SearchDestinationPlaces";
    private Context context = SearchDestinationPlacesActivity.this;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private GoogleApiClient mGoogleApiClientForGooglePlaces,mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float DEFAULT_ZOOM = 17f;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private View mapView;
    private Location location;

    private GeoDataClient geoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private PlacePhotoMetadata mSinglePhotoMetadata;

    private TextView mCurrentLocation;
    private ImageView mCurrentLocationImage;

    private boolean isConnectedToInternet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        if(IsConnectedToInternet.isConnectedToInternet(context)) {
            Log.d(TAG,"Setting activity_search_destination_places layout view");
            setContentView(R.layout.activity_search_destination_places);
            isConnectedToInternet = true;
        }
        else {
            Log.d(TAG,"Setting no_internet_connection_view layout");
            setContentView(R.layout.no_internet_connection_view);
            isConnectedToInternet = false;
        }

        Log.d(TAG,"onCreate : Started.");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);

        if(isConnectedToInternet) {
            mCurrentLocation = (TextView) findViewById(R.id.current_location);
            mCurrentLocationImage = (ImageView) findViewById(R.id.current_location_image);
            getLocationPermission();
            geoDataClient = Places.getGeoDataClient(this, null);
            mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);
            buildGoogleApiClient();
        }
    }

    private void initMap() {
        Log.d(TAG,"initMap : Initializing the map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView_current_location);
        mapFragment.getMapAsync(SearchDestinationPlacesActivity.this);
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

    private void buildGoogleApiClient() {
        Log.d(TAG,"build GoogleApiClient For Google Places : started");

        mGoogleApiClientForGooglePlaces = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // Initialization of Google Places Adapter...
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(context,mGoogleApiClientForGooglePlaces,LAT_LNG_BOUNDS,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"Adding the menu resource file.");

        getMenuInflater().inflate(R.menu.search_destination_places_toolbar_menu,menu);

        // getting the search view of the menu...
        SearchView searchView = (SearchView) menu.findItem(R.id.search_destination_places).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint(Html.fromHtml("<small>"+getString(R.string.where_do_you_want_to_go)+"</small>"));

        // getting edit text of the search view to change it's text properties...
        EditText textSearch = ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        textSearch.setTextColor(getResources().getColor(R.color.light_black));
        textSearch.setTextSize(18);

        // getting the AutoCompleteSuggestion object of the search view to add the adapter...
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if (isConnectedToInternet) searchAutoComplete.setAdapter(mPlaceAutocompleteAdapter);

        // Listen to search view item on click event...
        searchAutoComplete.setOnItemClickListener(mAdapterOnItemClickListener);

        return true;
    }

    private void getPlaceID() {
        Log.d(TAG,"getPlaceID : Getting placeID of the Current Location.");

        try {
            Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    PlaceLikelihoodBufferResponse places = task.getResult();
                    PlaceLikelihood placeLikelihood = places.get(0);
                    // request for getting place photo based on the place ID...
                    getPhotoMetaData(placeLikelihood.getPlace().getId());
                    places.release();
                }
            });
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void getPhotoMetaData(String placeId) {
        Log.d(TAG,"getPhotoMetaData : Getting photoMetadataBuffer...");

        final Task<PlacePhotoMetadataResponse> photoResponse = geoDataClient.getPlacePhotos(placeId);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                Log.d(TAG,"number of photos in photoMetadataBuffer : "+photoMetadataBuffer.getCount());
                if (photoMetadataBuffer.getCount()>0) {
                    mSinglePhotoMetadata = photoMetadataBuffer.get(0).freeze();
                    // get a single photo from SinglePhotoMetaData...
                    getPhoto(mSinglePhotoMetadata);
                    photoMetadataBuffer.release();
                }
            }
        });
    }

    private void getPhoto(PlacePhotoMetadata placePhotoMetadata) {
        Log.d(TAG,"getPhoto : Getting a photo from SinglePhotoMetaData.");

        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(placePhotoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                mCurrentLocationImage.setImageBitmap(bitmap);
            }
        });
    }

    private void getGeoLocationLocality(LatLng latlng) {
        Log.d(TAG,"Getting Locality or available city name of the location.");

        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());
        List<android.location.Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latlng.latitude,latlng.longitude, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            TextView mCurrentLocationText = (TextView) findViewById(R.id.current_location_text);
            mCurrentLocationText.setText(getString(R.string.current_location_text));
            mCurrentLocation.setText(addresses.get(0).getLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting the devices current location, move camera, getPlaceID and placePhoto...");

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

        // get city from the latitude and longitude...
        getGeoLocationLocality(latLng);

        // get place ID based on Current Location...
        getPlaceID();

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
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                Log.d(TAG,"mGoogleApiClient : onConnected : Location Found");
                getDeviceLocation();
            }
            else {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
                if (location == null) {
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
        mGoogleApiClientForGooglePlaces.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // later code
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        if (mGoogleApiClientForGooglePlaces != null){
            mGoogleApiClientForGooglePlaces.disconnect();
        }
    }

    /*
        --------------------------- Place Autocomplete Adapter OnItemCLickListener -----------------------
     */

    private AdapterView.OnItemClickListener mAdapterOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG,"Place Autocomplete Adapter: OnItemClickListener : Listened");

            // hide the keyboard...
            InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(SearchDestinationPlacesActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager
                    .HIDE_NOT_ALWAYS);

            // get selected item...
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            // get details of the selected place using place id...
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClientForGooglePlaces, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                Toast.makeText(context,"Sorry, Please try again!",Toast.LENGTH_SHORT).show();
                places.release();
                return;
            }
            final Place place = places.get(0);
            // go to an activity with Latitude & Longitude...
            startActivity(new Intent(context,NearbyPlacesActivity.class));
            places.release();
        }
    };

    @Override
    public void onLocationChanged(Location mLocation) {
        location = mLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady : GoogleMap is ready");
        mMap = googleMap;
    }
}