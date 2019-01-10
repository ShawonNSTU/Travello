package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 1/11/2019.
 */

public class NearbyPlacesActivity extends AppCompatActivity {

    private static final String TAG = "NearbyPlacesActivity";
    private Context context = NearbyPlacesActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);
        Log.d(TAG,"onCreate : Started");
    }
}
