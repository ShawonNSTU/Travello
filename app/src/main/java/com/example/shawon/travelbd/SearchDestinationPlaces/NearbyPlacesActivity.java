package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 1/11/2019.
 */

public class NearbyPlacesActivity extends AppCompatActivity {

    private static final String TAG = "NearbyPlacesActivity";
    private Context context = NearbyPlacesActivity.this;

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

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        // get bundle from previous activity...
        if (getIntent().hasExtra(getString(R.string.bundle))){
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
            }
        }
    }
}