package com.example.shawon.travelbd.Weather;

/**
 * Created by SHAWON on 6/11/2019.
 */

public class Wind {

    private double speed;

    private double deg;

    public Wind(double speed, double deg) {
        this.speed = speed;
        this.deg = deg;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }
}