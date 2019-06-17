package com.example.shawon.travelbd.UserStatus;

/**
 * Created by SHAWON on 6/15/2019.
 */

public class Leaderboard {

    private String user_id;
    private String profile_photo;
    private String points;
    private String username;

    public Leaderboard() {

    }

    public Leaderboard(String user_id, String profile_photo, String points, String username) {
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.points = points;
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Leaderboard{" +
                "user_id='" + user_id + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", points='" + points + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}