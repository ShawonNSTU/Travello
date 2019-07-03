package com.example.shawon.travelbd.MyTrips;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shawon.travelbd.ModelClass.DistrictModel;
import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ir.mirrajabi.searchdialog.core.SearchResultListener;

/**
 * Created by SHAWON on 6/22/2019.
 */

public class AddNewTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ImageView mBackArrow;
    private LinearLayout linearLayout;
    private LottieAnimationView lottieAnimationView;
    private TextView mSelectCityName;
    private TextView tripStartDate;
    private ArrayList<CitySearchModel> mSearchCities = new ArrayList<>();
    private DatePickerDialog mDatePickerDialog;
    private Button addTrip;
    private Trip trip = new Trip();
    private EditText mTripName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_trip_activity);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        mSelectCityName = (TextView) findViewById(R.id.select_city_name);
        tripStartDate = (TextView) findViewById(R.id.sdate);
        addTrip = (Button) findViewById(R.id.btn_add_trip);
        mTripName = (EditText) findViewById(R.id.tname);

        final Calendar calendar = Calendar.getInstance();

        mDatePickerDialog = new DatePickerDialog(
                AddNewTripActivity.this,
                AddNewTripActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        addTripOnClick();

        tripStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

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
                            trip.setCity_name(item.getName());
                            trip.setCity_image(item.getImage());
                            dialog.dismiss();
                        }).show();

            }
        });

        //loadAnimation();

    }

    private void addTripOnClick() {
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripName = mTripName.getText().toString();
                trip.setTrip_name(tripName);
                addToDatabase();
            }
        });
    }

    private void addToDatabase() {
        DatabaseReference mTripDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Upcoming Trips")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String key = mTripDatabase.push().getKey();
        mTripDatabase.child(key).setValue(trip);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activityAddNewTrip);
        Snackbar.make(relativeLayout,"Trip added!",Snackbar.LENGTH_LONG).show();
        finish();
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        Log.d("Month", String.valueOf(month));
        tripStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        String mStartDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        trip.setTrip_start_date(mStartDate);
    }
}