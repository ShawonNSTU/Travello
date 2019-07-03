package com.example.shawon.travelbd.SuggestForFollow;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by SHAWON on 6/18/2019.
 */

public class SuggestForFollowActivity extends AppCompatActivity{

    private static final String TAG = "SuggestForFollow";
    private double latitude, longitude;
    private String mCurrentUserID;
    private Context mContext;
    private DatabaseReference reference;
    private ArrayList<String> mCurrentUserFollowingList;
    private ArrayList<UserPublicInfo> mNearbyUserList;
    private ArrayList<UserPublicInfo> mNearbyUserListFinal;
    private ArrayList<Integer> mMutualFriendList;
    private ListView listView;
    private ImageView mBackArrow;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_for_follow);

        mContext = SuggestForFollowActivity.this;
        reference = FirebaseDatabase.getInstance().getReference();
        mCurrentUserFollowingList = new ArrayList<>();
        mNearbyUserList = new ArrayList<>();
        mNearbyUserListFinal = new ArrayList<>();
        mMutualFriendList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getLatLng();

    }

    private void getLatLng() {
        Query query = reference
                        .child(getString(R.string.user_public_Info))
                        .orderByChild(getString(R.string.field_user_id))
                        .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    latitude = Double.valueOf(ds.getValue(UserPublicInfo.class).getLatitude());
                    longitude = Double.valueOf(ds.getValue(UserPublicInfo.class).getLongitude());
                    mCurrentUserID = ds.getValue(UserPublicInfo.class).getUser_id();
                }
                getCurrentUserFollowingList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCurrentUserFollowingList() {
        Query query = reference
                        .child(getString(R.string.dbname_following))
                        .child(mCurrentUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    mCurrentUserFollowingList.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                getNearbyUserList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNearbyUserList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user_public_Info));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    double lat = Double.valueOf(ds.child("latitude").getValue().toString());
                    double lng = Double.valueOf(ds.child("longitude").getValue().toString());
                    String user_id = ds.child("user_id").getValue().toString();
                    Location currentLocation = new Location("My Location");
                    currentLocation.setLatitude(latitude);
                    currentLocation.setLongitude(longitude);
                    Location destinationLocation = new Location("User Location");
                    destinationLocation.setLatitude(lat);
                    destinationLocation.setLongitude(lng);
                    double distance = currentLocation.distanceTo(destinationLocation);
                    if (!mCurrentUserID.equals(user_id) && distance < 70000.0){
                        mNearbyUserList.add(ds.getValue(UserPublicInfo.class));
                    }
                }
                removeCurrentUserFriends();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeCurrentUserFriends() {
        for (int i=0; i<mNearbyUserList.size(); i++) {
            final int count = i;
            final UserPublicInfo userPublicInfo = mNearbyUserList.get(i);
            Query query = reference
                    .child(getString(R.string.dbname_followers))
                    .child(userPublicInfo.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        boolean check = true;
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            String uid = singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString();
                            if (uid.equals(mCurrentUserID)) {
                                check = false;
                            }
                        }
                        if (check == true){
                            mNearbyUserListFinal.add(userPublicInfo);
                        }
                    }
                    else{
                        mNearbyUserListFinal.add(userPublicInfo);
                    }
                    if (count == mNearbyUserList.size() - 1){
                        getFollowing();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFollowing() {
        for (int i=0; i<mNearbyUserListFinal.size(); i++){
            final int cnt = i;
            final UserPublicInfo userPublicInfo = mNearbyUserListFinal.get(i);
            Query query = reference
                            .child(getString(R.string.dbname_following))
                            .child(userPublicInfo.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> following = new ArrayList<>();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String uid = ds.child(getString(R.string.field_user_id)).getValue().toString();
                        following.add(uid);
                    }
                    if (cnt <= mNearbyUserListFinal.size() - 1){
                        int common = 0;
                        for (String s : mCurrentUserFollowingList) {
                            if (following.contains(s)){
                                ++common;
                            }
                        }
                        mMutualFriendList.add(common);
                    }
                    if (cnt == mNearbyUserListFinal.size() - 1){
                        setUpLayoutInflater();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setUpLayoutInflater() {
        ArrayList<NearbyPeople> mNearbyPeopleList = new ArrayList<>();
        for(int i=0; i<mMutualFriendList.size(); i++){

            UserPublicInfo userPublicInfo = mNearbyUserListFinal.get(i);
            int mutualFriend = mMutualFriendList.get(i);

            NearbyPeople nearbyPeople = new NearbyPeople();

            nearbyPeople.setFollowing(userPublicInfo.getFollowing());
            nearbyPeople.setHometown(userPublicInfo.getHometown());
            nearbyPeople.setLongitude(userPublicInfo.getLongitude());
            nearbyPeople.setUsername(userPublicInfo.getUsername());
            nearbyPeople.setNumber_of_travelled_places(userPublicInfo.getNumber_of_travelled_places());
            nearbyPeople.setProfile_photo(userPublicInfo.getProfile_photo());
            nearbyPeople.setFollowers(userPublicInfo.getFollowers());
            nearbyPeople.setLatitude(userPublicInfo.getLatitude());
            nearbyPeople.setPosts(userPublicInfo.getPosts());
            nearbyPeople.setUser_id(userPublicInfo.getUser_id());
            nearbyPeople.setMutual_friend(mutualFriend);

            mNearbyPeopleList.add(nearbyPeople);
        }
        animationView.setVisibility(View.GONE);
        listView.setAdapter(new SuggestForFollowAdapter(mContext,R.layout.suggest_for_follow_item, mNearbyPeopleList));
    }
}