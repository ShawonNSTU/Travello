package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 7/29/2018.
 */

public class UserPublicInfo {

    private String username;
    private String hometown;
    private long posts;
    private long followers;
    private long following;
    private String profile_photo;
    private long number_of_travelled_places;

    public UserPublicInfo() {

    }

    public UserPublicInfo(String username, String hometown, long posts, long followers, long following,
                          String profile_photo, long number_of_travelled_places) {
        this.username = username;
        this.hometown = hometown;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.profile_photo = profile_photo;
        this.number_of_travelled_places = number_of_travelled_places;
    }

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
                '}';
    }
}
