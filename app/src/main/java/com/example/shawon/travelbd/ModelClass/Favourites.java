package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/21/2019.
 */

public class Favourites {

    String place_id;
    String place_name;
    String latitude;
    String longitude;
    String place_rating;
    String total_rating;
    String status;
    String previous_place_latitude;
    String previous_place_longitude;
    String place_image;

    public Favourites() {

    }

    public Favourites(String place_id, String place_name, String latitude, String longitude,
                      String place_rating, String total_rating, String status,
                      String previous_place_latitude, String previous_place_longitude, String place_image) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_rating = place_rating;
        this.total_rating = total_rating;
        this.status = status;
        this.previous_place_latitude = previous_place_latitude;
        this.previous_place_longitude = previous_place_longitude;
        this.place_image = place_image;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlace_rating() {
        return place_rating;
    }

    public void setPlace_rating(String place_rating) {
        this.place_rating = place_rating;
    }

    public String getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(String total_rating) {
        this.total_rating = total_rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrevious_place_latitude() {
        return previous_place_latitude;
    }

    public void setPrevious_place_latitude(String previous_place_latitude) {
        this.previous_place_latitude = previous_place_latitude;
    }

    public String getPrevious_place_longitude() {
        return previous_place_longitude;
    }

    public void setPrevious_place_longitude(String previous_place_longitude) {
        this.previous_place_longitude = previous_place_longitude;
    }

    public String getPlace_image() {
        return place_image;
    }

    public void setPlace_image(String place_image) {
        this.place_image = place_image;
    }

    @Override
    public String toString() {
        return "Favourites{" +
                "place_id='" + place_id + '\'' +
                ", place_name='" + place_name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", place_rating='" + place_rating + '\'' +
                ", total_rating='" + total_rating + '\'' +
                ", status='" + status + '\'' +
                ", previous_place_latitude='" + previous_place_latitude + '\'' +
                ", previous_place_longitude='" + previous_place_longitude + '\'' +
                ", place_image='" + place_image + '\'' +
                '}';
    }
}