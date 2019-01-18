package com.example.shawon.travelbd.ModelClass;

/**
 * Created by SHAWON on 1/17/2019.
 */

public class Geometry {

    private Viewport viewport;

    private Location location;

    public Viewport getViewport () {
        return viewport;
    }

    public void setViewport (Viewport viewport) {
        this.viewport = viewport;
    }

    public Location getLocation () {
        return location;
    }

    public void setLocation (Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "viewport = "+viewport+", location = "+location;
    }

}
