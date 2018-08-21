package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 7/28/2018.
 */

public class UserPersonalInfo {

    private String username;
    private String email;
    private int is_user_email_verified;
    private String gender;
    private String phone_number;
    private String bio;
    private String profile_photo;
    private String password;

    public UserPersonalInfo() {

    }

    public UserPersonalInfo(String username, String email, int is_user_email_verified, String gender, String phone_number, String bio, String profile_photo, String password) {
        this.username = username;
        this.email = email;
        this.is_user_email_verified = is_user_email_verified;
        this.gender = gender;
        this.phone_number = phone_number;
        this.bio = bio;
        this.profile_photo = profile_photo;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_user_email_verified() {
        return is_user_email_verified;
    }

    public void setIs_user_email_verified(int is_user_email_verified) {
        this.is_user_email_verified = is_user_email_verified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPersonalInfo{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", is_user_email_verified=" + is_user_email_verified +
                ", gender='" + gender + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", bio='" + bio + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
