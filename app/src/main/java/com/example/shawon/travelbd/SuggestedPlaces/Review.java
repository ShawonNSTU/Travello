package com.example.shawon.travelbd.SuggestedPlaces;

/**
 * Created by SHAWON on 6/13/2019.
 */

public class Review {

    String author_name;
    String profile_photo_url;
    String rating;
    String relative_time_description;
    String text;

    public Review() {

    }

    public Review(String author_name, String profile_photo_url, String rating, String relative_time_description, String text) {
        this.author_name = author_name;
        this.profile_photo_url = profile_photo_url;
        this.rating = rating;
        this.relative_time_description = relative_time_description;
        this.text = text;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRelative_time_description() {
        return relative_time_description;
    }

    public void setRelative_time_description(String relative_time_description) {
        this.relative_time_description = relative_time_description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Review{" +
                "author_name='" + author_name + '\'' +
                ", profile_photo_url='" + profile_photo_url + '\'' +
                ", rating='" + rating + '\'' +
                ", relative_time_description='" + relative_time_description + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
