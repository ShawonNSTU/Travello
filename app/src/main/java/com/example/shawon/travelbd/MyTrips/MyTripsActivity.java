package com.example.shawon.travelbd.MyTrips;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAWON on 7/4/2019.
 */

public class MyTripsActivity extends AppCompatActivity {

    private FloatingActionButton button;
    private ViewPager viewPager;
    private Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        button = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPadding(130, 0, 130, 0);

        onClickFab();

        loadAllTrips();
    }

    private void loadAllTrips() {
        List<Trip> myTrips = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Upcoming Trips")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Trip trip = snapshot.getValue(Trip.class);
                            myTrips.add(trip);
                        }

                        adapter = new Adapter(myTrips,MyTripsActivity.this);

                        viewPager.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void onClickFab() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTripsActivity.this,AddNewTripActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllTrips();
    }
}