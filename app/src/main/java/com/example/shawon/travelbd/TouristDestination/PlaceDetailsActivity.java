package com.example.shawon.travelbd.TouristDestination;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.PlaceRating;
import com.example.shawon.travelbd.ModelClass.TouringPlaceItem;
import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by SHAWON on 6/6/2019.
 */

public class PlaceDetailsActivity extends AppCompatActivity implements RatingDialogListener {

    private static final String TAG = "PlaceDetails";
    private Context mContext;
    private TextView mPlaceLocation,mPlaceWebsite,mPlaceDescription,mPlaceHowToGo,mPlaceWhereToStay,mType;
    private ImageView mPlaceImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton mButtonReview;
    private Button mShowReviewButton;
    private RatingBar mRatingBar;
    private CoordinatorLayout mViewLayout;
    private String PlaceID = "";
    private DatabaseReference mDatabasePlace,mDatabasePlaceRating;
    private TouringPlaceItem mTouringPlaceItem;
    private CardView how,where,web_link;
    private String mPlaceNameShow;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        mContext = PlaceDetailsActivity.this;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mDatabasePlace = FirebaseDatabase.getInstance().getReference("Touring Places");
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mButtonReview = (FloatingActionButton) findViewById(R.id.review_btn);
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
                    mDatabasePlaceRating = FirebaseDatabase.getInstance().getReference().child("Place Rating").child(PlaceID);
                    getAllRatingOfThisPlace(PlaceID);
                    progressDialog.dismiss();
            }
        }

        mButtonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(1)
                .setTitle(mPlaceNameShow)
                .setDescription("Please review this place and give your feedback")
                .setTitleTextColor(R.color.example)
                .setDescriptionTextColor(R.color.example)
                .setHint("Describe your experience")
                .setHintTextColor(R.color.white)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.example)
                .setWindowAnimation(R.style.RatingDialogFadeAnimation)
                .create(PlaceDetailsActivity.this)
                .show();
    }

    private void getPlaceDetail(final String placeID) {
        mDatabasePlace.child(placeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mTouringPlaceItem = dataSnapshot.getValue(TouringPlaceItem.class);

                Picasso.get().load(mTouringPlaceItem.getImage()).into(mPlaceImage);

                collapsingToolbarLayout.setTitle(mTouringPlaceItem.getName());

                mPlaceNameShow = mTouringPlaceItem.getName();

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

    private void getAllRatingOfThisPlace(String placeID) {

        Query query = mDatabasePlaceRating.orderByChild("place_id").equalTo(placeID);

        query.addValueEventListener(new ValueEventListener() {

            int count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot getSnapshot : dataSnapshot.getChildren()){

                    PlaceRating placeRating = getSnapshot.getValue(PlaceRating.class);
                    sum+=Integer.parseInt(placeRating.getRating_value());
                    count++;
                }
                if(count != 0) {
                    float average = (float) sum / (float) count;
                    mRatingBar.setRating(average);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPositiveButtonClicked(final int i, @NotNull final String s) {

        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.user_public_Info))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                     uploadToDatabase(i,s,singleSnapshot.getValue(UserPublicInfo.class).getUsername(),
                     singleSnapshot.getValue(UserPublicInfo.class).getProfile_photo());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadToDatabase(int i, String s, String username, String profile_photo) {

        final PlaceRating placeRating = new PlaceRating(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                        PlaceID,
                                                        String.valueOf(i),
                                                        s,
                                                        username,
                                                        profile_photo);
        mDatabasePlaceRating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    mDatabasePlaceRating.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }

                mDatabasePlaceRating.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(placeRating);

                Snackbar.make(mViewLayout,"Thank you for sharing your experience.",Snackbar.LENGTH_SHORT).show();

                getAllRatingOfThisPlace(PlaceID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}