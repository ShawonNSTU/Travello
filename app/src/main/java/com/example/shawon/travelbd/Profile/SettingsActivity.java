package com.example.shawon.travelbd.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shawon.travelbd.Login.LoginActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.example.shawon.travelbd.Utils.FilePath;
import com.example.shawon.travelbd.Utils.ImageManager;
import com.example.shawon.travelbd.Utils.SectionsStatePagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by SHAWON on 7/20/2018.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Context context = SettingsActivity.this;
    private static final int ACTIVITY_NUM = 4;

    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private SectionsStatePagerAdapter sectionsStatePagerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    private ProgressDialog mProgessDialog;

    private double mPhotoUploadProgress = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "SettingsActivity onCreate: Starting.");

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
        mProgessDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        setupFragments();

        getIncomingIntent();

        setupAccountSettingsList();

        onClickBackArrow();

        setupBottomNavigationView();

        isUserLoggedInOrNot();

    }

    private void getIncomingIntent() {

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.selected_image))){
            String intentData = intent.getStringExtra(getString(R.string.selected_image));
            setupViewPager(sectionsStatePagerAdapter.getFragmentNumber(getString(R.string.edit_profile)));
            uploadProfilePhotoToStorage(intentData);
        }

        if (intent.hasExtra("EditProfileButton")){
            Log.d(TAG,"getIncomingIntent:received incoming intent from ProfileActivity");

            setupViewPager(sectionsStatePagerAdapter.getFragmentNumber(getString(R.string.edit_profile)));
        }

    }

    private void uploadProfilePhotoToStorage(String imgUrl) {
        Log.d(TAG,"uploadProfilePhotoToStorage : started");

        FilePath filePath = new FilePath();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageReference = mStorageReference
                .child(filePath.FIREBASE_IMAGE_STORAGE_PATH_OF_USERS + "/" + userID + "/profile_photo");

        // convert image url to bitmap
        Bitmap bitmap = ImageManager.getBitmap(imgUrl);

        // convert bitmap to byte array
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap,100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"uploadProfilePhotoToStorage : onSuccess");

                Toast.makeText(context,"Your profile photo successfully updated!",Toast.LENGTH_SHORT).show();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                // add the profile photo to 'UserPersonalInfo' and 'UserPublicInfo' node
                addPhotoToDatabase(downloadUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"uploadProfilePhotoToStorage : onFailure");
                Toast.makeText(context,"Sorry, an error occurred!",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = 0.0;
                if (taskSnapshot.getTotalByteCount() > 0){
                    progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                }
                if (progress - 15 > mPhotoUploadProgress){
                    Toast.makeText(context, "Processing... "+String.format("%.0f",progress)+"%",Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG,"uploadProfilePhotoToStorage : onProgress : "+progress+"% done.");
            }
        });
    }

    private void addPhotoToDatabase(String url) {
        Log.d(TAG, "addPhotoToDatabase : adding profile photo to the database");

        myRef.child(context.getString(R.string.user_personal_Info))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.profile_photo))
                .setValue(url);

        myRef.child(context.getString(R.string.user_public_Info))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.profile_photo))
                .setValue(url);

        myRef.child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("profile_image")
                .setValue(url);

        finish();
    }

    private void setupFragments() {

        sectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        sectionsStatePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile));      // Fragment 0

    }

    private void setupViewPager(int fragmentNumber) {

        mRelativeLayout.setVisibility(View.GONE);

        Log.d(TAG, "navigating to fragment no - " + fragmentNumber);

        mViewPager.setAdapter(sectionsStatePagerAdapter);

        mViewPager.setCurrentItem(fragmentNumber);

    }

    // set up the account settings list

    private void setupAccountSettingsList() {

        Log.d(TAG, "Initializing Account Settings List");

        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        listView.setOverscrollFooter(new ColorDrawable(Color.TRANSPARENT));     // to remove bottom divider of list view

        ArrayList<String> accountSettingsList = new ArrayList<>();

        accountSettingsList.add(getString(R.string.edit_profile));          // Fragment 0

        /*accountSettingsList.add(getString(R.string.change_password));         // Fragment 1

        accountSettingsList.add("Posts You've Liked");*/            // Fragment 2

        accountSettingsList.add(getString(R.string.sign_out));

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, accountSettingsList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    Log.d(TAG, "clicked on : " + sectionsStatePagerAdapter.getFragmentName(position) + " Fragment");
                    setupViewPager(position);
                }

                else if (position > 1) {
                    Log.d(TAG, "clicked on : " + sectionsStatePagerAdapter.getFragmentName(position-1) + " Fragment");
                    setupViewPager(position - 1);
                }

                else {
                    Log.d(TAG, "clicked on:Sign Out");
                    setupSignOut();
                }
            }
        });
    }

    private void setupSignOut() {

        mProgessDialog.setMessage("Signing Out");
        mProgessDialog.show();

        mAuth.signOut();

        finish();

    }

    // set up the back arrow for navigating back to the 'ProfileActivity'

    private void onClickBackArrow() {

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Back from SettingsActivity to ProfileActivity");
                finish();
            }
        });

    }

    private void setupBottomNavigationView() {

        Log.d(TAG, "SettingsActivity setupBottomNavigationView: setting up BottomNavigationView");

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

        Log.d(TAG, "Settings Activity : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Log.d(TAG, "onAuthStateChanged:navigating to Login Activity" );

                    mProgessDialog.dismiss();

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

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