package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.Favourites;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.FilePath;
import com.example.shawon.travelbd.Utils.ImageManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SHAWON on 6/9/2019.
 */

public class MapPlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = " MapPlaceDetails";
    private GoogleMap mMap;
    private View mapView;
    private GoogleApiClient googleApiClient;
    private String mSnippet = "";
    private Context mContext;
    private GeoDataClient geoDataClient;
    private String mRating,mLat,mLng,mPlaceID,mPlaceName,mTotalRating,mStatus,mPrevLat,mPrevLng;

    private ImageView mBackArrow;
    private ImageView place_image;
    private TextView place_name;
    private TextView rating;
    private RatingBar ratingBar;
    private TextView total_rating;
    private ImageView direction;
    private TextView text_direction;
    private TextView text_call;
    private ImageView call;
    private TextView text_website;
    private ImageView mWebsite;
    private TextView mLocation,mPlaceStatus,mPhoneNumber;
    private ImageView mFavourite;
    private String website = "", location = "", phone_number = "";
    private DatabaseReference favouritesDatabase;
    private Bitmap bitmap;
    private byte[] bytes;
    private long imageCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_place_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mContext = MapPlaceDetailsActivity.this;
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        place_image = (ImageView) findViewById(R.id.place_image);
        place_name = (TextView) findViewById(R.id.place_name);
        rating = (TextView) findViewById(R.id.rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        total_rating = (TextView) findViewById(R.id.countReviews);
        direction = (ImageView) findViewById(R.id.direction);
        text_direction = (TextView) findViewById(R.id.text_direction);
        call = (ImageView) findViewById(R.id.call);
        text_call = (TextView) findViewById(R.id.text_call);
        mWebsite = (ImageView) findViewById(R.id.website);
        text_website = (TextView) findViewById(R.id.text_website);
        mLocation = (TextView) findViewById(R.id.location_text);
        mPlaceStatus = (TextView) findViewById(R.id.place_status);
        mPhoneNumber = (TextView) findViewById(R.id.phone_number);
        mFavourite = (ImageView) findViewById(R.id.favourite_place);

        favouritesDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Favourites")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(getIntent() != null) {
            mSnippet = getIntent().getStringExtra("Snippet");
            Log.d(TAG,mSnippet);
        }
        if(!mSnippet.isEmpty() && mSnippet != null){
            splitStringFromSnippet(mSnippet);
            geoDataClient = Places.getGeoDataClient(this, null);
            details(mPlaceID);
            setUpWidgets();
            favouriteOnClick();
        }

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMap();

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    private void favouriteOnClick() {
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"favouriteOnClick");

                favouritesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imageCount = dataSnapshot.getChildrenCount();
                        ++imageCount;
                        if(dataSnapshot.hasChild(mPlaceID)) {
                            mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
                            favouritesDatabase.child(mPlaceID).removeValue();
                        }
                        else{
                            final ProgressDialog pd = new ProgressDialog(MapPlaceDetailsActivity.this);
                            pd.setMessage("Loading...");
                            pd.show();
                            bytes = ImageManager.getBytesFromBitmap(bitmap,100);
                            StorageReference  mStorageReference = FirebaseStorage.getInstance().getReference();
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FilePath filePath = new FilePath();
                            StorageReference storageReference = mStorageReference
                                    .child(filePath.FIREBASE_IMAGE_STORAGE_PATH_OF_USERS + "/" + userID + "/favourites" + "/photo" + imageCount);

                            UploadTask uploadTask = null;
                            uploadTask = storageReference.putBytes(bytes);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d(TAG,"uploadPhoto : onSuccess");

                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    uploadToDatabase(downloadUrl.toString());
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"uploadPhoto : onFailure");

                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void uploadToDatabase(String url) {
        mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_red));
        Favourites favourites = new Favourites();
        favourites.setPlace_id(mPlaceID);
        favourites.setPlace_name(mPlaceName);
        favourites.setLatitude(mLat);
        favourites.setLongitude(mLng);
        favourites.setPlace_rating(mRating);
        favourites.setTotal_rating(mTotalRating);
        favourites.setStatus(mStatus);
        favourites.setPrevious_place_latitude(mPrevLat);
        favourites.setPrevious_place_longitude(mPrevLng);
        favourites.setPlace_image(url);
        Log.d(TAG,favourites.toString());
        favouritesDatabase.child(mPlaceID).setValue(favourites);
    }

    private void details(String reference) {
        String API_KEY = "AIzaSyCl-djuKzjuj3TN6UVJNcob0PlpB4IwIs4";
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place");
            sb.append("/details");
            sb.append("/json");
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

            location = jsonObj.getString("formatted_address");
            if (jsonObj.has("website")) {
                website = jsonObj.getString("website");
            }
            if (jsonObj.has("formatted_phone_number")) {
                phone_number = jsonObj.getString("formatted_phone_number");
            }
            else if (jsonObj.has("international_phone_number")) {
                phone_number = jsonObj.getString("international_phone_number");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error processing JSON results", e);
        }
    }

    private void initMap() {
        Log.d(TAG,"initMap : Initializing the map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(MapPlaceDetailsActivity.this);
        mapView = mapFragment.getView();
    }

    private void moveMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mPlaceName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(21.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setUpWidgets() {
        getPhotoMetaData(mPlaceID);
        place_name.setText(mPlaceName);
        rating.setText(mRating);
        ratingBar.setRating(Float.parseFloat(mRating));
        if(Float.parseFloat(mRating) == 2.5){
            mTotalRating = "2";
        }
        total_rating.setText(mTotalRating+" reviews");
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,DirectionActivity.class);
                intent.putExtra("Location",mLat+" "+mLng+" "+mPlaceName);
                startActivity(intent);
            }
        });
        text_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,DirectionActivity.class);
                intent.putExtra("Location",mLat+" "+mLng+" "+mPlaceName);
                startActivity(intent);
            }
        });
        text_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_number.equals("")){
                    String msg = "Sorry, there is no valid phone number.";
                    buildAlertDialog(msg);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
                    try {
                        startActivity(intent);
                    }catch (SecurityException e){
                        e.getMessage();
                    }
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_number.equals("")){
                    String msg = "Sorry, there is no valid phone number.";
                    buildAlertDialog(msg);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
                    try {
                        startActivity(intent);
                    }catch (SecurityException e){
                        e.getMessage();
                    }
                }
            }
        });
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (website.equals("")){
                    String msg = "Sorry, there is no valid website.";
                    buildAlertDialog(msg);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(website));
                    startActivity(intent);
                }
            }
        });
        text_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (website.equals("")){
                    String msg = "Sorry, there is no valid website.";
                    buildAlertDialog(msg);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(website));
                    startActivity(intent);
                }
            }
        });

        if(phone_number.equals("")){
            mPhoneNumber.setVisibility(View.GONE);
            ImageView phone_icon = (ImageView) findViewById(R.id.phone_icon);
            phone_icon.setVisibility(View.GONE);
        }
        else mPhoneNumber.setText(phone_number);

        if (mStatus.equals("true")){
            mPlaceStatus.setText("Open now");
            mPlaceStatus.setTextColor(getResources().getColor(R.color.color_Accent));
        }
        else{
            mPlaceStatus.setText("Close now");
            mPlaceStatus.setTextColor(getResources().getColor(R.color.red));
        }

        if(location.equals("")){
            mLocation.setVisibility(View.GONE);
            ImageView location_icon = (ImageView) findViewById(R.id.location_icon);
            location_icon.setVisibility(View.GONE);
        }
        else mLocation.setText(location);

        favouritesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mPlaceID)) {
                    mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_red));
                }
                else{
                    mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void buildAlertDialog(String msg) {
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setMessage(msg);
        b.setCancelable(true);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog a = b.create();
        a.show();
    }

    private void splitStringFromSnippet(String mSnippet) {
        char[] arr = mSnippet.toCharArray();
        int cnt = 1;
        String splitString = "";
        for(int i=0; i<mSnippet.length(); i++){
            splitString += arr[i];
            if (arr[i] == '\n'){
                if(cnt == 1){
                    mRating = getStringExtra(splitString,"Rating : ");
                }
                else if(cnt == 2){
                    mLat = getStringExtra(splitString,"Latitude : ");
                }
                else if(cnt == 3){
                    mLng = getStringExtra(splitString,"Longitude : ");
                }
                else if(cnt == 4){
                    mPlaceID = getStringExtra(splitString,"Place ID : ");
                }
                else if(cnt == 5){
                    mPlaceName = getStringExtra(splitString,"Place Name : ");
                }
                else if(cnt == 6){
                    mTotalRating = getStringExtra(splitString,"Total Rating : ");
                }
                else if(cnt == 7){
                    mStatus = getStringExtra(splitString,"Status : ");
                }
                else if(cnt == 8){
                    mPrevLat = getStringExtra(splitString,"Lat : ");
                }
                else if(cnt == 9){
                    mPrevLng = getStringExtra(splitString,"Lng : ");
                }
                splitString = "";
                ++cnt;
            }
        }
    }

    private String getStringExtra(String splitString, String s) {
        String string = "";
        char[] arr = splitString.toCharArray();
        for(int i=0; i<splitString.length(); i++){
            string+=arr[i];
            if(string.equals(s)){
                string = "";
                for(int j=i+1; j<splitString.length()-1; j++){
                    string += arr[j];
                }
                break;
            }
        }
        if(s.equals("Rating : ")){
            String ss = "";
            char[] ara = string.toCharArray();
            for(int i=0; i<string.length(); i++){
                if(ara[i] == '/') break;
                ss += ara[i];
            }
            string = ss;
        }
        return string;
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
                if (photoMetadataBuffer.getCount() > 0) {
                    // get a single photo meta data from photoMetadataBuffer...
                    PlacePhotoMetadata mSinglePhotoMetadata = photoMetadataBuffer.get(0).freeze();
                    getPhoto(mSinglePhotoMetadata);
                    photoMetadataBuffer.release();
                }
            }
        });
    }

    private void getPhoto(PlacePhotoMetadata mSinglePhotoMetadata) {
        Log.d(TAG,"getPhoto : Getting a bitmap photo from SinglePhotoMetaData.");

        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(mSinglePhotoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                bitmap = photo.getBitmap();
                place_image.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        moveMap(Double.parseDouble(mLat),Double.parseDouble(mLng));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(mContext,DirectionActivity.class);
                intent.putExtra("Location",mLat+" "+mLng+" "+mPlaceName);
                startActivity(intent);
            }
        });
    }
}