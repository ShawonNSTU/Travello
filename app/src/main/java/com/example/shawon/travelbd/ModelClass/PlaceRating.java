package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/7/2019.
 */

public class PlaceRating {

    private String user_id;
    private String place_id;
    private String rating_value;
    private String review;
    private String name;
    private String profile_picture;

    public PlaceRating() {
    }

    public PlaceRating(String user_id, String place_id, String rating_value, String review, String name, String profile_picture) {
        this.user_id = user_id;
        this.place_id = place_id;
        this.rating_value = rating_value;
        this.review = review;
        this.name = name;
        this.profile_picture = profile_picture;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getRating_value() {
        return rating_value;
    }

    public void setRating_value(String rating_value) {
        this.rating_value = rating_value;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @Override
    public String toString() {
        return "PlaceRating{" +
                "user_id='" + user_id + '\'' +
                ", place_id='" + place_id + '\'' +
                ", rating_value='" + rating_value + '\'' +
                ", review='" + review + '\'' +
                ", name='" + name + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                '}';
    }
}