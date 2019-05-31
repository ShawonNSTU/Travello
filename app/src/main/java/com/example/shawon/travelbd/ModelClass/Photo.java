package com.example.shawon.travelbd.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by SHAWON on 9/28/2018.
 */

public class Photo implements Parcelable {

    private String caption;
    private String uploaded_date;
    private String image_url;
    private String photo_id;
    private String user_id;
    private String location;
    private String rating;
    private String google_places_rating;
    private String tagged_user_id;
    private String tags;
    private List<Like> likes;

    public Photo() {

    }

    public Photo(String caption, String uploaded_date, String image_url, String photo_id, String user_id, String location, String rating,
                 String google_places_rating, String tagged_user_id, String tags,
                 List<Like> likes) {
        this.caption = caption;
        this.uploaded_date = uploaded_date;
        this.image_url = image_url;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.location = location;
        this.rating = rating;
        this.google_places_rating = google_places_rating;
        this.tagged_user_id = tagged_user_id;
        this.tags = tags;
        this.likes = likes;
    }

    protected Photo(Parcel in) {
        caption = in.readString();
        uploaded_date = in.readString();
        image_url = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        location = in.readString();
        rating = in.readString();
        google_places_rating = in.readString();
        tagged_user_id = in.readString();
        tags = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

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

    public String getGoogle_places_rating(){
        return google_places_rating;
    }

    public void setGoogle_places_rating(String google_places_rating){
        this.google_places_rating = google_places_rating;
    }

    public String getTagged_people() {
        return tagged_user_id;
    }

    public void setTagged_people(String tagged_user_id) {
        this.tagged_user_id = tagged_user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
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
                ", google_places_rating='" + google_places_rating + '\'' +
                ", tagged_user_id='" + tagged_user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(uploaded_date);
        dest.writeString(image_url);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(location);
        dest.writeString(rating);
        dest.writeString(google_places_rating);
        dest.writeString(tagged_user_id);
        dest.writeString(tags);
    }
}