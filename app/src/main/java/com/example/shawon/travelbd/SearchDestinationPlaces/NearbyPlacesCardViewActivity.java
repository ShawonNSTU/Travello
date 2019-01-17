package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.shawon.travelbd.ModelClass.NearbyPlacesInfo;
import com.example.shawon.travelbd.R;

import java.util.List;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlacesCardViewActivity extends AppCompatActivity{

    private static final String TAG = "NearbyPlacesCardView";
    private Context context = NearbyPlacesCardViewActivity.this;

    List<NearbyPlacesInfo> nearbyPlacesInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurants);
        Log.d(TAG,"onCreate : Started");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.nearby_places_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*nearbyPlacesInfoList = new ArrayList<>();
        NearbyPlaceAdapter nearbyPlaceAdapter = new NearbyPlaceAdapter(this,nearbyPlacesInfoList);
        recyclerView.setAdapter(nearbyPlaceAdapter);*/
    }
}
