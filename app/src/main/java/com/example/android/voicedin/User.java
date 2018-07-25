package com.example.android.voicedin;

import java.util.UUID;

public class User {
    private String name = "";
    private String linkedInURL = "";
    String userID;
    UUID voiceID;

    //constructor
    public User(String name, String linkedInURL, String userID, UUID voiceID){
        this.name = name;
        this.linkedInURL = linkedInURL;
        this.userID = userID;
        this.voiceID = voiceID;

    }

    //getters
    public String getName(){
        return name;
    }
    public String getLinkedInURL(){
        return linkedInURL;
    }
    public String getUserID(){
        return userID;
    }
    public UUID getVoiceID(){
        return voiceID;
    }


    //setters
    public void setName(String name){
        this.name = name;
    }
    public void setLinkedInURL(String linkedInURL){
        this.linkedInURL = linkedInURL;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
    public void setVoiceID(UUID voiceID) {
        this.voiceID = voiceID;
    }

}
