package com.example.shawon.travelbd.TouristDestination;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.shawon.travelbd.ModelClass.PlaceRating;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.ViewHolder.ShowReviewViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by SHAWON on 6/8/2019.
 */

public class ShowReviewActivity extends AppCompatActivity {

    private static final String TAG = "ShowReview";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabasePlaceRating;
    private FirebaseRecyclerAdapter<PlaceRating,ShowReviewViewHolder> adapter;
    private String PlaceID = "";
    private ProgressDialog mProgress;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        if (getIntent() != null) {
            PlaceID = getIntent().getStringExtra("PlaceID");
        }

        mProgress = new ProgressDialog(this);
        mDatabasePlaceRating = FirebaseDatabase.getInstance().getReference().child("Place Rating").child(PlaceID);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgress.setMessage("Loading...");
        mProgress.show();
        loadReviews();
        mProgress.dismiss();
    }

    private String getTimestampDifference(String posted_date){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd't'HH:mm:ss'z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = posted_date;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }

        if(!difference.equals("0")){
            difference += " DAYS AGO";
        }else{
            difference = "TODAY";
        }
        return difference;
    }

    private void loadReviews() {
        adapter = new FirebaseRecyclerAdapter<PlaceRating, ShowReviewViewHolder>(
                PlaceRating.class,
                R.layout.layout_view_review,
                ShowReviewViewHolder.class,
                mDatabasePlaceRating
        ) {
            @Override
            protected void populateViewHolder(ShowReviewViewHolder viewHolder, PlaceRating model, int position) {
                viewHolder.mRatingBar.setRating(Float.parseFloat(model.getRating_value()));

                viewHolder.mUserName.setText(model.getName());

                viewHolder.mUserReview.setText(model.getReview());

                viewHolder.mTimestamp.setText(getTimestampDifference(model.getPosted_date()));

                Picasso.get().load(model.getProfile_picture()).into(viewHolder.mUserProfileImage);
            }
        };
        recyclerView.setAdapter(adapter);
    }
}