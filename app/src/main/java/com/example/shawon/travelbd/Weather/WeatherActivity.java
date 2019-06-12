package com.example.shawon.travelbd.Weather;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shawon.travelbd.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

/**
 * Created by SHAWON on 6/11/2019.
 */

public class WeatherActivity extends AppCompatActivity {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius, txtCountry, txtWindSpeed, txtSunTime;
    TextView Wind, Humidity, Time, SunTime, update;
    ImageView imageView, backArrow;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    private String latitude = "";
    private String longitude = "";
    private String place_name= "";
    private String country_name = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Control
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);
        txtCountry = (TextView) findViewById(R.id.txtCountry);
        txtWindSpeed = (TextView) findViewById(R.id.txtWindSpeed);
        Wind = (TextView) findViewById(R.id.Wind);
        Humidity = (TextView) findViewById(R.id.Humidity);
        Time = (TextView) findViewById(R.id.Time);
        SunTime = (TextView) findViewById(R.id.SunTime);
        txtSunTime = (TextView) findViewById(R.id.txtSunTime);
        update = (TextView) findViewById(R.id.update);
        backArrow = (ImageView) findViewById(R.id.backArrow);

        // get bundle from previous activity...
        if (getIntent().hasExtra(getString(R.string.bundle))) {

            String getIntent = getIntent().getStringExtra(getString(R.string.bundle));
            if (getIntent.length() != 0) {
                int line = 0;
                String s = "";
                for (int i = 0; i < getIntent.length(); i++) {
                    if (getIntent.charAt(i) == '|') {
                        ++line;
                        if (line == 1) {
                            place_name = s;
                            s = "";
                        }
                        else if(line == 2){
                            country_name = s;
                            s = "";
                        }
                        else if(line == 3){
                            latitude = s;
                            s = "";
                        }
                    } else {
                        s += getIntent.charAt(i);
                    }
                }
                longitude = s;
            }
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new GetWeather().execute(WeatherCommon.apiRequest(latitude,longitude));

        }

    private class GetWeather extends AsyncTask<String,Void,String> {
        ProgressDialog pd = new ProgressDialog(WeatherActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error: Not found city")){
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();

            txtCity.setText(String.format("%s",place_name));
            txtCountry.setText(String.format("%s",country_name));
            txtLastUpdate.setText(String.format("%s", WeatherCommon.getDateNow()));

            String d = openWeatherMap.getWeather().get(0).getDescription();
            String f = d.substring(0,1).toUpperCase()+d.substring(1);

            Humidity.setVisibility(View.VISIBLE);
            Wind.setVisibility(View.VISIBLE);
            Time.setVisibility(View.VISIBLE);
            SunTime.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);

            txtDescription.setText(String.format("%s",f));
            txtHumidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s",WeatherCommon.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
            txtSunTime.setText(String.format("%s",WeatherCommon.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));
            txtWindSpeed.setText(String.format("%.2f m/s",openWeatherMap.getWind().getSpeed()));
            Picasso.get().load(R.drawable.ic_cloud).into(imageView);

        }

    }

}