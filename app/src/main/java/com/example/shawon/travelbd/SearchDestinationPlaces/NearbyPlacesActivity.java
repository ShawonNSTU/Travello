package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
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
    String placeID = "";
    String latitude = "";
    String longitude = "";
    String placeName = "";

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
                }
            }
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