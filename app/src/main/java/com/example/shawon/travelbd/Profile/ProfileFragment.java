package com.example.shawon.travelbd.Profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.AddPost.LocationSelectFromGooglePlaces;
import com.example.shawon.travelbd.ModelClass.Like;
import com.example.shawon.travelbd.ModelClass.Photo;
import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.example.shawon.travelbd.Utils.GridImageAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 5/29/2019.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;
    private static final int NUM_GRID_COLUMN = 3;
    private static final int ACTIVITY_NUM = 4;
    private static final int ERROR_DIALOG_REQUEST = 8001;

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
    private ImageView mEditHometown;
    private ImageView mSeeTraveledPlaces;
    private ImageView mProfileMenu;
    private ImageView mProfileAddPerson;
    private Toolbar toolbar;
    public static String mSelectedHometownLocation = "";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        mProfileName = (TextView) view.findViewById(R.id.profileName);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        mTvPosts = (TextView) view.findViewById(R.id.tvPosts);
        mTvFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mTvFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mHometown = (TextView) view.findViewById(R.id.hometown);
        mNumberOfTraveledPlaces = (TextView) view.findViewById(R.id.numberOfTraveledPlaces);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        mEditHometown = (ImageView) view.findViewById(R.id.edit_hometown);
        mTextEditProfile = (TextView) view.findViewById(R.id.textEditProfile);
        mSeeTraveledPlaces = (ImageView) view.findViewById(R.id.see_traveled_places);
        mProfileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        mProfileAddPerson = (ImageView) view.findViewById(R.id.profileAddPerson);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        mProgressBar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 21){
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray),android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        setupBottomNavigationView();

        setupToolbar();

        isUserLoggedInOrNot();

        setupGridView();

        onClickEditProfileButton();

        onClickEditHometown();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

    private void onClickEditHometown() {
        Log.d(TAG,"onClickEditHometown : editing user's hometown");
        mEditHometown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServicesOK()) {
                    gotoHometownLocationSelectActivity();
                }
            }
        });
    }

    private void gotoHometownLocationSelectActivity() {
        Intent intent = new Intent(getActivity(), LocationSelectFromGooglePlaces.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isServicesOK() {
        Log.d(TAG,"isServicesOK : checking Google Play Services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map request to edit his/her hometown
            Log.d(TAG,"isServicesOK : Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error has occurred but user can resolve it
            Log.d(TAG,"isServicesOK : an error faced but user can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(getActivity(),"Sorry, you can't make map request to edit your hometown.",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void onClickEditProfileButton() {

        mTextEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickEditProfileButton:navigating to "+getString(R.string.edit_profile)+" Fragment");

                Intent intent = new Intent(getActivity(),SettingsActivity.class);
                intent.putExtra("EditProfileButton","ProfileActivity");
                startActivity(intent);
            }
        });

    }

    // To Test The Grid Image View
    private void setupGridView(){
        Log.d(TAG,"setupGridView : Setting up grid view from user uploaded photos");
        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.uploaded));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    // photos.add(singleSnapshot.getValue(Photo.class));
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                    photo.setUploaded_date(objectMap.get(getString(R.string.field_uploaded_date)).toString());

                    photo.setImage_url(objectMap.get(getString(R.string.field_image_url)).toString());

                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());

                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());

                    photo.setLocation(objectMap.get(getString(R.string.field_location)).toString());

                    photo.setRating(objectMap.get(getString(R.string.field_rating)).toString());

                    photo.setGoogle_places_rating(objectMap.get(getString(R.string.field_google_places_rating)).toString());

                    try{
                        photo.setTagged_people(objectMap.get(getString(R.string.field_tagged_user_id)).toString());
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }

                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                    List<Like> likesList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                int rowWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = rowWidth / NUM_GRID_COLUMN;
                gridView.setColumnWidth(imageWidth);
                ArrayList<String> imageUrls = new ArrayList<String>();
                for(int i=0; i<photos.size(); i++){
                    imageUrls.add(photos.get(i).getImage_url());
                }
                GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(),R.layout.square_grid_image_view,imageUrls,"");
                gridView.setAdapter(gridImageAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // later code...
            }
        });
    }
    private void setupToolbar() {
        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        mProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Profile Menu onClick : Account Settings");
                startActivity(new Intent(getActivity(),SettingsActivity.class));
            }
        });

        mProfileAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Profile Add Person onClick : Follow Person");
                // later code
            }
        });
    }

    // BottomNavigationView Setup

    private void setupBottomNavigationView() {
        Log.d(TAG, "ProfileActivity setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getActivity(), bottomNavigationViewEx);
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
            mHometown.setTextColor(getResources().getColor(R.color.light_black));
        }
        else{
            mHometown.setText("You haven't identified your hometown yet!");
            mHometown.setTextColor(getResources().getColor(R.color.gray));
        }

        mEditHometown.setVisibility(View.VISIBLE);

        long numberOfTraveledPlaces = userPublicInfo.getNumber_of_travelled_places();
        if (numberOfTraveledPlaces > 0){
            String textNumberOfTraveledPlaces = "Traveled to "+numberOfTraveledPlaces+" places!";
            mNumberOfTraveledPlaces.setText(textNumberOfTraveledPlaces);
            mSeeTraveledPlaces.setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();
        if (mSelectedHometownLocation.length() > 0){
            String s = "Lives in "+mSelectedHometownLocation;
            mHometown.setText(s);
            mHometown.setTextColor(getResources().getColor(R.color.light_black));
            myRef.child(getString(R.string.user_public_Info))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.hometown_field))
                    .setValue(mSelectedHometownLocation);
            mSelectedHometownLocation = "";
        }
    }
}