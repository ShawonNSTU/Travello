package com.example.shawon.travelbd.AddPost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.IsConnectedToInternet;
import com.example.shawon.travelbd.Utils.ShowCamera;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHAWON on 8/11/2018.
 */

public class PhotoFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "Photo Fragment";

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    private ImageView mCaptureButton;
    private Bitmap bitmap = null;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private boolean mLocationPermissionGranted = false;

    private GoogleApiClient mGoogleApiClient;
    private Location location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated: started.");

        frameLayout = (FrameLayout) getView().findViewById(R.id.frameLayout);
        mCaptureButton = (ImageView) getView().findViewById(R.id.capture_image);

        mCaptureButton.setColorFilter(getActivity().getResources().getColor(R.color.divider));

        if (IsConnectedToInternet.isConnectedToInternet(getActivity())) {
            Log.d(TAG,"IsConnectedToInternet : Success");
            getLocationPermission();
        }

        capturePhoto();

        closeButtonHandler();

        nextButtonHandler();

    }

    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission : Getting location permissions");
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                buildGoogleApiClient();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
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
                        buildGoogleApiClient();
                    }
                    else{
                        Log.d(TAG,"onRequestPermissionsResult : Permission Failed");
                    }
                }
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting the devices current location");

        if (ActivityCompat.checkSelfPermission(getActivity(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        android.location.Address address = getAddress(lat,lng);
        Toast.makeText(getActivity(),""+address.getAddressLine(0),Toast.LENGTH_SHORT).show();
    }

    public android.location.Address getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void nextButtonHandler() {

        TextView nextButton = (TextView) getView().findViewById(R.id.tvNext);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nextButtonHandler : onClicked.");

                if(bitmap != null){
                    Log.d(TAG,"nextButtonHandler : onClicked : Bitmap : "+bitmap);

                    Intent intent = new Intent(getActivity(), NextShareActivity.class);
                    intent.putExtra(getString(R.string.captured_image_bitmap), bitmap);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(),"Sorry, you have not captured any photo!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void closeButtonHandler() {

        ImageView mClose = (ImageView) getView().findViewById(R.id.close);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Closing the Photo Fragment");
                getActivity().finish();
            }
        });

    }

    /**
     * handle the photo that was captured
     */
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Camera:PictureCallback:Handling the picture that was taken");

            // getting bitmap of the captured photo
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

            // reducing the size of bitmap
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1){
                width = 600;
                height = (int) (width/bitmapRatio);
            }
            else {
                height = 600;
                width = (int) (height*bitmapRatio);
            }

            // getting the bitmap with new height and width
            bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
        }
    };

    /**
     * Place the camera by SurfaceView and SurfaceHolder
     */
    private void showCameraSurface() {
        Log.d(TAG,"showCameraSurface:Preparing the surface for showing camera");

        try {
            camera = Camera.open();
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        showCamera = new ShowCamera(getActivity(),camera);
        frameLayout.addView(showCamera);

    }

    /**
     * Handle the capture button to capture photo
     */
    private void capturePhoto() {
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick:capturePhoto");

                if (camera != null && bitmap == null){
                    camera.takePicture(null,null,mPictureCallback);
                }
            }
        });
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
                if(location != null){
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

    }

    @Override
    public void onLocationChanged(Location mLocation) {
        location = mLocation;
    }

    @Override
    public void onResume() {
        super.onResume();
        showCameraSurface();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }
}