package com.example.shawon.travelbd.SearchDestinationPlaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 6/9/2019.
 */

public class MapPlaceDetailsActivity extends AppCompatActivity {

    private static final String TAG = " MapPlaceDetails";

    private String mSnippet = "";
    private Context mContext;
    private ImageView mBackArrow;

    private String mRating,mLat,mLng,mPlaceID,mPlaceName,mTotalRating,mStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_place_details);

        mContext = MapPlaceDetailsActivity.this;
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        if(getIntent() != null) {
            mSnippet = getIntent().getStringExtra("Snippet");
        }
        if(!mSnippet.isEmpty() && mSnippet != null){
            splitStringFromSnippet(mSnippet);
        }

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
}