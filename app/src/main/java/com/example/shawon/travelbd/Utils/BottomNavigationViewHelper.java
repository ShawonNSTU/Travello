package com.example.shawon.travelbd.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.shawon.travelbd.AddPost.AddPostActivity;
import com.example.shawon.travelbd.Home.HomeActivity;
import com.example.shawon.travelbd.Notification.NotificationActivity;
import com.example.shawon.travelbd.Profile.ProfileActivity;
import com.example.shawon.travelbd.R;
import com.example.shawon.travelbd.Search.SearchActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by SHAWON on 7/17/2018.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavViewHelper";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG,"Utils setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.ic_home:
                        Intent intent1 = new Intent(context,HomeActivity.class);   // ACTIVITY_NUM 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context,SearchActivity.class);  // ACTIVITY_NUM 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_add:
                        Intent intent3 = new Intent(context,AddPostActivity.class); // ACTIVITY_NUM 2
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_favorite:
                        Intent intent4 = new Intent(context,NotificationActivity.class);    // ACTIVITY_NUM 3
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_person:
                        Intent intent5 = new Intent(context,ProfileActivity.class); // ACTIVITY_NUM 4
                        context.startActivity(intent5);
                        break;

                }
                return false;
            }
        });
    }

}
