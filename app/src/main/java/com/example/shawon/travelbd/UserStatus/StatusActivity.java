package com.example.shawon.travelbd.UserStatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.Like;
import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = "StatusActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context context = StatusActivity.this;

    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearLayout1, mLinearLayout2, mLinearLayout3, mLinearLayout4;
    private CircleImageView mProfilePhoto;
    private TextView myStatus, mStatus;
    private TextView mRookie, mNomad, mExplorer, mExpert, mMaster;
    private SeekBar mSeekbar;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Log.d(TAG, "StatusActivity onCreate: Starting.");

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
        mLinearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        mLinearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        mLinearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        mLinearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        myStatus = (TextView) findViewById(R.id.my_status);
        mStatus = (TextView) findViewById(R.id.status);
        mRookie = (TextView) findViewById(R.id.rookie);
        mNomad = (TextView) findViewById(R.id.nomad);
        mExplorer = (TextView) findViewById(R.id.explorer);
        mExpert = (TextView) findViewById(R.id.expert);
        mMaster = (TextView) findViewById(R.id.master);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        dialog = new ProgressDialog(context);

        dialog.setMessage("Loading...");
        dialog.show();

        setupBottomNavigationView();

        setProfilePhoto();

        getInfo();

    }

    private void setProfilePhoto() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query = reference1.child(getString(R.string.user_public_Info))
                .orderByChild(getString(R.string.field_user_id)).equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(UserPublicInfo.class).toString());

                    UserPublicInfo userPublicInfo = new UserPublicInfo();
                    userPublicInfo = singleSnapshot.getValue(UserPublicInfo.class);
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    Picasso.get().load(userPublicInfo.getProfile_photo()).into(mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                    .child(getString(R.string.user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.uploaded));
        final List<Like> likesList = new ArrayList<Like>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                }
                int size = likesList.size();
                setUpWidgets(size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpWidgets(int size) {
        myStatus.setVisibility(View.VISIBLE);
        mStatus.setVisibility(View.VISIBLE);
        mLinearLayout1.setVisibility(View.VISIBLE);
        mSeekbar.setVisibility(View.VISIBLE);
        mLinearLayout2.setVisibility(View.VISIBLE);
        if(size >= 0 && size <= 2){
            mStatus.setText("- ROOKIE -");
            mRookie.setTextAppearance(context,R.style.TextStyle);
            mSeekbar.setProgress(1);
        }
        else if(size > 2 && size <= 5){
            mStatus.setText("- NOMAD -");
            mRookie.setTextAppearance(context,R.style.TextStyle);
            mNomad.setTextAppearance(context,R.style.TextStyle);
            mSeekbar.setProgress(2);
        }
        else if(size > 5 && size <= 8){
            mStatus.setText("- EXPLORER -");
            mRookie.setTextAppearance(context,R.style.TextStyle);
            mNomad.setTextAppearance(context,R.style.TextStyle);
            mExplorer.setTextAppearance(context,R.style.TextStyle);
            mSeekbar.setProgress(3);
        }
        else if(size > 8 && size <= 11){
            mStatus.setText("- EXPERT -");
            mRookie.setTextAppearance(context,R.style.TextStyle);
            mNomad.setTextAppearance(context,R.style.TextStyle);
            mExplorer.setTextAppearance(context,R.style.TextStyle);
            mExpert.setTextAppearance(context,R.style.TextStyle);
            mSeekbar.setProgress(4);
        }
        else if(size > 11){
            mStatus.setText("- MASTER -");
            mRookie.setTextAppearance(context,R.style.TextStyle);
            mNomad.setTextAppearance(context,R.style.TextStyle);
            mExplorer.setTextAppearance(context,R.style.TextStyle);
            mExpert.setTextAppearance(context,R.style.TextStyle);
            mMaster.setTextAppearance(context,R.style.TextStyle);
            mSeekbar.setProgress(5);
        }
        mSeekbar.setEnabled(false);
        dialog.dismiss();
    }

    // BottomNavigationView Setup

    private void setupBottomNavigationView() {

        Log.d(TAG, "StatusActivity setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);

        menuItem.setChecked(true);

    }
}