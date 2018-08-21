package com.example.shawon.travelbd.Login;

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
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shawon.travelbd.Home.HomeActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by SHAWON on 7/22/2018.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LogintActivity";
    private Context context = LoginActivity.this;

    private RelativeLayout mRelativeLayout;
    private TextInputLayout mInputEmailWrapper, mInputPasswordWrapper;
    private EditText mInputEmail, mInputPassword;
    private AppCompatButton mButtonLogIn;
    private TextView mLinkSignUp;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgessDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "LoginActivity onCreate: Starting.");

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);

        mLinkSignUp = (TextView) findViewById(R.id.link_signup);

        mInputEmail = (EditText) findViewById(R.id.input_email);
        mInputPassword = (EditText) findViewById(R.id.input_password);

        mProgessDialog = new ProgressDialog(this);

        setTextInputLayoutHint();

        setSpannableStringBuilderLinkSignUp();

        isUserLoggedInOrNot();

        mButtonLogInAction();

        mLinkSignUpAction();

    }

    private void mLinkSignUpAction() {

        mLinkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "LoginActivity LinkSignUp : Navigating to SignUpActivity");

                Intent intent = new Intent(context,SignupActivity.class);
                startActivity(intent);
                finish();

            }
        });

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

    // Login button action

    private void mButtonLogInAction() {

        mButtonLogIn = (AppCompatButton) findViewById(R.id.btn_login);

        mButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Login Button");

                mProgessDialog.setMessage("Logging In");
                mProgessDialog.show();

                String email = mInputEmail.getText().toString();

                String password = mInputPassword.getText().toString();

                if (!isStringNull(email) && !isStringNull(password)){

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                mProgessDialog.dismiss();
                                Log.d(TAG, "signInWithEmailAndPassword : Successful");

                                /*
                                    If user logged in successfully then navigate to HomeActivity and call 'finish()'
                                 */

                                if (mAuth.getCurrentUser() != null){

                                    Intent intent = new Intent(context,HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                            else {
                                mProgessDialog.dismiss();
                                Log.d(TAG, "signInWithEmailAndPassword : Failed");
                                Snackbar snackbar = Snackbar.make(mRelativeLayout,"Failed to login. Please put valid email and password.",Snackbar.LENGTH_LONG);
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

    private void setSpannableStringBuilderLinkSignUp() {

        SpannableStringBuilder builder;
        SpannableString str1,str2;

        /**
         * To get some changes on a particular text like Color

         * SpannableStringBuilder for the changed text to hold.

         * SpannableString is used to change the text, whatever you want.

         * BufferType.SPANNABLE is used for the runtime changed of the text.
         */

        builder = new SpannableStringBuilder();

        str1 = new SpannableString("Don't have an account? ");
        str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_grey)),0,str1.length(),0);
        builder.append(str1);

        str2 = new SpannableString("Sign up.");
        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_black)),0,str2.length(),0);
        builder.append(str2);

        mLinkSignUp.setText(builder,TextView.BufferType.SPANNABLE);

    }

    private void setTextInputLayoutHint() {

        mInputEmailWrapper = (TextInputLayout) findViewById(R.id.input_email_wrapper);

        mInputPasswordWrapper = (TextInputLayout) findViewById(R.id.input_password_wrapper);

        mInputEmailWrapper.setHint("Email");

        mInputPasswordWrapper.setHint("Password");

    }

    /*
        --------------------------------- Firebase ----------------------------------
     */

    // This method is to check whether user logged in or not.

    private void isUserLoggedInOrNot() {

        Log.d(TAG,"LoginActivity : isUserLoggedInOrNot()");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

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