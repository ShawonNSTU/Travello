package com.example.shawon.travelbd.MyTrips;

/**
 * Created by SHAWON on 7/3/2019.
 */

public class Trip {

    private String trip_name;
    private String trip_start_date;
    private String city_name;
    private String city_image;

    public Trip() {

    }

    public Trip(String trip_name, String trip_start_date, String city_name, String city_image) {
        this.trip_name = trip_name;
        this.trip_start_date = trip_start_date;
        this.city_name = city_name;
        this.city_image = city_image;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public String getTrip_start_date() {
        return trip_start_date;
    }

    public void setTrip_start_date(String trip_start_date) {
        this.trip_start_date = trip_start_date;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_image() {
        return city_image;
    }

    public void setCity_image(String city_image) {
        this.city_image = city_image;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "trip_name='" + trip_name + '\'' +
                ", trip_start_date='" + trip_start_date + '\'' +
                ", city_name='" + city_name + '\'' +
                ", city_image='" + city_image + '\'' +
                '}';
    }
}