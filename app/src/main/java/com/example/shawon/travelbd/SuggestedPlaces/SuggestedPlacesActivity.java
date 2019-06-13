package com.example.shawon.travelbd.SuggestedPlaces;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.SearchDestinationPlaces.DirectionActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by SHAWON on 6/12/2019.
 */

public class SuggestedPlacesActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "SuggestedPlaces";
    private Context context = SuggestedPlacesActivity.this;

    private GoogleMap mMap;
    private View mapView;
    private GoogleApiClient googleApiClient;

    private GeoDataClient geoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private PlacePhotoMetadata mSinglePhotoMetadata;

    private Context mContext;
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
    private String totalRating = "", website =  "", location = "", phone_number = "";
    private String mCurrentPlace;
    private double mLat,mLng;
    private String opening_hours = "";
    private ArrayList<Review> ReviewsList;
    private ListView listView;
    private TextView mReviews;
    private ImageView favourite_place, location_icon, phone_icon, clock_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_places);

        geoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ReviewsList = new ArrayList<>();
        mReviews = (TextView) findViewById(R.id.Reviews);
        favourite_place = (ImageView) findViewById(R.id.favourite_place);
        listView = (ListView) findViewById(R.id.review_list);
        mContext = SuggestedPlacesActivity.this;
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
        phone_icon = (ImageView) findViewById(R.id.phone_icon);
        location_icon = (ImageView) findViewById(R.id.location_icon);
        clock_icon = (ImageView) findViewById(R.id.clock_icon);

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(SuggestedPlacesActivity.this);
            startActivityForResult(i,1);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
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

    private void initMap() {
        Log.d(TAG,"initMap : Initializing the map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(SuggestedPlacesActivity.this);
        mapView = mapFragment.getView();
    }

    private void moveMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
            .position(latLng)
            .title(mCurrentPlace)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(21.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
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

            if (jsonObj.has("user_ratings_total")) {
                totalRating = jsonObj.getString("user_ratings_total");
            }

            if (jsonObj.has("opening_hours")) {
                JSONObject j = (JSONObject) jsonObj.get("opening_hours");
                opening_hours = j.getString("open_now");
            }

            JSONArray array = jsonObj.getJSONArray("reviews");

            for (int i=0; i<array.length(); i++){

                JSONObject js = (JSONObject) array.get(i);

                Review review = new Review();

                review.setAuthor_name(js.getString("author_name"));
                review.setProfile_photo_url(js.getString("profile_photo_url"));
                review.setRating(js.getString("rating"));
                review.setRelative_time_description(js.getString("relative_time_description"));
                review.setText(js.getString("text"));

                Log.d(TAG,review.toString());

                ReviewsList.add(review);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error processing JSON results", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            final Place place = PlacePicker.getPlace(SuggestedPlacesActivity.this,data);

            String PlaceID = place.getId();
            getPhotoMetaData(PlaceID);

            mLat = place.getLatLng().latitude;
            mLng = place.getLatLng().longitude;

            moveMap(mLat,mLng);

            details(PlaceID);

            if (ReviewsList.size() != 0){
                listView.setAdapter(new ReviewListAdapter(this,ReviewsList));
                Utility.setListViewHeightBasedOnChildren(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //
                    }
                });
            }
            else{
                mReviews.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }

            favourite_place.setVisibility(View.VISIBLE);
            direction.setVisibility(View.VISIBLE);
            text_direction.setVisibility(View.VISIBLE);
            call.setVisibility(View.VISIBLE);
            text_call.setVisibility(View.VISIBLE);
            mWebsite.setVisibility(View.VISIBLE);
            text_website.setVisibility(View.VISIBLE);
            location_icon.setVisibility(View.VISIBLE);
            phone_icon.setVisibility(View.VISIBLE);
            clock_icon.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);

            String rating_text = "";
            rating_text = String.valueOf(place.getRating());
            if(rating_text.equals("")){
                rating_text = "2.5";
            }
            rating.setText(rating_text);

            ratingBar.setRating(Float.parseFloat(rating_text));

            final String placeName = place.getName().toString();
            place_name.setText(placeName);
            mCurrentPlace = placeName;

            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DirectionActivity.class);
                    intent.putExtra("Location",String.valueOf(mLat)+" "+String.valueOf(mLng)+" "+mCurrentPlace);
                    startActivity(intent);
                }
            });

            text_direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DirectionActivity.class);
                    intent.putExtra("Location",String.valueOf(mLat)+" "+String.valueOf(mLng)+" "+mCurrentPlace);
                    startActivity(intent);
                }
            });


            phone_number = place.getPhoneNumber().toString();

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

            if(phone_number.equals("")){
                mPhoneNumber.setVisibility(View.GONE);
                ImageView phone_icon = (ImageView) findViewById(R.id.phone_icon);
                phone_icon.setVisibility(View.GONE);
            }
            else mPhoneNumber.setText(phone_number);

            location = place.getAddress().toString();

            if(location.equals("")){
                mLocation.setVisibility(View.GONE);
                ImageView location_icon = (ImageView) findViewById(R.id.location_icon);
                location_icon.setVisibility(View.GONE);
            }
            else mLocation.setText(location);

            if (place.getWebsiteUri() != null){
                website = place.getWebsiteUri().toString();
            }

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

            if(totalRating.equals("")){
                totalRating = "2";
            }
            total_rating.setText(totalRating+" reviews");

            if (opening_hours.equals("true")){
                mPlaceStatus.setText("Open now");
                mPlaceStatus.setTextColor(getResources().getColor(R.color.color_Accent));
            }
            else{
                mPlaceStatus.setText("Close now");
                mPlaceStatus.setTextColor(getResources().getColor(R.color.red));
            }
        }
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
                place_image.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        moveMap(mLat,mLng);
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
                intent.putExtra("Location",String.valueOf(mLat)+" "+String.valueOf(mLng)+" "+mCurrentPlace);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}