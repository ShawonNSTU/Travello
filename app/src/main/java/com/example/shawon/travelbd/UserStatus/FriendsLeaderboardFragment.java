package com.example.shawon.travelbd.UserStatus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.shawon.travelbd.ModelClass.Like;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsLeaderboardFragment extends Fragment {

    private ListView listView;
    private ArrayList<Leaderboard> mLeaderboardList;
    private ArrayList<String> mFollowing;
    private ArrayList<UserPublicInfo> mUserPublicInfoList;
    private int cnt = -1;
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_leaderboard, container, false);
        Log.d("TAG","FriendsLeaderboardFragment");

        listView = (ListView) view.findViewById(R.id.listView);
        mLeaderboardList = new ArrayList<>();
        mFollowing = new ArrayList<>();
        mUserPublicInfoList = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        getInfo();

        return view;
    }

    private void getInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    mFollowing.add(singleSnapshot.child("user_id").getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d("getInfo",mFollowing.toString());
                getUserInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i=0; i<mFollowing.size(); i++) {
            Query query = reference
                    .child("User Public Info")
                    .orderByChild("user_id")
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d("TAG", "onDataChange: found user: "
                                + singleSnapshot.getValue(UserPublicInfo.class).getUsername());
                        UserPublicInfo userPublicInfo = new UserPublicInfo();
                        userPublicInfo.setFollowing(singleSnapshot.getValue(UserPublicInfo.class).getFollowing());
                        userPublicInfo.setHometown(singleSnapshot.getValue(UserPublicInfo.class).getHometown());
                        userPublicInfo.setLongitude(singleSnapshot.getValue(UserPublicInfo.class).getLongitude());
                        userPublicInfo.setUsername(singleSnapshot.getValue(UserPublicInfo.class).getUsername());
                        userPublicInfo.setNumber_of_travelled_places(singleSnapshot.getValue(UserPublicInfo.class).getNumber_of_travelled_places());
                        userPublicInfo.setProfile_photo(singleSnapshot.getValue(UserPublicInfo.class).getProfile_photo());
                        userPublicInfo.setFollowers(singleSnapshot.getValue(UserPublicInfo.class).getFollowers());
                        userPublicInfo.setLatitude(singleSnapshot.getValue(UserPublicInfo.class).getLatitude());
                        userPublicInfo.setPosts(singleSnapshot.getValue(UserPublicInfo.class).getPosts());
                        userPublicInfo.setUser_id(singleSnapshot.getValue(UserPublicInfo.class).getUser_id());
                        mUserPublicInfoList.add(userPublicInfo);
                    }
                    ++cnt;
                    getPointCount(cnt);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getPointCount(final int p) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child("User Photos")
                    .child(mUserPublicInfoList.get(p).getUser_id())
                    .child("Uploaded");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Like> likesList = new ArrayList<Like>();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("likes").getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                    }
                        int size = likesList.size();
                        Leaderboard leaderboard = new Leaderboard();
                        leaderboard.setUsername(mUserPublicInfoList.get(p).getUsername());
                        leaderboard.setProfile_photo(mUserPublicInfoList.get(p).getProfile_photo());
                        leaderboard.setUser_id(mUserPublicInfoList.get(p).getUser_id());
                        leaderboard.setPoints(String.valueOf(size));
                        mLeaderboardList.add(leaderboard);
                        Log.d("getPointCount", mLeaderboardList.toString());
                        if (p == mFollowing.size() - 1){

                            try {
                                Collections.sort(mLeaderboardList, new Comparator<Leaderboard>() {
                                    @Override
                                    public int compare(Leaderboard o1, Leaderboard o2) {
                                        return o2.getPoints().compareTo(o1.getPoints());
                                    }
                                });
                            } catch (NullPointerException e){
                                e.getMessage();
                            }
                            pd.dismiss();
                            listView.setAdapter(new LeaderboardItemAdapter(getActivity(),R.layout.user_leaderboard_item,mLeaderboardList));
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}