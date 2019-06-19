package com.example.shawon.travelbd.Utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.shawon.travelbd.R;

import java.util.ArrayList;

/**
 * Created by SHAWON on 6/19/2019.
 */

public class UtilitiesActivity extends AppCompatActivity {

    private ArrayList<String> mActivityName;
    private ListView listView;
    private Context mContext;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities);

        listView = (ListView) findViewById(R.id.listView);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        mContext = UtilitiesActivity.this;

        mActivityName = new ArrayList<>();
        mActivityName.add("Weather Forecast");
        mActivityName.add("Currency Converter");
        mActivityName.add("World Clock Time");

        listView.setAdapter(new UtilitiesItemAdapter(mContext,R.layout.utilities_item,mActivityName));

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}