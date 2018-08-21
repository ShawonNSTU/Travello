package com.example.shawon.travelbd.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.example.shawon.travelbd.Utils.GridImageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context context = ProfileActivity.this;

    private static final int NUM_GRID_COLUMN = 3;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private TextView mProfileName;
    private CircleImageView mProfileImage;
    private TextView mTvPosts;
    private TextView mTvFollowers;
    private TextView mTvFollowing;
    private TextView mUserName;
    private TextView mHometown;
    private TextView mNumberOfTraveledPlaces;
    private ProgressBar mProgressBar;
    private TextView mTextEditProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "ProfileActivity onCreate: Starting.");

        mProfileName = (TextView) findViewById(R.id.profileName);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mTvPosts = (TextView) findViewById(R.id.tvPosts);
        mTvFollowers = (TextView) findViewById(R.id.tvFollowers);
        mTvFollowing = (TextView) findViewById(R.id.tvFollowing);
        mUserName = (TextView) findViewById(R.id.user_name);
        mHometown = (TextView) findViewById(R.id.hometown);
        mNumberOfTraveledPlaces = (TextView) findViewById(R.id.numberOfTraveledPlaces);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= 21){
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray),android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        mTextEditProfile = (TextView) findViewById(R.id.textEditProfile);

        setupBottomNavigationView();

        setupToolbar();

        setupGridImages();

        isUserLoggedInOrNot();

        onClickEditProfileButton();

    }

    private void onClickEditProfileButton() {

        mTextEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickEditProfileButton:navigating to "+getString(R.string.edit_profile)+" Fragment");

                Intent intent = new Intent(context,SettingsActivity.class);
                intent.putExtra("EditProfileButton","ProfileActivity");
                startActivity(intent);
            }
        });

    }

    // To Test The Grid Image View

    private void setupGridImages() {

        ArrayList<String>imageUrl = new ArrayList<>();

        imageUrl.add("https://78.media.tumblr.com/da2502ad2eacaa20c3db0b65235972ba/tumblr_ns1y4fQoAF1smk4d7o1_500.jpg");
        imageUrl.add("http://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Aronnak_Holiday_Cottage%2C_Rangamati10.jpg/500px-Aronnak_Holiday_Cottage%2C_Rangamati10.jpg");
        imageUrl.add("http://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Ctg_foys_lake_water_2003.jpg/500px-Ctg_foys_lake_water_2003.jpg");
        imageUrl.add("https://homelandtourismbd.files.wordpress.com/2016/07/tumblr_ntu9revosc1smk4d7o1_500.jpg?w=640");
        imageUrl.add("http://farm1.static.flickr.com/668/21011779476_aa76bddffb.jpg");

        GridView gridView = (GridView) findViewById(R.id.gridView);

        int rowWidth = getResources().getDisplayMetrics().widthPixels;

        int imageWidth = rowWidth / NUM_GRID_COLUMN;

        gridView.setColumnWidth(imageWidth);

        GridImageAdapter gridImageAdapter = new GridImageAdapter(context,R.layout.square_grid_image_view,imageUrl,"");

        gridView.setAdapter(gridImageAdapter);

    }

    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);

        setSupportActionBar(toolbar);

        ImageView mProfileMenu = (ImageView) findViewById(R.id.profileMenu);

        ImageView mProfileAddPerson = (ImageView) findViewById(R.id.profileAddPerson);

        mProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Profile Menu onClick : Account Settings");
                startActivity(new Intent(context,SettingsActivity.class));
            }
        });

        mProfileAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Profile Add Person onClick : Follow Person");
            }
        });

    }

    // BottomNavigationView Setup

    private void setupBottomNavigationView() {

        Log.d(TAG, "ProfileActivity setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);

        menuItem.setChecked(true);

    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    // This method is to check whether user logged in or not.

    private void isUserLoggedInOrNot() {

        Log.d(TAG,"Profile Activity : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // retrieve user's all information from the database
                            retrieveUserInformation(dataSnapshot,user.getUid());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    private void retrieveUserInformation(DataSnapshot dataSnapshot, String uid) {

        Log.d(TAG, "retrieveUserInformation:retrieving user info from the database");

        for (DataSnapshot ds : dataSnapshot.getChildren()){

            // User Public Info
            if (ds.getKey().equals(getString(R.string.user_public_Info))){
                Log.d(TAG, "retrieveUserPublicInformation: "+ds);
                try{
                    UserPublicInfo userPublicInfo = new UserPublicInfo();

                    userPublicInfo.setUsername(ds.child(uid).getValue(UserPublicInfo.class).getUsername());

                    userPublicInfo.setHometown(ds.child(uid).getValue(UserPublicInfo.class).getHometown());

                    userPublicInfo.setPosts(ds.child(uid).getValue(UserPublicInfo.class).getPosts());

                    userPublicInfo.setFollowers(ds.child(uid).getValue(UserPublicInfo.class).getFollowers());

                    userPublicInfo.setFollowing(ds.child(uid).getValue(UserPublicInfo.class).getFollowing());

                    userPublicInfo.setProfile_photo(ds.child(uid).getValue(UserPublicInfo.class).getProfile_photo());

                    userPublicInfo.setNumber_of_travelled_places(ds.child(uid).getValue(UserPublicInfo.class).getNumber_of_travelled_places());

                    Log.d(TAG, "retrieveUserPublicInformation:retrieving user public info: "+userPublicInfo.toString());

                    setupTopProfileWidget(userPublicInfo);

                }catch (NullPointerException e){
                    Log.e(TAG, "retrieveUserPublicInformation:NullPointerException: "+e.getMessage());
                }
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupTopProfileWidget(UserPublicInfo userPublicInfo) {

        Log.d(TAG, "setupTopProfileWidget:setup "+userPublicInfo.getUsername()+" public information");

        mProfileName.setText(userPublicInfo.getUsername());

        String mProfilePhoto = userPublicInfo.getProfile_photo();
        if (!mProfilePhoto.isEmpty()){
            Picasso.get().load(userPublicInfo.getProfile_photo()).into(mProfileImage);
        }
        else {
            Picasso.get().load(R.drawable.avatar).into(mProfileImage);
            mProfileImage.setBackgroundColor(Color.TRANSPARENT);
        }

        mTvPosts.setText(String.valueOf(userPublicInfo.getPosts()));

        if (userPublicInfo.getFollowers() > 9){
            String textNumberFollowers = "    "+userPublicInfo.getFollowers();
            mTvFollowers.setText(textNumberFollowers);
        }
        else {
            mTvFollowers.setText(String.valueOf(userPublicInfo.getFollowers()));
        }

        mTvFollowing.setText(String.valueOf(userPublicInfo.getFollowing()));

        mUserName.setText(userPublicInfo.getUsername());

        String hometown = userPublicInfo.getHometown();
        if (!hometown.isEmpty()){
            hometown = "Lives in "+userPublicInfo.getHometown();
            mHometown.setText(hometown);
        }
        else{
            mHometown.setText("You haven't identified your hometown yet!");
            mHometown.setTextColor(getResources().getColor(R.color.gray));
        }

        long numberOfTraveledPlaces = userPublicInfo.getNumber_of_travelled_places();
        if (numberOfTraveledPlaces > 0){
            String textNumberOfTraveledPlaces = "Traveled to "+numberOfTraveledPlaces+" places!";
            mNumberOfTraveledPlaces.setText(textNumberOfTraveledPlaces);
        }
        else{
            mNumberOfTraveledPlaces.setText("You haven't traveled any place yet!");
            mNumberOfTraveledPlaces.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}