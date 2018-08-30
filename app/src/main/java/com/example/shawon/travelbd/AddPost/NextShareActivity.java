package com.example.shawon.travelbd.AddPost;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 8/20/2018.
 */

public class NextShareActivity extends AppCompatActivity {

    private static final String TAG = "NextShareActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int INCOMING_ACTIVITY_REQUEST_CODE = 701;
    private Context context = NextShareActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private String mAppend = "file://";

    private TextView mAddLocation;
    private ImageView mLocationIcon;

    private String mSelectedLocation = "";

    private String mSelectedLocationRating = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_share);

        Log.d(TAG,"NextShareActivity onCreate : Started");
        Log.d(TAG,"onCreate : Selected Image from GalleryFragment: "+getIntent().getStringExtra(getString(R.string.selected_image)));

        mAddLocation = (TextView) findViewById(R.id.add_location);
        mLocationIcon = (ImageView) findViewById(R.id.location_icon);

        setupToolbar();

        setSelectedImage();

        addLocationOnClick();

        isUserLoggedInOrNot();

    }

    private void addLocationOnClick() {

        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout3);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServicesOK()) {
                    gotoLocationSelectActivity();
                }
            }
        });

    }

    private boolean isServicesOK() {

        Log.d(TAG,"isServicesOK : checking Google Play Services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log.d(TAG,"isServicesOK : Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error faced but user can resolve it
            Log.d(TAG,"isServicesOK : an error faced but user can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NextShareActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(context,"Sorry, you can't make map requests.",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void gotoLocationSelectActivity() {
        Intent intent = new Intent(context,LocationSelectFromGooglePlaces.class);
        startActivityForResult(intent,INCOMING_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INCOMING_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                String mGetIntentFromIncomingActivity = data.getStringExtra(getString(R.string.user_selected_location));
                mSelectedLocation = "";
                mSelectedLocationRating = "";
                int i;
                for (i=0; i<mGetIntentFromIncomingActivity.length(); i++){
                    if(mGetIntentFromIncomingActivity.charAt(i)!='@') mSelectedLocation+=mGetIntentFromIncomingActivity.charAt(i);
                    else break;
                }
                for(int j = i+1; j<mGetIntentFromIncomingActivity.length(); j++){
                    mSelectedLocationRating+=mGetIntentFromIncomingActivity.charAt(j);
                }
                mAddLocation.setText(mSelectedLocation);
                mLocationIcon.setColorFilter(getResources().getColor(R.color.next));
            }
        }
    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setSelectedImage() {

        Log.d(TAG,"setSelectedImage:setting up the image that was selected from gallery or captured by camera");

        Intent intent = getIntent();
        ImageView mSelectedImage = (ImageView) findViewById(R.id.imageShare);
        String imageUrl = mAppend + intent.getStringExtra(getString(R.string.selected_image));
        Picasso.get().load(imageUrl).resize(200,200).onlyScaleDown().centerCrop().into(mSelectedImage);

    }

    private void setupToolbar() {

        Log.d(TAG,"setupToolbar : Setting up toolbar in NextShare Activity");

        Toolbar toolbar = (Toolbar) findViewById(R.id.nextShareToolBar);

        setSupportActionBar(toolbar);

        ImageView mBackArrow = (ImageView) findViewById(R.id.backArrow);

        TextView mShareButton = (TextView) findViewById(R.id.tvShare);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"BackArrow onClick : closing the NextShare activity");
                finish();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Share Button onClick : Sharing the post by uploading data to database.");

                // upload the image to database
            }
        });

    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    // This method is to check whether user logged in or not.

    private void isUserLoggedInOrNot() {

        Log.d(TAG,"NextShare Activity : isUserLoggedInOrNot()");

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