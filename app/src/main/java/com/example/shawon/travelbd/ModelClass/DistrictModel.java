package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/5/2019.
 */

public class DistrictModel {
    private String name;
    private String image;

    public DistrictModel() {
    }

    public DistrictModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "DistrictModel{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}