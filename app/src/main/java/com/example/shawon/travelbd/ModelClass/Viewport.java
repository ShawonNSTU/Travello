package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 1/17/2019.
 */

public class Viewport {

    private Southwest southwest;

    private Northeast northeast;

    public Southwest getSouthwest () {
        return southwest;
    }

    public void setSouthwest (Southwest southwest) {
        this.southwest = southwest;
    }

    public Northeast getNortheast () {
        return northeast;
    }

    public void setNortheast (Northeast northeast) {
        this.northeast = northeast;
    }

    @Override
    public String toString() {
        return "southwest = "+southwest+", northeast = "+northeast;
    }

}
