package com.example.shawon.travelbd.AddPost;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.PlaceInfo;
import com.example.shawon.travelbd.Profile.ProfileFragment;
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
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAWON on 8/28/2018.
 */

public class LocationSelectFromGooglePlaces extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        TextWatcher,
        LocationListener {

    private static final String TAG = "LocationSelectActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float DEFAULT_ZOOM = 17f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private Context context = LocationSelectFromGooglePlaces.this;

    private boolean mLocationPermissionGranted = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient, mGoogleApiClientForGooglePlaces;
    private Location location;
    private Marker marker;
    private View mapView;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private AutoCompleteTextView mInputSearch;
    private ImageView mSaveCheck;

    private PlaceInfo mPlace;
    private String mGeoLocateAddress;

    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photoMetadataList;
    private int mCurrentPhotoIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_select_from_google_places);
        Log.d(TAG, "onCreate : Started");

        mInputSearch = (AutoCompleteTextView) findViewById(R.id.input_search);
        mSaveCheck = (ImageView) findViewById(R.id.save_check);

        if (IsConnectedToInternet.isConnectedToInternet(context)) {
            Log.d(TAG,"IsConnectedToInternet : Success");
            getLocationPermission();
            geoDataClient = Places.getGeoDataClient(this,null);
        }
        else{
            Log.d(TAG,"IsConnectedToInternet : Failed");
            Toast.makeText(context,"Please check your internet connection.",Toast.LENGTH_SHORT).show();
        }
        if(isRootTask()){
            mInputSearch.setHint("Try your traveled places");
        }
        else{
            mInputSearch.setHint("Search your hometown");
        }
    }

    private boolean isRootTask(){
        if(getIntent().getFlags() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    private void initWidgets() {
        Log.d(TAG,"initWidgets : Initializing activity widgets");

        mGoogleApiClientForGooglePlaces = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(context,mGoogleApiClientForGooglePlaces,LAT_LNG_BOUNDS,null);
        mInputSearch.setAdapter(mPlaceAutocompleteAdapter);
        mInputSearch.setOnItemClickListener(mAdapterOnItemClickListener);
        mInputSearch.addTextChangedListener(this);

        mInputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    mInputSearch.dismissDropDown();
                    // Execute our method for searching
                    if (mPlace != null) mPlace = null;
                    geoLocate();
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker != null) marker.hideInfoWindow();
            }
        });

        mSaveCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlace != null && isValidPlace(mInputSearch.getText().toString())){
                    try {
                        String placeName = mPlace.getName();
                        float placeRating = mPlace.getRating();
                        Log.d(TAG,"mSaveCheck : onClick : Location Found By Places Object : Place Name : "+placeName+" Place Ratings :"+placeRating);
                        if(isRootTask()) {
                            Intent intent = new Intent();
                            intent.putExtra(getString(R.string.user_selected_location), placeName + "@" + placeRating);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else{
                            ProfileFragment.mSelectedHometownLocation = placeName;
                            ProfileFragment.latitude = String.valueOf(mPlace.getLatlng().latitude);
                            ProfileFragment.longitude = String.valueOf(mPlace.getLatlng().longitude);
                            finish();
                        }
                    }catch (NullPointerException e){
                        Log.d(TAG,"mSaveCheck : onClick : NullPointerException : "+e.getMessage());
                    }
                }
                else if (!mInputSearch.getText().toString().isEmpty()){
                    if (!isValidPlace(mInputSearch.getText().toString())) {
                        Log.d(TAG,"mSaveCheck : onCLick : User input is not a valid location");
                        Toast.makeText(context,"Sorry, your input in the search bar is not a valid location",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(TAG,"mSaveCheck : onClick : Location : "+mGeoLocateAddress+" Found By Geo Locate Address");
                        if (isRootTask()) {
                            Intent intent = new Intent();
                            intent.putExtra(getString(R.string.user_selected_location), mGeoLocateAddress + "@" + "2.2");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else{
                            ProfileFragment.mSelectedHometownLocation = mGeoLocateAddress;
                            finish();
                        }
                    }
                }
            }
        });
    }

    private boolean isValidPlace(String inputSearch) {
        Log.d(TAG,"isValidPlace : Checking whether the text contains : "+inputSearch+" in the search bar is an actual address or not");

        Geocoder geoCoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();

        try{
            list = geoCoder.getFromLocationName(inputSearch,1);
        }catch (IOException e){
            Log.d(TAG,"isValidPlace : geoLocate : IOException: "+e.getMessage());
        }

        if (list.size() > 0){
            Address address = list.get(0);
            mGeoLocateAddress = address.getAddressLine(0);
            return true;
        }
        else{
            return false;
        }
    }

    private void geoLocate() {
        Log.d(TAG,"geoLocate : Geo locating");

        String mSearchString = mInputSearch.getText().toString();
        Geocoder geoCoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();
        try{
            list = geoCoder.getFromLocationName(mSearchString,1);
        }catch (IOException e){
            Log.d(TAG,"geoLocate : IOException: "+e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG,"geoLocate : Found a location: "+address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting the devices current location");

        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");
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

    private void moveCamera(LatLng latlng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG,"moveCamera : Moving the camera to: latitude: "+latlng.latitude+" , longitude: "+latlng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if (marker != null) marker.remove();

        String placeName = "";
        float placeRating = 0.0f;

        if (placeInfo != null){
            try {
                placeName = placeInfo.getName();
                placeRating = placeInfo.getRating();
                MarkerOptions options = new MarkerOptions()
                        .position(latlng)
                        .title(placeName)
                        .snippet("Rating : "+Float.toString(placeRating))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                marker = mMap.addMarker(options);
            }catch (NullPointerException e){
                Log.d(TAG,"moveCamera : NullPointerException : "+e.getMessage());
            }
        }
        else {
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            marker = mMap.addMarker(options);
        }

        if(isRootTask() && mPlace != null){
            getPhotoMetaData(placeInfo.getId());
        }
    }

    private void getPhotoMetaData(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoResponse
                = geoDataClient.getPlacePhotos(placeId);

        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                photoMetadataList = new ArrayList<>();
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                Log.d(TAG,"number of photos: "+photoMetadataBuffer.getCount());
                if (photoMetadataBuffer.getCount()>0) {
                    for (int i=0; i<photoMetadataBuffer.getCount(); i++) {
                        photoMetadataList.add(photoMetadataBuffer.get(i).freeze());
                    }
                    photoMetadataBuffer.release();
                }
                Log.d(TAG,"size: "+photoMetadataList.size());
                if(photoMetadataList.size()>0){
                    if(NextShareActivity.mSelectedLocationPhotosBitmap.size() > 0)
                        NextShareActivity.mSelectedLocationPhotosBitmap.clear();
                    for(int i=0; i<photoMetadataList.size(); i++){
                        mCurrentPhotoIndex = i;
                        getPhoto(photoMetadataList.get(mCurrentPhotoIndex));
                    }
                }
            }
        });

    }

    private void getPhoto(PlacePhotoMetadata placePhotoMetadata) {

        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(placePhotoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                NextShareActivity.mSelectedLocationPhotosBitmap.add(bitmap);
            }
        });

    }

    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.d(TAG,"moveCamera : Moving the camera to: latitude: "+latlng.latitude+" , longitude: "+latlng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if (marker != null) marker.remove();

        if (!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            marker = mMap.addMarker(options);
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initMap() {
        Log.d(TAG,"initMap : Initializing the map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(LocationSelectFromGooglePlaces.this);
        mapView = mapFragment.getView();
    }

    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission : Getting location permissions");
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
                buildGoogleApiClient();
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
        mLocationPermissionGranted = false;
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
                        buildGoogleApiClient();
                    }
                    else{
                        Log.d(TAG,"onRequestPermissionsResult : Permission Failed");
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady : Map is ready");
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
                    Toast.makeText(context, "Unable to get current location. Please turn on your location.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"mGoogleApiClient : onConnected : Location Found");
                    getDeviceLocation();
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
        initWidgets();
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
        --------------------------- Google Places API Adapter OnItemCLickListener -----------------------
     */

    private AdapterView.OnItemClickListener mAdapterOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(TAG,"Google Places API Adapter: OnItemClickListener : Listened");

            InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(LocationSelectFromGooglePlaces.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

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
                places.release();
                return;
            }
            final Place place = places.get(0);
            try{
                mPlace = new PlaceInfo();

                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());

                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());

                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());

                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());

                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());

                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());

                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                List<Integer> place_types = place.getPlaceTypes();
                int first = place_types.get(0);

                java.lang.reflect.Field[] fields = Place.class.getDeclaredFields();

                for(Field field : fields){
                    Class<?> type = field.getType();
                    if (type == int.class) {
                        try{
                            if (first == field.getInt(null)){
                                mPlace.setPlace_types(field.getName());
                                break;
                            }
                        }catch (IllegalAccessException e){
                            e.printStackTrace();
                        }
                    }
                }

                Log.d(TAG,"onResult: place types: "+mPlace.getPlace_types());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing To Do
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!mInputSearch.getText().toString().isEmpty()){
            mSaveCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_check));
        }
        else{
            mSaveCheck.setImageDrawable(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Nothing To Do
    }

    @Override
    public void onLocationChanged(Location mLocation) {
        location = mLocation;
    }
}