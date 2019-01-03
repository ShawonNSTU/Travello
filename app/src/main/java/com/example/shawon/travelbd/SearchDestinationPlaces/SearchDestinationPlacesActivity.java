package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 1/3/2019.
 */

public class SearchDestinationPlacesActivity extends AppCompatActivity {

    private static final String TAG = "SearchDestinationPlaces";
    private Context context = SearchDestinationPlacesActivity.this;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination_places);
        Log.d(TAG,"onCreate: Starting.");

        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);
    }
}
