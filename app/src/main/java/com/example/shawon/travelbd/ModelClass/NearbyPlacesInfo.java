package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 1/15/2019.
 */

public class NearbyPlacesInfo {

    private String nearby_place_name;
    private String nearby_place_image;

    public NearbyPlacesInfo(String nearby_place_name, String nearby_place_image) {
        this.nearby_place_name = nearby_place_name;
        this.nearby_place_image = nearby_place_image;
    }

    public String getNearby_place_name() {
        return nearby_place_name;
    }

    public String getNearby_place_image() {
        return nearby_place_image;
    }
}
