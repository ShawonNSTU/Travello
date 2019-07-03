package com.example.shawon.travelbd.FavouritePlace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shawon.travelbd.ModelClass.Favourites;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.SearchDestinationPlaces.MapPlaceDetailsActivity;
import com.example.shawon.travelbd.Utils.RecyclerViewItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 6/21/2019.
 */

public class FavouritePlaceActivity extends AppCompatActivity {

    private static final String TAG = "FavouritePlace";

    private DatabaseReference mDatabaseFavourite, mDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Favourites, FavouritePlaceItemViewHolder> adapter;
    private ImageView mBackArrow;
    private RelativeLayout relativeLayout;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoutite_place);

        mDatabaseFavourite = FirebaseDatabase.getInstance().getReference()
                .child("Favourites")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout2);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_favourites);
        recyclerView.setHasFixedSize(true);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    loadFavoritePlaces();
                }
                else{
                    animationView.setVisibility(View.GONE);
                    Snackbar.make(relativeLayout,"You have not added any place to favourites!",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }

    private void loadFavoritePlaces() {
        adapter = new FirebaseRecyclerAdapter<Favourites, FavouritePlaceItemViewHolder>(
                Favourites.class,
                R.layout.favourite_place_item,
                FavouritePlaceItemViewHolder.class,
                mDatabaseFavourite
        ) {
            @Override
            protected void populateViewHolder(FavouritePlaceItemViewHolder viewHolder, final Favourites model, int position) {

                viewHolder.placeName.setText(model.getPlace_name());
                Picasso.get().load(model.getPlace_image()).into(viewHolder.placeImage);

                viewHolder.setItemClickListener(new RecyclerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        String placeRating = adapter.getItem(position).getPlace_rating();
                        String place_id = adapter.getItem(position).getPlace_id();
                        String latitude = adapter.getItem(position).getLatitude();
                        String longitude = adapter.getItem(position).getLongitude();
                        String place_name = adapter.getItem(position).getPlace_name();
                        String total_rating = adapter.getItem(position).getTotal_rating();
                        String place_status = adapter.getItem(position).getStatus();
                        String prevLat = adapter.getItem(position).getPrevious_place_latitude();
                        String prevLng = adapter.getItem(position).getPrevious_place_longitude();
                        String Snippet = "Rating : "+placeRating+"/5"+"\n"+
                                "Latitude : "+latitude+"\n"+
                                "Longitude : "+longitude+"\n"+
                                "Place ID : "+place_id+"\n"+
                                "Place Name : "+place_name+"\n"+
                                "Total Rating : "+total_rating+"\n"+
                                "Status : "+place_status+"\n"+
                                "Lat : "+prevLat+"\n"+
                                "Lng : "+prevLng+"\n";
                        Intent intent = new Intent(FavouritePlaceActivity.this, MapPlaceDetailsActivity.class);
                        intent.putExtra("Snippet", Snippet);
                        startActivity(intent);
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
        animationView.setVisibility(View.GONE);
    }
}