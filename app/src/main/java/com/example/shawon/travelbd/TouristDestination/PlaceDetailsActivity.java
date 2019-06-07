package com.example.shawon.travelbd.TouristDestination;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.TouringPlaceItem;
import com.example.shawon.travelbd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 6/6/2019.
 */

public class PlaceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PlaceDetails";

    private TextView mPlaceLocation,mPlaceWebsite,mPlaceDescription,mPlaceHowToGo,mPlaceWhereToStay,mType;
    private ImageView mPlaceImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton mButtonReview;
    private Button mShowReviewButton;
    private RatingBar mRatingBar;
    private CoordinatorLayout mViewLayout;
    private String PlaceID = "";
    private DatabaseReference mDatabasePlace;
    private TouringPlaceItem mTouringPlaceItem;
    private CardView how,where,web_link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        mDatabasePlace = FirebaseDatabase.getInstance().getReference("Touring Places");
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mButtonReview = (FloatingActionButton) findViewById(R.id.review_btn);

//        Drawable drawable = mRatingBar.getProgressDrawable();
//        drawable.setColorFilter(Color.parseColor("#39796b"), PorterDuff.Mode.SRC_ATOP);

        mPlaceLocation = (TextView) findViewById(R.id.location_text);
        mPlaceWebsite = (TextView) findViewById(R.id.link_text);
        mPlaceDescription = (TextView) findViewById(R.id.description);
        mPlaceHowToGo = (TextView) findViewById(R.id.how_togo);
        mPlaceWhereToStay = (TextView) findViewById(R.id.where_tostay);
        mPlaceImage = (ImageView) findViewById(R.id.place_image);
        mShowReviewButton = (Button) findViewById(R.id.btn_show_review);
        mType = (TextView) findViewById(R.id.type);
        how = (CardView) findViewById(R.id.how);
        where = (CardView) findViewById(R.id.where);
        web_link = (CardView) findViewById(R.id.web_link);
        mViewLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar1);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar1);

        // Set Toolbar for back arrow on it...
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // back arrow onClick
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent() != null){
            PlaceID = getIntent().getStringExtra("PlaceID");
            if(!PlaceID.isEmpty() && PlaceID != null){
                    getPlaceDetail(PlaceID);
            }
        }
    }

    private void getPlaceDetail(final String placeID) {
        mDatabasePlace.child(placeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mTouringPlaceItem = dataSnapshot.getValue(TouringPlaceItem.class);

                Picasso.get().load(mTouringPlaceItem.getImage()).into(mPlaceImage);
                collapsingToolbarLayout.setTitle(mTouringPlaceItem.getName());
                mPlaceLocation.setText(mTouringPlaceItem.getLocation());
                if(mTouringPlaceItem.getWikipedia().equals("")) {
                    web_link.setVisibility(View.GONE);
                }
                else mPlaceWebsite.setText(mTouringPlaceItem.getWikipedia());
                if(mTouringPlaceItem.getHow_to_go().equals("")) {
                    how.setVisibility(View.GONE);
                }
                else mPlaceHowToGo.setText(mTouringPlaceItem.getHow_to_go());
                if(mTouringPlaceItem.getWhere_to_stay().equals("")) {
                    where.setVisibility(View.GONE);
                }
                else mPlaceWhereToStay.setText(mTouringPlaceItem.getWhere_to_stay());
                if(mTouringPlaceItem.getType().equals("")) {
                    mType.setVisibility(View.GONE);
                }
                else{
                    String hash_tag = "#";
                    hash_tag += mTouringPlaceItem.getType();
                    char[] arr = hash_tag.toCharArray();
                    for(int i=0; i<hash_tag.length(); i++){
                        if(arr[i] == ' '){
                            arr[i] = '_';
                        }
                    }
                    hash_tag = String.valueOf(arr);
                    mType.setText(hash_tag);
                }
                mPlaceDescription.setText(mTouringPlaceItem.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}