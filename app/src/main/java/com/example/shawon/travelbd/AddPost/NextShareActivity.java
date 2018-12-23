package com.example.shawon.travelbd.AddPost;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shawon.travelbd.ModelClass.Photo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.FilePath;
import com.example.shawon.travelbd.Utils.ImageManager;
import com.example.shawon.travelbd.Utils.StringManipulation;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.Locale;

/**
 * Created by SHAWON on 8/20/2018.
 */

public class NextShareActivity extends AppCompatActivity {

    private static final String TAG = "NextShareActivity";
    private Context context = NextShareActivity.this;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int INCOMING_ACTIVITY_REQUEST_CODE = 701;
    private static final int INCOMING_ACTIVITY_SEARCH_USER_FOR_TAG_REQUEST_CODE = 702;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    private String imageUrl = "file://";
    private String imgUrl="";
    private double mPhotoUploadProgress = 0.0;
    private String mSelectedLocation = "";
    private String mSelectedLocationRating = "";
    private float mRating = (float) 0.0;
    private String mTotalSelectedUserNameForTag = "";
    private String mTotalSelectedUserAuthIdForTag = "";
    private boolean mFacebookSelected = false;

    private TextView mAddLocation;
    private ImageView mLocationIcon;
    private TextView mRateYourTraveledPlace;
    private ImageView mRatingIcon;
    private TextView mTagPeople;
    private ImageView mTagPeopleIcon;
    private TextView mFacebook;
    private SwitchCompat mSwitchCompat;
    private EditText mPhotoDescription;
    private TextView mShareButton;

    private ShareDialog mShareDialog;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "Target : onBitmapLoaded : Started");

            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class)){

                SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();

                mShareDialog.show(sharePhotoContent);

                Log.d(TAG, "Target : onBitmapLoaded : Finished");

            }
            else {
                Log.d(TAG, "Target : onBitmapLoaded : SharePhotoContent.class does not supported.");
            }
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.d(TAG, "Target : onBitmapFailed");
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            //later code
        }
    };

    // variables that are related to uploaded image
    private int imageCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_share);

        Log.d(TAG,"NextShareActivity onCreate : Started");
        Log.d(TAG,"onCreate : Selected Image from GalleryFragment: "+getIntent().getStringExtra(getString(R.string.selected_image)));

        mShareDialog = new ShareDialog(this);
        mAddLocation = (TextView) findViewById(R.id.add_location);
        mLocationIcon = (ImageView) findViewById(R.id.location_icon);
        mRateYourTraveledPlace = (TextView) findViewById(R.id.add_rating);
        mRatingIcon = (ImageView) findViewById(R.id.rating_icon);
        mTagPeople = (TextView) findViewById(R.id.tag_people);
        mTagPeopleIcon = (ImageView) findViewById(R.id.tag_people_icon);
        mFacebook = (TextView) findViewById(R.id.facebook_share);
        mSwitchCompat = (SwitchCompat) findViewById(R.id.facebook_share_switch);
        mPhotoDescription = (EditText) findViewById(R.id.description);
        mShareButton = (TextView) findViewById(R.id.tvShare);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        setupToolbar();

        setSelectedImage();

        addLocationOnClick();

        isUserLoggedInOrNot();

        rateYourTraveledPlaceOnClick();

        tagPeopleOnClick();

        mFacebookOnClick();

        mSwitchCompatCheckedOnChangeListener();

        mShareButtonOnClick();

    }

    private void mShareButtonOnClick() {

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mShareButton : OnClicked");
                sharePost();
            }
        });

    }

    private void sharePost() {

        if (mFacebookSelected){
            Picasso.get().load(imageUrl).into(target);
        }

        // upload the image to firebase storage
        Log.d(TAG, "sharePost : Attempting to upload the photo of travelled place to firebase storage");
        if(mPhotoDescription.getText().toString().length()>0 &&
                mSelectedLocation.length()>0 &&
                mRating > 0.0) uploadPhotoOfTravelledPlace();
        else Toast.makeText(this,"Please give all the specified information",Toast.LENGTH_SHORT).show();

    }

    private void uploadPhotoOfTravelledPlace() {
        Log.d(TAG, "uploadPhotoOfTravelledPlace : started");

        FilePath filePath = new FilePath();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = mStorageReference
                .child(filePath.FIREBASE_IMAGE_STORAGE_PATH_OF_USERS + userID + "/uploaded" + "/photo" + imageCount);

        // convert image url to bitmap
        Bitmap bitmap = ImageManager.getBitmap(imgUrl);

        // convert bitmap to byte array
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap,100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"uploadPhotoOfTravelledPlace : onSuccess");

                Toast.makeText(context,"Photo successfully uploaded!",Toast.LENGTH_SHORT).show();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                // add the new photo to 'photos' and 'user photos' node
                addPhotoToDatabase(mPhotoDescription.getText().toString(),downloadUrl.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"uploadPhotoOfTravelledPlace : onFailure");

                Toast.makeText(context,"Photo upload failed!",Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = 0.0;
                if (taskSnapshot.getTotalByteCount() > 0){
                    progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                }
                if (progress - 15 > mPhotoUploadProgress){
                    Toast.makeText(context, "Uploading... "+String.format("%.0f",progress)+"%",Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG,"uploadPhotoOfTravelledPlace : onProgress : "+progress+"% done.");

            }
        });
    }

    private void addPhotoToDatabase(String description, String downloadUrl) {
        Log.d(TAG, "addPhotoToDatabase : adding uploaded photo to the database");

        String newPhotoKey = myRef.child(context.getString(R.string.photos)).push().getKey();

        Photo photo = new Photo();

        photo.setCaption(description);
        photo.setUploaded_date(getDateTime());
        photo.setImage_url(downloadUrl);
        photo.setPhoto_id(newPhotoKey);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setLocation(mSelectedLocation);
        photo.setRating(Float.toString(mRating));
        photo.setGoogle_places_rating(mSelectedLocationRating);
        photo.setTagged_people(mTotalSelectedUserAuthIdForTag);
        String tags = StringManipulation.getTags(description);
        if (!tags.equals("No Tags")) photo.setTags(tags);
        else photo.setTags("");

        myRef.child(context.getString(R.string.user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.uploaded))
                .child(newPhotoKey).setValue(photo);
        myRef.child(context.getString(R.string.photos))
                .child(newPhotoKey).setValue(photo);

        String taggedAuthID = "";
        for(int i=0; i<mTotalSelectedUserAuthIdForTag.length(); i++){
            char ch = mTotalSelectedUserAuthIdForTag.charAt(i);
            if(ch == '@'){
                myRef.child(context.getString(R.string.user_photos))
                        .child(taggedAuthID)
                        .child(context.getString(R.string.tagged))
                        .child(newPhotoKey).setValue(photo);
                taggedAuthID = "";
            }
            else{
                taggedAuthID+=ch;
            }
        }
    }

    private String getDateTime() {
        Log.d(TAG,"getDateTime : getting default date-time format");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        return sdf.format(new Date());

    }

    private void mSwitchCompatCheckedOnChangeListener() {

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d(TAG, "mSwitchCompat : Checked");
                    mFacebookSelected = true;
                }
                else {
                    Log.d(TAG,"mSwitchCompat : Unchecked");
                    mFacebookSelected = false;
                }
            }
        });

    }

    private void mFacebookOnClick() {

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"mFacebook : onClicked");
                if (mSwitchCompat.isChecked()){
                    mSwitchCompat.setChecked(false);
                }
                else {
                    mSwitchCompat.setChecked(true);
                }
            }
        });

    }

    private void tagPeopleOnClick() {

        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout5);

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"TagPeople : OnClick : Navigating To SearchUserForTagActivity");

                Intent intent = new Intent(context,SearchUserForTagActivity.class);
                startActivityForResult(intent,INCOMING_ACTIVITY_SEARCH_USER_FOR_TAG_REQUEST_CODE);
            }
        });

        mTagPeopleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"mTagPeopleIcon : onClick");
                if(!mTotalSelectedUserNameForTag.equals("")){
                    Log.d(TAG,"mTagPeopleIcon : onClick : Removing All The Tagged People.");
                    mTagPeople.setText(getString(R.string.tag_people));
                    mTotalSelectedUserNameForTag = "";
                    mTotalSelectedUserAuthIdForTag = "";
                    mTagPeopleIcon.setImageDrawable(getDrawable(R.drawable.ic_tag_people));
                    mTagPeopleIcon.setColorFilter(null);
                }
                else {
                    Log.d(TAG,"mTagPeopleIcon : OnClick : Navigating To SearchUserForTagActivity");
                    Intent intent = new Intent(context,SearchUserForTagActivity.class);
                    startActivityForResult(intent,INCOMING_ACTIVITY_SEARCH_USER_FOR_TAG_REQUEST_CODE);
                }
            }
        });

    }

    private void rateYourTraveledPlaceOnClick() {

        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout4);

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"rateYourTraveledPlace : OnClick");

                if (mSelectedLocation.equals("")){
                    Log.d(TAG,"rateYourTraveledPlaceOnClick : Didn't click on add location");
                    Toast.makeText(context,"Sorry, you didn't add the location of your traveled place.",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"rateYourTraveledPlaceOnClick : Navigating To Rating Dialog");
                    setupRatingDialog();
                }
            }
        });
    }

    private void setupRatingDialog() {

        Log.d(TAG,"setupRatingDialog : Setting up the rating dialog");

        final Dialog mDialog;

        mDialog = new Dialog(context);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.setContentView(R.layout.custom_rating_dialog);

        RelativeLayout mOkButtonArea = (RelativeLayout) mDialog.findViewById(R.id.ok_button_area);

        final RatingBar mRatingBar = (RatingBar) mDialog.findViewById(R.id.ratingBar);

        mRatingBar.setRating(mRating);

        final TextView mRateThisPlace = (TextView) mDialog.findViewById(R.id.rate_this_place);

        final TextView mRatingBarIndicatorText = (TextView) mDialog.findViewById(R.id.ratingBarIndicatorText);

        mDialog.getWindow().getAttributes().windowAnimations = R.style.fade_in_animation;

        mDialog.setCanceledOnTouchOutside(false);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                mRating = rating;

                Log.d(TAG,"mRatingBar : setOnRatingBarChangeListener : Rating Changed To: "+mRating);

                if (mRating > 0.0) {
                    mRateThisPlace.setText(R.string.thank_you_for_the_rating);
                    mRatingBarIndicatorText.setText(String.valueOf(rating));
                }
                else {
                    mRateThisPlace.setText(getString(R.string.rate_this_place));
                    mRatingBarIndicatorText.setText(getString(R.string.tap_a_star_to_rate));
                }
            }
        });

        mOkButtonArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"setupRatingDialog : OnClickOkButton");

                mDialog.dismiss();

                if (mRating > 0.0){
                    mRateYourTraveledPlace.setText(String.valueOf(mRating));
                    mRatingIcon.setColorFilter(getResources().getColor(R.color.next));
                }
                else {
                    mRatingIcon.setColorFilter(null);
                    mRateYourTraveledPlace.setText(getString(R.string.rate_this_place));
                }
            }
        });
        mDialog.show();

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK){
                    Log.d(TAG,"mDialog : setOnKeyListener : Pressed on the back button to dismiss the dialog");

                    if (!mRateYourTraveledPlace.getText().toString().equals(getString(R.string.rate_this_place))) {
                        mRating = Float.parseFloat(mRateYourTraveledPlace.getText().toString());
                    }
                    else{
                        mRating = 0;
                    }
                    dialog.dismiss();
                }
                return true;
            }
        });

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

        Log.d(TAG,"onActivityResult : Called");

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
                if (Float.parseFloat(mSelectedLocationRating) < 0){
                    mSelectedLocationRating = "2.2";
                }

                mAddLocation.setText(mSelectedLocation);
                mLocationIcon.setColorFilter(getResources().getColor(R.color.next));

                mRateYourTraveledPlace.setText(getString(R.string.rate_this_place));
                mRatingIcon.setColorFilter(null);

                mRating = 0;
            }
        }
        if (requestCode == INCOMING_ACTIVITY_SEARCH_USER_FOR_TAG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String mGetIntentFromIncomingActivity = data.getStringExtra(getString(R.string.selected_user_info));
                String mSelectedSingleUserAuthIdForTag = "";
                String mSelectedSingleUserNameForTag = "";
                int i;
                for (i=0; i<mGetIntentFromIncomingActivity.length(); i++){
                    if(mGetIntentFromIncomingActivity.charAt(i)!='@') mSelectedSingleUserAuthIdForTag+=mGetIntentFromIncomingActivity.charAt(i);
                    else break;
                }
                for(int j = i+1; j<mGetIntentFromIncomingActivity.length(); j++){
                    mSelectedSingleUserNameForTag+=mGetIntentFromIncomingActivity.charAt(j);
                }
                if (mTotalSelectedUserNameForTag.equals("")){
                    mTotalSelectedUserNameForTag+=mSelectedSingleUserNameForTag+"@";
                }
                if (mTotalSelectedUserAuthIdForTag.equals("")){
                    mTotalSelectedUserAuthIdForTag+=mSelectedSingleUserAuthIdForTag+"@";
                }
                if (mTagPeople.getText().toString().equals(getString(R.string.tag_people))){
                    mTagPeople.setText(mSelectedSingleUserNameForTag);
                }
                else{
                    String single_user_auth = "";
                    boolean check = false;
                    for (int k=0; k<mTotalSelectedUserAuthIdForTag.length(); k++){
                        char ch = mTotalSelectedUserAuthIdForTag.charAt(k);
                        if (ch == '@'){
                            if (single_user_auth.equals(mSelectedSingleUserAuthIdForTag)){
                                check = true;
                                break;
                            }
                            single_user_auth = "";
                        }
                        else {
                            single_user_auth+=ch;
                        }
                    }
                    if (!check){
                        mTotalSelectedUserNameForTag+=mSelectedSingleUserNameForTag+"@";
                        mTotalSelectedUserAuthIdForTag+=mSelectedSingleUserAuthIdForTag+"@";
                        String string = "";
                        for(int k=0; k<mTotalSelectedUserNameForTag.length(); k++){
                            char character = mTotalSelectedUserNameForTag.charAt(k);
                            if (character == '@'){
                                string+="  ";
                            }
                            else {
                                string+=character;
                            }
                        }
                        mTagPeople.setText(string);
                    }
                }
                mTagPeopleIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                mTagPeopleIcon.setColorFilter(getResources().getColor(R.color.next));
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
        imageUrl+= intent.getStringExtra(getString(R.string.selected_image));
        imgUrl+= intent.getStringExtra(getString(R.string.selected_image));
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
                            Log.d(TAG, "myRef : addValueEventListener : onDataChange");

                            imageCount = getImageCount(dataSnapshot);

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

    private int getImageCount(DataSnapshot dataSnapshot) {

        Log.d(TAG, "getImageCount : called");

        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(getString(R.string.user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.uploaded))
                .getChildren()){
            count++;
        }
        Log.d(TAG, "getImageCount : Counted image : "+count);
        return count;
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