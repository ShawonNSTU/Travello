package com.example.shawon.travelbd.ModelClass;

import java.util.List;

/**
 * Created by SHAWON on 9/28/2018.
 */

public class Photo {

    private String caption;
    private String uploaded_date;
    private String image_url;
    private String photo_id;
    private String user_id;
    private String location;
    private String rating;
    private List<String> tagged_user_id;

    public Photo() {

    }

    public Photo(String caption, String uploaded_date, String image_url, String photo_id, String user_id, String location, String rating, List<String> tagged_user_id) {
        this.caption = caption;
        this.uploaded_date = uploaded_date;
        this.image_url = image_url;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.location = location;
        this.rating = rating;
        this.tagged_user_id = tagged_user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public void setUploaded_date(String uploaded_date) {
        this.uploaded_date = uploaded_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<String> getTagged_people() {
        return tagged_user_id;
    }

    public void setTagged_people(List<String> tagged_user_id) {
        this.tagged_user_id = tagged_user_id;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", uploaded_date='" + uploaded_date + '\'' +
                ", image_url='" + image_url + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", location='" + location + '\'' +
                ", rating='" + rating + '\'' +
                ", tagged_people=" + tagged_user_id +
                '}';
    }
}