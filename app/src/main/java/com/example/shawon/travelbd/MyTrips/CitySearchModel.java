package com.example.shawon.travelbd.MyTrips;

import android.os.Parcel;
import android.os.Parcelable;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by SHAWON on 6/22/2019.
 */

public class CitySearchModel implements Searchable, Parcelable {

    private String mName;
    private String mImage;

    public CitySearchModel(String name, String image) {
        mName = name;
        mImage = image;
    }

    protected CitySearchModel(Parcel in) {
        mName = in.readString();
        mImage = in.readString();
    }

    public static final Creator<CitySearchModel> CREATOR = new Creator<CitySearchModel>() {
        @Override
        public CitySearchModel createFromParcel(Parcel in) {
            return new CitySearchModel(in);
        }

        @Override
        public CitySearchModel[] newArray(int size) {
            return new CitySearchModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mImage);
    }

    @Override
    public String getTitle() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public CitySearchModel setName(String name) {
        mName = name;
        return this;
    }

    public String getImage() {
        return mImage;
    }
}