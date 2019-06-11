package com.example.shawon.travelbd.Weather;

/**
 * Created by SHAWON on 6/11/2019.
 */

public class Coord {

    private double lon;

    private double lat;

    public Coord(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void setLon(double lon){
        this.lon = lon;
    }

    public double getLon(){
        return lon;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLat(){
        return lat;
    }

}
