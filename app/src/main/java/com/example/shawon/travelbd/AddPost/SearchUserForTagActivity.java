package com.example.shawon.travelbd.AddPost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 9/2/2018.
 */

public class SearchUserForTagActivity extends AppCompatActivity {

    private static final String TAG = "SearchUserForTag";
    private Context context = SearchUserForTagActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_for_tag);

        Log.d(TAG, "onCreate : Started.");

        setupToolbar();
    }

    private void setupToolbar() {

        Log.d(TAG,"setupToolbar : Setting up toolbar in SearchUserForTag Activity");

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchUserForTagToolBar);

        setSupportActionBar(toolbar);

    }
}