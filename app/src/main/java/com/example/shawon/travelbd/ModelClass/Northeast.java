package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 1/17/2019.
 */

public class Northeast {

    private String lng;

    private String lat;

    public String getLng () {
        return lng;
    }

    public void setLng (String lng) {
        this.lng = lng;
    }

    public String getLat () {
        return lat;
    }

    public void setLat (String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "lng = "+lng+", lat = "+lat;
    }

}