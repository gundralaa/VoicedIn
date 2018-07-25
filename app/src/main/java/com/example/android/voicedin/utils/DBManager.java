package com.example.android.voicedin.utils;

import java.util.ArrayList;
import java.util.UUID;

/*
*this class accesses the db and implements functionality created in SQLConnection
* use methods declared below to alter database
* !!!GENERATES USERID!!!
 */
public class DBManager {

    private ArrayList<String> userInfo;
    SQLConnection sqlConnection;
    // config file name

    public DBManager() {
        sqlConnection = new SQLConnection();
    }

    //creates unique UserId, should be called upon creating new account
    public String generateUSERID(){
        UUID userId = UUID.randomUUID();
        return userId.toString();
    }

    //retrieves last known location of user from db
    //for just lat, or just long, parse through array in method retrieveAllUserInfo
    public String retrieveUserLocation(String UserId) throws Exception{
        return sqlConnection.retrieve_location(UserId);
    }

    //updates user location in db
    public void updateUserLocation(String UserId, float Longitude, float Latitude) throws Exception{
        sqlConnection.update_location(Latitude, Longitude, UserId);
    }

    //retrieves voiceId from corresponding UserId
    public String getVoiceFromUserId(String UserId) throws Exception {
        return sqlConnection.get_voice_from_user(UserId);
    }

    //matches user to corresponding VoiceId
    //voiceID UUID generated from voice traning @Kandarp
    public String getUserIdFromVoice(UUID VoiceId) throws Exception{
        return sqlConnection.get_user_from_voice(VoiceId);
    }

    //inserts new user into db
    //this method requires all variables but some of them can be null
    public void createNewUser(String UserId, String Name, String LinkedInURL,
                              String VoiceId, float Latitude, float Longitude) throws Exception{
        sqlConnection.populate_profile(UserId, Name, LinkedInURL, VoiceId, Latitude, Longitude);
    }

    //returns all values from db associated with a specific UserId
    public ArrayList<String> retrieveAllUserInfo(String UserId) throws Exception{
        return sqlConnection.retrieve_all_user_info(UserId);
        /*
        *0: name
        * 1: url
        * 2: voice
        * 3: lat
        * 4: long
         */
    }

    public void updateName(String Name, String UserId) throws Exception{
        sqlConnection.update_name(Name, UserId);
    }

    public void updateURL(String URL, String UserId) throws Exception{
        sqlConnection.update_URL(URL, UserId);
    }

    public void updateLat(String lat, String UserId) throws Exception{
        sqlConnection.update_latitude(lat, UserId);
    }

    public void updateLong(String lon, String UserId) throws Exception{
        sqlConnection.update_longitude(lon, UserId);
    }
}
