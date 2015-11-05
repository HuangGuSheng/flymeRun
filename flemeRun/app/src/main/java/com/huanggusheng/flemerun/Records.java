package com.huanggusheng.flemerun;

/**
 * Created by Huang on 2015/10/24.
 */
public class Records {
    private double distance;
    private double speed;
    private String date;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public String getDate() {
        return date;
    }
}
