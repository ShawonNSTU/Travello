package com.example.shawon.travelbd.UserStatus;

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

public class AllUsersLeaderboardFragment extends Fragment {

    private static final String TAG = "AllUsersLeaderboard";
    private ListView listView;
    private ArrayList<String> mUserIdList;
    private ArrayList<UserPublicInfo> mUserPublicInfoList;
    private ArrayList<Leaderboard> mLeaderboardList;
    private int cnt = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_users_leaderboard, container, false);
        Log.d("TAG","AllUsersLeaderboardFragment");

        listView = (ListView) view.findViewById(R.id.listView);
        mUserIdList = new ArrayList<>();
        mLeaderboardList = new ArrayList<>();
        mUserPublicInfoList = new ArrayList<>();

        getAlluser();

        return view;
    }

    private void getAlluser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User Public Info");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    mUserIdList.add(ds.child("user_id").getValue().toString());
                }
                getAlluserPublicInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAlluserPublicInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i=0; i<mUserIdList.size(); i++) {
            Query query = reference
                    .child("User Public Info")
                    .orderByChild("user_id")
                    .equalTo(mUserIdList.get(i));
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
                if (p == mUserIdList.size() - 1){
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
                    listView.setAdapter(new LeaderboardItemAdapter(getActivity(),R.layout.user_leaderboard_item,mLeaderboardList));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}