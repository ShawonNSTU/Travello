package com.example.shawon.travelbd.WorldClockTime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by SHAWON on 6/21/2019.
 */

public class WorldClockTimeActivity extends AppCompatActivity {

    private CustomAnalogClock customAnalogClock;
    private Spinner spinner;
    private TextView txtTimeZoneTime;
    private long ms;
    private ArrayAdapter<String> idAdapter;
    private SimpleDateFormat sdf;
    private Date resultDate;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_clock_time);

        spinner = (Spinner) findViewById(R.id.spinner);
        txtTimeZoneTime = (TextView) findViewById(R.id.txt_zone_time);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);

        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.init(WorldClockTimeActivity.this,R.drawable.clock_face,R.drawable.hour_hand,R.drawable.minute_hand,
                0,false,false);

        String[] idArray = TimeZone.getAvailableIDs();
        sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        idAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,idArray);
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(idAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getGMTtime();
                String selectedID = (String) (parent.getItemAtPosition(position));
                TimeZone timeZone = TimeZone.getTimeZone(selectedID);
                ms = ms + timeZone.getRawOffset();
                resultDate = new Date(ms);
                txtTimeZoneTime.setText(""+sdf.format(resultDate));
                customAnalogClock.setTime(ms);
                ms = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getGMTtime() {
        Calendar calendar = Calendar.getInstance();
        ms = calendar.getTimeInMillis();
        TimeZone timeZone = calendar.getTimeZone();
        int offset = timeZone.getRawOffset();
        if(timeZone.inDaylightTime(new Date())){
            offset += timeZone.getDSTSavings();
        }
        ms -= offset;
    }
}