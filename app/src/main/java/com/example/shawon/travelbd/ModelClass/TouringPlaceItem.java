package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 6/6/2019.
 */

public class TouringPlaceItem {

    private String name;
    private String image;
    private String description;
    private String location;
    private String how_to_go;
    private String where_to_stay;
    private String wikipedia;
    private String type;
    private String district_id;

    public TouringPlaceItem() {
    }

    public TouringPlaceItem(String name, String image, String description, String location,
                            String how_to_go, String where_to_stay, String wikipedia,
                            String type, String district_id) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.location = location;
        this.how_to_go = how_to_go;
        this.where_to_stay = where_to_stay;
        this.wikipedia = wikipedia;
        this.type = type;
        this.district_id = district_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHow_to_go() {
        return how_to_go;
    }

    public void setHow_to_go(String how_to_go) {
        this.how_to_go = how_to_go;
    }

    public String getWhere_to_stay() {
        return where_to_stay;
    }

    public void setWhere_to_stay(String where_to_stay) {
        this.where_to_stay = where_to_stay;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    @Override
    public String toString() {
        return "TouringPlaceItem{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", how_to_go='" + how_to_go + '\'' +
                ", where_to_stay='" + where_to_stay + '\'' +
                ", wikipedia='" + wikipedia + '\'' +
                ", type='" + type + '\'' +
                ", district_id='" + district_id + '\'' +
                '}';
    }
}