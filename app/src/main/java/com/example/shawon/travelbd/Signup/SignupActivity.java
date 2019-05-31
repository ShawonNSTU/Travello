package com.example.shawon.travelbd.Signup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shawon.travelbd.ModelClass.UserPersonalInfo;
import com.example.shawon.travelbd.ModelClass.UserPublicInfo;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.SplashSreen.SignUpToWelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SHAWON on 7/23/2018.
 */

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private Context context = SignupActivity.this;

    private RelativeLayout mRelativeLayout;
    private TextInputLayout mInputUsernameWrapper, mInputEmailWrapper, mInputPasswordWrapper;
    private EditText mInputUsername, mInputEmail, mInputPassword;
    private AppCompatButton mButtonSignUp;

    private String username;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private ProgressDialog mProgessDialog;

    private String mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "SignupActivity onCreate: Starting.");

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
        mInputUsername = (EditText) findViewById(R.id.input_username);
        mInputEmail = (EditText) findViewById(R.id.input_email);
        mInputPassword = (EditText) findViewById(R.id.input_password);
        mProgessDialog = new ProgressDialog(this);

        setTextInputLayoutHint();

        isUserLoggedInOrNot();

        mButtonSignUpAction();

    }

    private void mButtonSignUpAction() {

        mButtonSignUp = (AppCompatButton) findViewById(R.id.btn_signup);

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : SignUp Button");

                mProgessDialog.setMessage("Loading");
                mProgessDialog.show();

                username = mInputUsername.getText().toString();

                String email = mInputEmail.getText().toString();

                mPassword = mInputPassword.getText().toString();

                if (!isStringNull(username) && !isStringNull(email) && !isStringNull(mPassword)){

                    mAuth.createUserWithEmailAndPassword(email,mPassword).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                mProgessDialog.dismiss();
                                Log.d(TAG, "createUserWithEmailAndPassword : Successful");

                                if (mAuth.getCurrentUser() != null){

                                    String mUserEmail = mAuth.getCurrentUser().getEmail();

                                    setupDialog(mUserEmail);

                                }

                            }

                            else {
                                mProgessDialog.dismiss();
                                Log.d(TAG, "createUserWithEmailAndPassword : Failed");
                                Snackbar snackbar = Snackbar.make(mRelativeLayout,"Failed to Sign Up. Please put valid email.",Snackbar.LENGTH_LONG);
                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(R.color.white));
                                snackbar.show();
                            }
                        }
                    });

                }
                else {
                    mProgessDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mRelativeLayout,"You must fill out all the fields.",Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                }
            }
        });
    }

    private void setupDialog(String mUserEmail) {

        final Dialog mDialog;

        mDialog = new Dialog(context);

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

                Intent intent = new Intent(context,SignUpToWelcomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();

            }
        });
        mDialog.show();

    }

    private void setTextInputLayoutHint() {

        mInputUsernameWrapper = (TextInputLayout) findViewById(R.id.input_username_wrapper);

        mInputEmailWrapper = (TextInputLayout) findViewById(R.id.input_email_wrapper);

        mInputPasswordWrapper = (TextInputLayout) findViewById(R.id.input_password_wrapper);

        mInputUsernameWrapper.setHint("Full name");

        mInputEmailWrapper.setHint("Email");

        mInputPasswordWrapper.setHint("Password");

    }

    private boolean isStringNull(String string){

        Log.d(TAG, "isStringNull : Checking whether the string is null or not");

        if (string.equals("")){
            return true;
        }
        else{
            return false;
        }

    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    // This method is to check whether user logged in or not.

    private void isUserLoggedInOrNot() {

        Log.d(TAG,"SignUpActivity : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    sendUserEmailVerification();

                    mDatabase = FirebaseDatabase.getInstance();

                    myRef = mDatabase.getReference();

                    // addListenerForSingleValueEvent() executes onDataChange method immediately when data are changed/added in the
                    // reference location it is attached to. And after executing that method once, it stops listening to the reference
                    // location it is attached to.

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "addListenerForSingleValueEvent:onDataChange");

                            int isUserEmailVerified = 0;

                            if (user.isEmailVerified()){
                                isUserEmailVerified = 1;
                            }

                            UserPersonalInfo userPersonalInfo = new UserPersonalInfo(username,user.getEmail(),isUserEmailVerified,"","","","",mPassword,user.getUid());
                            myRef.child(getString(R.string.user_personal_Info)).child(user.getUid()).setValue(userPersonalInfo);

                            UserPublicInfo userPublicInfo = new UserPublicInfo(username,"",0,0,0,"",0,user.getUid());
                            myRef.child(getString(R.string.user_public_Info)).child(user.getUid()).setValue(userPublicInfo);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "addListenerForSingleValueEvent:onCancelled");
                        }
                    });

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    private void sendUserEmailVerification() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
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