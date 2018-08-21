package com.example.shawon.travelbd.Notification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context context = NotificationActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "NotificationActivity onCreate: Starting.");

        setupBottomNavigationView();

    }

    // BottomNavigationView Setup

    private void setupBottomNavigationView() {

        Log.d(TAG, "NotificationActivity setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);

        menuItem.setChecked(true);

    }
}