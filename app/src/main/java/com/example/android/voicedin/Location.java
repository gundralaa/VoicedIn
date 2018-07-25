package com.example.android.voicedin;

public class Location {
    private double longitude = 0;
    private double latitude = 0;
    //constructor
    public Location(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    //getters
    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    //setters
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
}