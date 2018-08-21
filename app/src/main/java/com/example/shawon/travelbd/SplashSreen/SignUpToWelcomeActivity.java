package com.example.shawon.travelbd.SplashSreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.shawon.travelbd.Home.HomeActivity;
import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 7/28/2018.
 */

public class SignUpToWelcomeActivity extends AppCompatActivity {

    private static final String TAG = "SignUpToWelcomeActivity";
    private Context context = SignUpToWelcomeActivity.this;

    private TextView mWelcomeUser, mWelcomeToCommunity;
    private AppCompatButton mGetStarted;

    private String mFirstName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.sign_up_to_welcome_activity);
        Log.d(TAG, "SignUpToWelcomeActivity onCreate: Starting.");

        mWelcomeUser = (TextView) findViewById(R.id.welcome_user_name);
        mWelcomeToCommunity = (TextView) findViewById(R.id.welcome_to_our_community);
        mGetStarted = (AppCompatButton) findViewById(R.id.get_started);

        setupAnimation();

        mGetStartedAction();

        mFirstName =  getIntentFromSignUp();

        setSpannableStringBuilderWelcomeUser();

    }

    private void setupAnimation() {

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.welcome_splash_screen);

        mWelcomeUser.startAnimation(animation);
        mWelcomeToCommunity.startAnimation(animation);

        Animation anim = AnimationUtils.loadAnimation(context,R.anim.bottom_up_anim);

        mGetStarted.startAnimation(anim);

    }

    private void setSpannableStringBuilderWelcomeUser() {

        SpannableStringBuilder builder;
        SpannableString str1,str2;

        /**
         * To get some changes on a particular text like Color

         * SpannableStringBuilder for the changed text to hold.

         * SpannableString is used to change the text, whatever you want.

         * BufferType.SPANNABLE is used for the runtime changed of the text.
         */

        builder = new SpannableStringBuilder();

        str1 = new SpannableString("Hello ");
        str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,str1.length(),0);
        builder.append(str1);

        str2 = new SpannableString(mFirstName);
        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)),0,str2.length(),0);
        builder.append(str2);

        mWelcomeUser.setText(builder,TextView.BufferType.SPANNABLE);

    }

    private String getIntentFromSignUp() {

        String firstName = "";
        if (getIntent() != null){
            String username = getIntent().getStringExtra("username");
            if (!username.isEmpty() && username != null){
                for (int i=0; i<username.length(); i++){
                    char ch = username.charAt(i);
                    if (ch == ' '){
                        break;
                    }
                    else{
                        firstName+=ch;
                    }
                }
            }
        }
        return firstName;
    }

    private void mGetStartedAction() {

        mGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void hideStatusBar() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}
