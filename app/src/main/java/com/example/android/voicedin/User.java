package com.example.android.voicedin;

public class User {
    private String name = "";
    private String linkedInURL = "";
    int userID = 0;
    int voiceID = 0;
    double latitude = 0;
    double longitude = 0;

    //constructor
    public User(String name, String linkedInURL, int userID, int voiceID, Location location){
        this.name = name;
        this.linkedInURL = linkedInURL;
        this.userID = userID;
        this.voiceID = voiceID;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    //getters
    public String getName(){
        return name;
    }
    public String getLinkedInURL(){
        return linkedInURL;
    }
    public int getUserID(){
        return userID;
    }
    public int getVoiceID(){
        return voiceID;
    }
    public Location getLocation(){ //shortcut to get location with single method
        return new Location(latitude, longitude);
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }
    public void setLinkedInURL(String linkedInURL){
        this.linkedInURL = linkedInURL;
    }
    public void setUserID(int userID){
        this.userID = userID;
    }
    public void setVoiceID(int voiceID) {
        this.voiceID = voiceID;
    }
    public void setLocation(Location location){ //shortcut to set location with single method
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
}
