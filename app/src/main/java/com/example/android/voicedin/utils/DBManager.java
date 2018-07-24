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
    SQLConnection sqlConnection = new SQLConnection("PLACEHOLDER");//todo: find{
    // config file name

    //creates unique UserId, should be called upon creating new account
    public UUID generateUSERID(){
        UUID userId = UUID.randomUUID();
        return userId;
    }

    //retrieves last known location of user from db
    //for just lat, or just long, parse through array in method retrieveAllUserInfo
    public String retrieveUserLocation(UUID UserId) throws Exception{
        return sqlConnection.retrieve_location(UserId);
    }

    //updates user location in db
    public void updateUserLocation(UUID UserId, float Longitude, float Latitude) throws Exception{
        sqlConnection.update_location(Latitude, Longitude, UserId);
    }

    //retrieves voiceId from corresponding UserId
    public String getVoiceFromUserId(UUID UserId) throws Exception {
        return sqlConnection.get_voice_from_user(UserId);
    }

    //matches user to corresponding VoiceId
    //voiceID UUID generated from voice traning @Kandarp
    public String getUserIdFromVoice(UUID VoiceId) throws Exception{
        return sqlConnection.get_user_from_voice(VoiceId);
    }

    //inserts new user into db
    //this method requires all variables but some of them can be null
    public void createNewUser(UUID UserId, String Name, String LinkedInURL,
                              UUID VoiceId, float Latitude, float Longitude) throws Exception{
        sqlConnection.populate_profile(UserId, Name, LinkedInURL, VoiceId, Latitude, Longitude);
    }

    //returns all values from db associated with a specific UserId
    public ArrayList<String> retrieveAllUserInfo(UUID UserId) throws Exception{
        return sqlConnection.retrieve_all_user_info(UserId);
        /*
        *0: name
        * 1: url
        * 2: voice
        * 3: lat
        * 4: long
         */
    }
}
