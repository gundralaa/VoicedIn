package com.example.android.voicedin.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

// Use the JDBC driver
import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;

import javax.xml.transform.Result;

public class SQLConnection {

    private String configFilename;
    private Properties configProps = new Properties();

    private Connection connection;

    private ArrayList<String> userInfo;

    //DB Credentials
    private String jSQLDriver;
    private String jSQLUrl;
    private String jSQLUser;
    private String jSQLPassword;

    //CANNED QUERIES
    private static final String RETRIEVE_VALID_USERID_SQL =
            "SELECT TOP UserId FROM Customers WHERE Name = ?";
    private PreparedStatement retrieveValidCidStatement;

    private static final String RETRIEVE_LOCATION =
            "SELECT LocationLatitude, LocationLongitude FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveLocationStatement;

    private static final String UPDATE_LOCATION_SQL =
            "UPDATE Users" +
                    "SET LocationLatitude = ?, LocationLongitude = ?" +
                    "WHERE UserId = ?;";
    private PreparedStatement updateLocationStatement;

    private static final String RETRIEVE_VOICEID_FROM_USERID_SQL =
            "SELECT VoiceId FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveVoiceIdFromUserStatement;

    private static final String RETRIEVE_USERID_FROM_VOICE_SQL =
            "SELECT UserId FROM Users WHERE VoiceId = ?;";
    private PreparedStatement retrieveUserIdFromVoiceIdStatement;

    private static final String INSERT_NEW_USER_SQL =
            "INSERT INTO Users(Name, LinkedInUrl, VoiceId, LocationLatitude, LocationLongitude)" +
                    "VALUES(?, ?, ?, ?, ?, ?) WHERE UserID = ?;";
    private PreparedStatement insertNewUserStatement;


    private static final String UPDATE_NAME =
            "UPDATE Users" +
                    "SET Name = ?" +
                    "WHERE UserId = ?;";
    private PreparedStatement updateNameStatement;

    private static final String UPDATE_LINKEDINURL =
            "UPDATE Users" +
                    "SET LinkedInURL = ?" +
                    "WHERE UserId = ?;";
    private PreparedStatement updateLinkedInURLStatement;

    private static final String UPDATE_LATITUDE =
            "UPDATE Users" +
                    "SET LocationLatitude = ?" +
                    "WHERE UserId = ?;";
    private PreparedStatement updateLatitudeStatement;

    private static final String UPDATE_LONGITUDE =
            "UPDATE Users" +
                    "SET LocationLatitude = ?" +
                    "WHERE UserId = ?;";
    private PreparedStatement updateLongitudeStatement;



    private static final String RETRIEVE_ALL_USER_INFO =
            "SELECT * FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveAllUserInfoStatement;

    private static final String RETRIEVE_NAME_FROM_USERID =
            "SELECT Name FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveNameStatement;

    private static final String RETRIEVE_LINKEDINURL_FROM_USERID =
            "SELECT LinkedInURL FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveLinkedInURLStatement;

    private static final String RETRIEVE_LATITUDE_FROM_USERID =
            "SELECT LocationLatitude FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveLatitudeStatement;

    private static final String RETRIEVE_LONGITUDE_FROM_USERID =
            "SELECT LocationLongitude FROM Users WHERE UserId = ?;";
    private PreparedStatement retrieveLongitudeStatement;


    //TRANSACTIONS
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
    private PreparedStatement beginTransactionStatement;

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";
    private PreparedStatement commitTransactionStatement;

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
    private PreparedStatement rollbackTransactionStatement;


    //connect to SQL DB
    public SQLConnection() {
        try {
            ActualOpenConnection();
            prepareStatements();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ActualOpenConnection() {
        String connectionString =
                "jdbc:sqlserver://hack-sqlserver.database.windows.net:1433;"
                        + "database=hackathon-db;"
                        + "user=hackuser@hack-sqlserver;"
                        + "password=microHack18;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "hostNameInCertificate=*.database.windows.net;"
                        + "loginTimeout=30;";

        // Declare the JDBC objects.
        this.connection = null;

        try {
            this.connection = DriverManager.getConnection(connectionString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) try { connection.close(); } catch(Exception e) {}
        }
    }

    /* I think this might not actually work */
    public void OldOpenConnection() throws Exception {

        jSQLDriver = "com.microsoft.sqlserver.jdbc.SWLServerDriver";
        jSQLUrl = "jdbc:sqlserver://hack-sqlserver.database.windows.net;database=hackathon-db";
        jSQLUser = "hackuser@hack-sqlserver";
        jSQLPassword = "microHack18";

        Class.forName(jSQLDriver).newInstance();
        connection = DriverManager.getConnection(jSQLUrl, jSQLUser, jSQLPassword);
        connection.setAutoCommit(true);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    }

    public void closeConnection() throws Exception {
        connection.close();
    }

    public void prepareStatements() throws Exception {
        retrieveValidCidStatement = connection.prepareStatement(RETRIEVE_VALID_USERID_SQL);
        retrieveLocationStatement = connection.prepareStatement(RETRIEVE_LOCATION);
        updateLocationStatement = connection.prepareStatement(UPDATE_LOCATION_SQL);
        retrieveVoiceIdFromUserStatement = connection.prepareStatement(RETRIEVE_VOICEID_FROM_USERID_SQL);
        retrieveUserIdFromVoiceIdStatement = connection.prepareStatement(RETRIEVE_USERID_FROM_VOICE_SQL);

        updateNameStatement = connection.prepareStatement(UPDATE_NAME);
        updateLinkedInURLStatement = connection.prepareStatement(UPDATE_LINKEDINURL);
        updateLatitudeStatement = connection.prepareStatement(UPDATE_LATITUDE);
        updateLongitudeStatement = connection.prepareStatement(UPDATE_LONGITUDE);

        retrieveNameStatement = connection.prepareStatement(RETRIEVE_NAME_FROM_USERID);
        retrieveLinkedInURLStatement = connection.prepareStatement(RETRIEVE_LINKEDINURL_FROM_USERID);
        retrieveLatitudeStatement = connection.prepareStatement(RETRIEVE_LATITUDE_FROM_USERID);
        retrieveLongitudeStatement = connection.prepareStatement(RETRIEVE_LONGITUDE_FROM_USERID);


        insertNewUserStatement = connection.prepareStatement(INSERT_NEW_USER_SQL);
        retrieveAllUserInfoStatement = connection.prepareStatement(RETRIEVE_ALL_USER_INFO);

        beginTransactionStatement = connection.prepareStatement(BEGIN_TRANSACTION_SQL);
        commitTransactionStatement = connection.prepareStatement(COMMIT_SQL);
        rollbackTransactionStatement = connection.prepareStatement(ROLLBACK_SQL);
    }

    /*
    *these are here in case more complicated logic is needed
    * i.e. if userid already exists rollback populate profile transaction
     */
    public void beginTransaction() throws Exception {
        connection.setAutoCommit(false);
        beginTransactionStatement.executeUpdate();
    }

    public void commitTransaction() throws Exception {
        commitTransactionStatement.executeUpdate();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws Exception {
        rollbackTransactionStatement.executeUpdate();
        connection.setAutoCommit(true);
    }


    //DB Management Transactions
    public String retrieve_location(String UserId) throws Exception {
        retrieveLocationStatement.clearParameters();
        retrieveLocationStatement.setString(1, UserId);
        retrieveLocationStatement.setString(2, UserId);

        ResultSet locationResult = retrieveLocationStatement.executeQuery();
        return locationResult.toString();
    }

    public void update_location(float Latitude, float Longitude, String UserId) throws Exception {
        updateLocationStatement.clearParameters();
        updateLocationStatement.setFloat(1, Latitude);
        updateLocationStatement.setFloat(2, Longitude);
        updateLocationStatement.setString(3, UserId);

        ResultSet locationResult = updateLocationStatement.executeQuery();
    }

    public String get_voice_from_user(String UserId) throws Exception {
        retrieveVoiceIdFromUserStatement.clearParameters();
        retrieveVoiceIdFromUserStatement.setString(1, UserId);

        ResultSet voiceResult = retrieveVoiceIdFromUserStatement.executeQuery();
        return voiceResult.toString();
    }

    public String get_user_from_voice(UUID VoiceId) throws Exception {
        retrieveUserIdFromVoiceIdStatement.clearParameters();
        retrieveUserIdFromVoiceIdStatement.setString(1, VoiceId.toString());

        ResultSet userResult = retrieveUserIdFromVoiceIdStatement.executeQuery();
        return userResult.toString();
    }

    public void populate_profile
            (String UserId, String Name, String LinkedInURL, String VoiceId,
             float LocationLatitude, float LocationLongitude) throws Exception {
        insertNewUserStatement.clearParameters();
        insertNewUserStatement.setString(1, UserId);
        insertNewUserStatement.setString(2, Name);
        insertNewUserStatement.setString(3, LinkedInURL);
        insertNewUserStatement.setString(4, VoiceId);
        insertNewUserStatement.setFloat(5, LocationLatitude);
        insertNewUserStatement.setFloat(6, LocationLongitude);

        ResultSet populationResult = insertNewUserStatement.executeQuery();

    }

    public void update_name(String Name, String UserId) throws Exception{
        updateNameStatement.clearParameters();
        updateNameStatement.setString(1, Name);
        updateNameStatement.setString(2, UserId);

        ResultSet nameResult = updateNameStatement.executeQuery();
    }

    public void update_URL(String URL, String UserId) throws Exception{
        updateLinkedInURLStatement.clearParameters();
        updateLinkedInURLStatement.setString(1, URL);
        updateLinkedInURLStatement.setString(2, UserId);

        ResultSet urlResult = updateLinkedInURLStatement.executeQuery();
    }

    public void update_latitude(String lat, String UserId) throws Exception{
        updateLatitudeStatement.clearParameters();
        updateLatitudeStatement.setString(1, lat);
        updateLatitudeStatement.setString(2, UserId);

        ResultSet latResult = updateLatitudeStatement.executeQuery();
    }

    public void update_longitude(String lon, String UserId) throws Exception{
        updateLongitudeStatement.clearParameters();
        updateLongitudeStatement.setString(1, lon);
        updateLongitudeStatement.setString(2, UserId);
    }


    public ArrayList<String> retrieve_all_user_info(String UserId)throws Exception{
        retrieveNameStatement.clearParameters();
        retrieveNameStatement.setString(1, UserId);

        retrieveLinkedInURLStatement.clearParameters();
        retrieveLinkedInURLStatement.setString(1, UserId);

        retrieveVoiceIdFromUserStatement.clearParameters();
        retrieveVoiceIdFromUserStatement.setString(1, UserId);

        retrieveLatitudeStatement.clearParameters();
        retrieveLatitudeStatement.setString(1, UserId);

        retrieveLongitudeStatement.clearParameters();
        retrieveLongitudeStatement.setString(1, UserId);

        ResultSet nameResult = retrieveNameStatement.executeQuery();
        ResultSet urlResult = retrieveLinkedInURLStatement.executeQuery();
        ResultSet voiceResult = retrieveVoiceIdFromUserStatement.executeQuery();
        ResultSet latResult = retrieveLatitudeStatement.executeQuery();
        ResultSet longResult = retrieveLongitudeStatement.executeQuery();

        userInfo.add(nameResult.toString());
        userInfo.add(urlResult.toString());
        userInfo.add(voiceResult.toString());
        userInfo.add(latResult.toString());
        userInfo.add(longResult.toString());

        return userInfo;

        //first try:
        //retrieveAllUserInfoStatement.clearParameters();
        //retrieveAllUserInfoStatement.setString(1, UserId);

        //ResultSet userResult = retrieveAllUserInfoStatement.executeQuery();
    }



}
