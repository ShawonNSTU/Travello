package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.shawon.travelbd.Adapter.NearbyPlaceAdapter;
import com.example.shawon.travelbd.ModelClass.NearbyPlacesInfo;
import com.example.shawon.travelbd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlacesCardViewActivity extends AppCompatActivity{

    private static final String TAG = "NearbyPlacesCardView";
    private Context context = NearbyPlacesCardViewActivity.this;

    List<NearbyPlacesInfo> nearbyPlacesInfoList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurants);
        Log.d(TAG,"onCreate : Started");

        recyclerView = (RecyclerView) findViewById(R.id.nearby_places_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nearbyPlacesInfoList = new ArrayList<>();
        nearbyPlacesInfoList.add(new NearbyPlacesInfo("Sanur","http://static.asiawebdirect.com/m/bangkok/portals/bali-indonesia-com/homepage/top10/top10-restaurants-sanur/pagePropertiesImage/best-restaurants-sanur.jpg.jpg"));
        nearbyPlacesInfoList.add(new NearbyPlacesInfo("Cafe Adda","http://24heureinfo.com/wp-content/uploads/2018/07/resturant-298185.jpg"));
        nearbyPlacesInfoList.add(new NearbyPlacesInfo("Holly Food","http://hotel-edelweiss73.com/wp-content/uploads/2017/04/slide-9-etoile-des-neiges-restaurant-saint-martin-de-belleville-les-menuires.jpg"));
        nearbyPlacesInfoList.add(new NearbyPlacesInfo("Food Feasta","https://media-cdn.tripadvisor.com/media/photo-s/0e/cc/0a/dc/restaurant-chocolat.jpg"));

        NearbyPlaceAdapter nearbyPlaceAdapter = new NearbyPlaceAdapter(this,nearbyPlacesInfoList);
        recyclerView.setAdapter(nearbyPlaceAdapter);
    }
}
