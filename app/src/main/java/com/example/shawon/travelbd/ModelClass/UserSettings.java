package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/2/2019.
 */

public class UserSettings {

    private UserPersonalInfo userPersonalInfo;
    private UserPublicInfo userPublicInfo;

    public UserSettings(UserPersonalInfo userPersonalInfo, UserPublicInfo userPublicInfo) {
        this.userPersonalInfo = userPersonalInfo;
        this.userPublicInfo = userPublicInfo;
    }

    public UserSettings() {

    }


    public UserPersonalInfo getUserPersonalInfo() {
        return userPersonalInfo;
    }

    public void setUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
        this.userPersonalInfo = userPersonalInfo;
    }

    public UserPublicInfo getUserPublicInfo() {
        return userPublicInfo;
    }

    public void setUserPublicInfo(UserPublicInfo userPublicInfo) {
        this.userPublicInfo = userPublicInfo;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "userPersonalInfo=" + userPersonalInfo +
                ", userPublicInfo=" + userPublicInfo +
                '}';
    }

}
