package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.NearbyPlaces;
import com.example.shawon.travelbd.ModelClass.Results;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Remote.Common;
import com.example.shawon.travelbd.Remote.IGoogleAPIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlacesMapActivity extends AppCompatActivity{

    private static final String TAG = "NearbyPlacesMapView";
    private Context context = NearbyPlacesMapActivity.this;

    private IGoogleAPIService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places_mapview);
        Log.d(TAG,"onCreate : Started");

        mService = Common.getGoogleAPIService();

        getNearbyPlaces();
    }

    private void getNearbyPlaces() {

        double latitude = 0.0;
        double longitude = 0.0;
        String placeTypes = "";

        // Get lat, lng & placeType from previous activity...
        if (getIntent().hasExtra(getString(R.string.place_types))) {
            String getIntent = getIntent().getStringExtra(getString(R.string.place_types));
            if (getIntent.length() != 0) {
                int line = 0;
                String s = "";
                for (int i = 0; i < getIntent.length(); i++) {
                    if (getIntent.charAt(i) == '|') {
                        ++line;
                        if (line == 1) {
                            latitude = Double.parseDouble(s);
                            s = "";
                        } else if (line == 2) {
                            longitude = Double.parseDouble(s);
                            s = "";
                        }
                    } else {
                        s += getIntent.charAt(i);
                    }
                }
                placeTypes = s;
            }
        }

        // Setting the url...
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+7000);
        googlePlacesUrl.append("&type="+placeTypes);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCl-djuKzjuj3TN6UVJNcob0PlpB4IwIs4");
        String url = googlePlacesUrl.toString();
        Log.d(TAG,"getNearbyPlaces : URL : "+url);

        mService.getNearByPlaces(url)
            .enqueue(new Callback<NearbyPlaces>() {
                @Override
                public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                    if (response.isSuccessful()){
                        for (int i = 0; i<response.body().getResults().length; i++) {
                            Results googlePlaces = response.body().getResults()[i];
                            String placeName = googlePlaces.getName();
                            Toast.makeText(context,""+placeName,Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NearbyPlaces> call, Throwable t) {

                }
            });
    }
}