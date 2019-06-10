package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/10/2019.
 */

public class UserInfo {
    String user_id;
    String username;
    String profile_image;

    public UserInfo() {
    }

    public UserInfo(String user_id, String username, String profile_image) {
        this.user_id = user_id;
        this.username = username;
        this.profile_image = profile_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", profile_image='" + profile_image + '\'' +
                '}';
    }
}