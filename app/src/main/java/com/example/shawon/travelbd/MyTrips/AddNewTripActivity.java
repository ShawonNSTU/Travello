package com.example.shawon.travelbd.MyTrips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shawon.travelbd.ModelClass.DistrictModel;
import com.example.shawon.travelbd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.SearchResultListener;

/**
 * Created by SHAWON on 6/22/2019.
 */

public class AddNewTripActivity extends AppCompatActivity {

    private ImageView mBackArrow;
    private LinearLayout linearLayout;
    private LottieAnimationView lottieAnimationView;
    private TextView mSelectCityName;
    private ArrayList<CitySearchModel> mSearchCities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_trip_activity);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        mSelectCityName = (TextView) findViewById(R.id.select_city_name);

        loadAllCity();

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSelectCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CitySearchDialogCompat(AddNewTripActivity.this,"Cities",
                        "What are you looking for…?", null, mSearchCities,
                        (SearchResultListener<CitySearchModel>) (dialog, item, position) -> {
                            mSelectCityName.setText(item.getName());
                            dialog.dismiss();
                        }).show();

            }
        });

        //loadAnimation();

    }

    private void loadAllCity() {
        DatabaseReference mCityDatabase = FirebaseDatabase.getInstance().getReference().child("district");
        for(int i=1; i<=56; i++){
            String child = "";
            if(i<10){
                child += '0';
            }
            child += String.valueOf(i);
            mCityDatabase.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DistrictModel districtModel = dataSnapshot.getValue(DistrictModel.class);
                    mSearchCities.add(new CitySearchModel(districtModel.getName(),districtModel.getImage()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadAnimation() {
        linearLayout.setVisibility(View.INVISIBLE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
    }
}