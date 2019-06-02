package com.example.shawon.travelbd.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.Photo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.ViewCommentsFragment;
import com.example.shawon.travelbd.Utils.ViewPostFragment;
import com.example.shawon.travelbd.Utils.ViewProfileFragment;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener {

    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;

    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image Grid view: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "ProfileActivity onCreate: Starting.");
        init();
    }

    private void init() {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.intent_user))){
                Log.d(TAG, "init: inflating view profile");
                ViewProfileFragment fragment = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.intent_user),
                        intent.getParcelableExtra(getString(R.string.intent_user)));
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();
            }else{
                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Log.d(TAG, "init: inflating Profile");
            ProfileFragment mProfileFragment = new ProfileFragment();
            android.support.v4.app.FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mProfileFragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }
    }
    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        else super.onBackPressed();
    }
}