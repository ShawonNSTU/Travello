package com.example.shawon.travelbd.Profile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.UserPersonalInfo;
import com.example.shawon.travelbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by SHAWON on 7/21/2018.
 */

public class EditProfileFragment extends Fragment {

    private static final String TAG = "Edit Profile Fragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private Toolbar toolbar;
    private ImageView mClose;
    private ImageView mSaveCheck;
    private ImageView mProfilePhoto;
    private TextView mChangeProfilePhoto;
    private TextInputLayout mUserNameWrapper, mUserGenderWrapper, mUserPhoneWrapper, mUserBioWrapper, mUserEmailWrapper;
    EditText mUserNameEditText, mUserPhoneEditText, mUserBioEditText, mUserEmailEditText;
    private AutoCompleteTextView mUserGender;
    private ProgressBar mProgressBar;
    private String userID;
    private String mProfileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) getView().findViewById(R.id.editProfileToolBar);
        mClose = (ImageView) getView().findViewById(R.id.close);
        mSaveCheck = (ImageView) getView().findViewById(R.id.save_check);
        mProfilePhoto = (ImageView) getView().findViewById(R.id.profile_photo);
        mChangeProfilePhoto = (TextView) getView().findViewById(R.id.changeProfilePhoto);
        mUserNameWrapper = (TextInputLayout) getView().findViewById(R.id.user_name_wrapper);
        mUserEmailWrapper = (TextInputLayout) getView().findViewById(R.id.user_email_wrapper);
        mUserGenderWrapper = (TextInputLayout) getView().findViewById(R.id.user_gender_wrapper);
        mUserPhoneWrapper = (TextInputLayout) getView().findViewById(R.id.user_phone_wrapper);
        mUserBioWrapper = (TextInputLayout) getView().findViewById(R.id.user_bio_wrapper);
        mUserNameEditText = (EditText) getView().findViewById(R.id.user_name);
        mUserEmailEditText = (EditText) getView().findViewById(R.id.user_email);
        mUserGender = (AutoCompleteTextView) getView().findViewById(R.id.user_gender);
        mUserPhoneEditText = (EditText) getView().findViewById(R.id.user_phone);
        mUserBioEditText = (EditText) getView().findViewById(R.id.user_bio);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.editProfileProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= 21){
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray),android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        mUserNameWrapper.setHint("Username");
        mUserEmailWrapper.setHint("Email");
        mUserGenderWrapper.setHint("Gender");
        mUserPhoneWrapper.setHint("Phone Number");
        mUserBioWrapper.setHint("Bio");

        isUserLoggedInOrNot();

        setupToolbar();

        setupSpinnerGenderSelect(mUserGender);

        mUserEmailEditTextOnClick();

    }

    private void mUserEmailEditTextOnClick() {

        mUserEmailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null){
                    sendUserEmailVerification();
                    setupDialog(mAuth.getCurrentUser().getEmail());
                }
            }
        });

    }

    private void sendUserEmailVerification() {

        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "sendEmailVerification:success");
                }
                else {
                    Log.d(TAG, "sendEmailVerification:failed");
                }
            }
        });

    }

    private void setupDialog(String mUserEmail) {

        Log.d(TAG,"setupDialog:SendUserEmailVerification");

        final Dialog mDialog;

        mDialog = new Dialog(getActivity());

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.setContentView(R.layout.custom_popup_check_your_email);

        RelativeLayout mOkButtonArea = (RelativeLayout) mDialog.findViewById(R.id.ok_button_area);

        TextView mCheckEmailDialogBoxText = (TextView) mDialog.findViewById(R.id.check_email_dialog_box_text);

        mCheckEmailDialogBoxText.setText("Follow the link in the email we sent "+mUserEmail+" to confirm your email address and help secure your account.");

        mDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        mDialog.setCanceledOnTouchOutside(false);

        mOkButtonArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"setupDialog:OnClickOkButton");

                mDialog.dismiss();

            }
        });
        mDialog.show();

    }

    private void setupSpinnerGenderSelect(final AutoCompleteTextView mUserGender) {
        Log.d(TAG,"setupSpinnerGenderSelect:GenderSelection");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Gender_Names));

        mUserGender.setCursorVisible(false);

        mUserGender.setDropDownBackgroundResource(R.color.white);

        mUserGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"setOnItemClickListener:mUserGender");

                String selection = (String) parent.getItemAtPosition(position);

                Log.d(TAG,"Gender Selected : "+selection);

            }
        });

        mUserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"setOnClickListener:mUserGender");

                mUserGender.showDropDown();
                mUserGender.setAdapter(arrayAdapter);
            }
        });

    }

    private void setupToolbar() {
        Log.d(TAG,"setupToolbar:EditProfile Setting up Toolbar");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Edit Profile Close onClick : Profile Activity");
                getActivity().finish();
            }
        });

        mSaveCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Edit Profile Save onClick : Profile Activity");
                saveEditProfileWidget();
                getActivity().finish();
            }
        });

    }

    /**
     * Retrieve the data contained in the EditProfileWidget and submit it to the database
     */

    private void saveEditProfileWidget() {
        Log.d(TAG, "saveEditProfileWidget:saving EditProfileWidget data to the database");

        if (!mProfileImage.isEmpty()){
            Log.d(TAG, "saveEditProfileWidget:Profile Image Exists");
            // step 1: upload the photo to storage
            // step 2: then 'UserPersonalInfo' node
        }
        else {
            Log.d(TAG, "saveEditProfileWidget:No User Profile Image");
        }

        String username = mUserNameEditText.getText().toString();
        String gender = mUserGender.getText().toString();
        String phone_no = mUserPhoneEditText.getText().toString();
        String bio = mUserBioEditText.getText().toString();

        if (username.isEmpty()) username="";
        if (gender.isEmpty()) gender="";
        if (phone_no.isEmpty()) phone_no="";
        if (bio.isEmpty()) bio="";

        try {
            myRef.child(getString(R.string.user_personal_Info)).child(userID).child(getString(R.string.field_username)).setValue(username);

            myRef.child(getString(R.string.user_personal_Info)).child(userID).child(getString(R.string.field_gender)).setValue(gender);

            myRef.child(getString(R.string.user_personal_Info)).child(userID).child(getString(R.string.field_phone_number)).setValue(phone_no);

            myRef.child(getString(R.string.user_personal_Info)).child(userID).child(getString(R.string.field_bio)).setValue(bio);

            myRef.child(getString(R.string.user_public_Info)).child(userID).child(getString(R.string.field_username)).setValue(username);
        }catch (NullPointerException e){
            Log.e(TAG, "saveEditProfileWidget:NullPointerException :"+e.getMessage());
        }

    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    // This method is to check whether user logged in or not.

    private void isUserLoggedInOrNot() {

        Log.d(TAG,"EditProfileFragment : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    userID = user.getUid();

                    mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // retrieve user's all information from the database
                            retrieveUserInformation(dataSnapshot,user.getUid());
                            myRef.removeEventListener(this);

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

            // User Personal Info
            if (ds.getKey().equals(getString(R.string.user_personal_Info))){
                Log.d(TAG, "retrieveUserPersonalInformation: "+ds);
                try{
                    UserPersonalInfo userPersonalInfo = new UserPersonalInfo();

                    userPersonalInfo.setUsername(ds.child(uid).getValue(UserPersonalInfo.class).getUsername());

                    userPersonalInfo.setEmail(ds.child(uid).getValue(UserPersonalInfo.class).getEmail());

                    userPersonalInfo.setIs_user_email_verified(ds.child(uid).getValue(UserPersonalInfo.class).getIs_user_email_verified());

                    userPersonalInfo.setGender(ds.child(uid).getValue(UserPersonalInfo.class).getGender());

                    userPersonalInfo.setPhone_number(ds.child(uid).getValue(UserPersonalInfo.class).getPhone_number());

                    userPersonalInfo.setBio(ds.child(uid).getValue(UserPersonalInfo.class).getBio());

                    userPersonalInfo.setProfile_photo(ds.child(uid).getValue(UserPersonalInfo.class).getProfile_photo());

                    userPersonalInfo.setPassword(ds.child(uid).getValue(UserPersonalInfo.class).getPassword());

                    Log.d(TAG, "retrieveUserPersonalInformation:retrieving user personal info: "+userPersonalInfo.toString());

                    setupEditProfileWidget(userPersonalInfo);
                }catch (NullPointerException e){
                    Log.e(TAG, "retrieveUserPersonalInformation:NullPointerException :"+e.getMessage());
                }
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupEditProfileWidget(UserPersonalInfo userPersonalInfo) {

        Log.d(TAG, "setupEditProfileWidget:setup "+userPersonalInfo.getUsername()+" public information");

        // profile image
        mProfileImage = userPersonalInfo.getProfile_photo();
        if (!mProfileImage.isEmpty()){
            Picasso.get().load(userPersonalInfo.getProfile_photo()).into(mProfilePhoto);
        }
        else {
            Picasso.get().load(R.drawable.avatar).into(mProfilePhoto);
            mProfilePhoto.setBackgroundColor(Color.TRANSPARENT);
        }
        // username
        mUserNameEditText.setText(userPersonalInfo.getUsername());
        // email
        mUserEmailEditText.setText(mAuth.getCurrentUser().getEmail());
        if (!mAuth.getCurrentUser().isEmailVerified()) {
            mUserEmailEditText.setEnabled(true);
            mUserEmailEditText.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
        else{
            mUserEmailEditText.setEnabled(false);
        }
        // gender
        mUserGender.setText(userPersonalInfo.getGender());
        // phone_number
        mUserPhoneEditText.setText(userPersonalInfo.getPhone_number());
        // bio
        mUserBioEditText.setText(userPersonalInfo.getBio());
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