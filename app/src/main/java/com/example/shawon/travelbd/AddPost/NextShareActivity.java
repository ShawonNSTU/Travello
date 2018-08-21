package com.example.shawon.travelbd.AddPost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shawon.travelbd.R;

/**
 * Created by SHAWON on 8/20/2018.
 */

public class NextShareActivity extends AppCompatActivity {

    private static final String TAG = "NextShareActivity";
    private Context context = NextShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_share);

        Log.d(TAG,"NextShareActivity onCreate : Started");
        Log.d(TAG,"onCreate : Selected Image from GalleryFragment: "+getIntent().getStringExtra(getString(R.string.selected_image)));

    }
}
