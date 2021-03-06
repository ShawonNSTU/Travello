package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.IsConnectedToInternet;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by SHAWON on 1/11/2019.
 */

public class NearbyPlacesActivity extends AppCompatActivity {

    private static final String TAG = "NearbyPlacesActivity";
    private Context context = NearbyPlacesActivity.this;

    private GeoDataClient geoDataClient;

    // for selected location...
    private String placeID = "";
    private String latitude = "";
    private String longitude = "";
    private String placeName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);
        Log.d(TAG,"onCreate : Started");

        if (IsConnectedToInternet.isConnectedToInternet(context)) {
            geoDataClient = Places.getGeoDataClient(this, null);
        }

        // Collapsing Toolbar Layout...
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        // Set Toolbar for back arrow on it...
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        // back arrow onClick
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Back Arrow : onClick");
                finish();
            }
        });

        // get bundle from previous activity...
        if (getIntent().hasExtra(getString(R.string.bundle))){
            Log.d(TAG,"Getting Bundle from previous activities AdapterOnItemClick.");

            String getIntent = getIntent().getStringExtra(getString(R.string.bundle));
            if(getIntent.length() != 0){
                int line = 0;
                String s = "";
                for(int i=0; i<getIntent.length(); i++){
                    if (getIntent.charAt(i) == '|'){
                        ++line;
                        if (line == 1){
                            placeID = s;
                            s = "";
                        }else if (line == 2){
                            placeName = s;
                            s = "";
                        }else if (line == 3){
                            latitude = s;
                            s = "";
                        }
                    }else {
                        s+=getIntent.charAt(i);
                    }
                }
                longitude = s;
                collapsingToolbarLayout.setTitle(placeName);
                if (IsConnectedToInternet.isConnectedToInternet(context)){
                    getPhotoMetaData(placeID);
                    nearbyRestaurantsOnClick();
                    nearbyCoffeeShopsOnClick();
                    nearbyParksOnClick();
                    nearbyAttractionsOnClick();
                    nearbyMovieTheatersOnClick();
                    nearbyShoppingMallOnClick();
                    nearbyHotelsOnClick();
                    nearbyATMsOnClick();
                    nearbyHospitalsOnClick();
                    nearbyBusStationsOnClick();
                    nearbyTravelAgenciesOnClick();
                }
            }
        }
    }

    /*
    Nearby Travel Agencies
     */
    private void nearbyTravelAgenciesOnClick(){
        CardView cardView = (CardView) findViewById(R.id.nearby_travel_agency);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyTravelAgencies : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "travel_agency";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });
    }

    /*
    Nearby Bus Stations
     */
    private void nearbyBusStationsOnClick(){
        CardView cardView = (CardView) findViewById(R.id.nearby_bus_stations);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyBusStations : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "bus_station";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });
    }

    /*
    Nearby Hospitals
     */
    private void nearbyHospitalsOnClick(){
        CardView cardView = (CardView) findViewById(R.id.nearby_hospitals);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyHospitals : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "hospital";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });
    }

    /*
    Nearby ATMs
     */
    private void nearbyATMsOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_atms);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyATMs : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "atm";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Hotels
     */
    private void nearbyHotelsOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_hotels);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyHotels : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "lodging";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Shopping Malls
     */
    private void nearbyShoppingMallOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_shopping_mall);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyShoppingMall : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "shopping_mall";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Movie Theaters
     */
    private void nearbyMovieTheatersOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_movie_theater);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyMovieTheaters : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "movie_theater";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Attractions
     */
    private void nearbyAttractionsOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_attractions);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyAttractions : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "natural_feature";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Parks
     */
    private void nearbyParksOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_parks);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyParks : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "park";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Coffee Shops
     */
    private void nearbyCoffeeShopsOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_coffee_shop);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyCoffeeShops : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "cafe";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

    }

    /*
    Nearby Restaurants
     */
    private void nearbyRestaurantsOnClick() {
        CardView cardView = (CardView) findViewById(R.id.nearby_restaurants);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"nearbyRestaurants : OnClicked");

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                String type = "restaurant";
                String S = lat+"|"+lng+"|"+type;
                Intent intent = new Intent(context,NearbyPlacesMapActivity.class);
                intent.putExtra(getString(R.string.place_types),S);
                startActivity(intent);
            }
        });

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
                Bitmap bitmap = photo.getBitmap();
                ImageView mSelectedPlaceImage = (ImageView) findViewById(R.id.selected_place_image);
                mSelectedPlaceImage.setImageBitmap(bitmap);
            }
        });

    }
}