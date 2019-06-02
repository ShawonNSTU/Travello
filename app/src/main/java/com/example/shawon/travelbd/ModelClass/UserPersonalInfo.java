package com.example.shawon.travelbd.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SHAWON on 7/28/2018.
 */

public class UserPersonalInfo implements Parcelable {

    private String username;
    private String email;
    private int is_user_email_verified;
    private String gender;
    private String phone_number;
    private String bio;
    private String profile_photo;
    private String password;
    private String user_id;

    public UserPersonalInfo() {

    }

    public UserPersonalInfo(String username, String email, int is_user_email_verified, String gender, String phone_number, String bio,
                            String profile_photo, String password, String user_id) {
        this.username = username;
        this.email = email;
        this.is_user_email_verified = is_user_email_verified;
        this.gender = gender;
        this.phone_number = phone_number;
        this.bio = bio;
        this.profile_photo = profile_photo;
        this.password = password;
        this.user_id = user_id;
    }

    protected UserPersonalInfo(Parcel in) {
        username = in.readString();
        email = in.readString();
        is_user_email_verified = in.readInt();
        gender = in.readString();
        phone_number = in.readString();
        bio = in.readString();
        profile_photo = in.readString();
        password = in.readString();
        user_id = in.readString();
    }

    public static final Creator<UserPersonalInfo> CREATOR = new Creator<UserPersonalInfo>() {
        @Override
        public UserPersonalInfo createFromParcel(Parcel in) {
            return new UserPersonalInfo(in);
        }

        @Override
        public UserPersonalInfo[] newArray(int size) {
            return new UserPersonalInfo[size];
        }
    };

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeInt(is_user_email_verified);
        dest.writeString(gender);
        dest.writeString(phone_number);
        dest.writeString(bio);
        dest.writeString(profile_photo);
        dest.writeString(password);
        dest.writeString(user_id);
    }
}