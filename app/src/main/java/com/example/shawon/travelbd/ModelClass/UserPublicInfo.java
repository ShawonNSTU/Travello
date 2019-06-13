package com.example.shawon.travelbd.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SHAWON on 7/29/2018.
 */

public class UserPublicInfo implements Parcelable {

    private String username;
    private String hometown;
    private long posts;
    private long followers;
    private long following;
    private String profile_photo;
    private long number_of_travelled_places;
    private String user_id;
    private String latitude;
    private String longitude;

    public UserPublicInfo() {

    }

    public UserPublicInfo(String username, String hometown, long posts, long followers, long following, String profile_photo,
                          long number_of_travelled_places, String user_id, String latitude, String longitude) {
        this.username = username;
        this.hometown = hometown;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.profile_photo = profile_photo;
        this.number_of_travelled_places = number_of_travelled_places;
        this.user_id = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected UserPublicInfo(Parcel in) {
        username = in.readString();
        hometown = in.readString();
        posts = in.readLong();
        followers = in.readLong();
        following = in.readLong();
        profile_photo = in.readString();
        number_of_travelled_places = in.readLong();
        user_id = in.readString();
    }

    public static final Creator<UserPublicInfo> CREATOR = new Creator<UserPublicInfo>() {
        @Override
        public UserPublicInfo createFromParcel(Parcel in) {
            return new UserPublicInfo(in);
        }

        @Override
        public UserPublicInfo[] newArray(int size) {
            return new UserPublicInfo[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public long getNumber_of_travelled_places() {
        return number_of_travelled_places;
    }

    public void setNumber_of_travelled_places(long number_of_travelled_places) {
        this.number_of_travelled_places = number_of_travelled_places;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    @Override
    public String toString() {
        return "UserPublicInfo{" +
                "username='" + username + '\'' +
                ", hometown='" + hometown + '\'' +
                ", posts=" + posts +
                ", followers=" + followers +
                ", following=" + following +
                ", profile_photo='" + profile_photo + '\'' +
                ", number_of_travelled_places=" + number_of_travelled_places +
                ", user_id='" + user_id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(hometown);
        dest.writeLong(posts);
        dest.writeLong(followers);
        dest.writeLong(following);
        dest.writeString(profile_photo);
        dest.writeLong(number_of_travelled_places);
        dest.writeString(user_id);
    }
}