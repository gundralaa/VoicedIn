package com.example.android.voicedin.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

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
            "INSERT INTO Users(UserId, Name, LinkedInUrl, VoiceId, LocationLatitude, LocationLongitude)" +
                    "VALUES(?, ?, ?, ?, ?, ?);";
    private PreparedStatement insertNewUserStatement;

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
    private PreparedStatement retrieveLongitdueStatement;


    //TRANSACTIONS
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
    private PreparedStatement beginTransactionStatement;

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";
    private PreparedStatement commitTransactionStatement;

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
    private PreparedStatement rollbackTransactionStatement;


    //connect to SQL DB
    public SQLConnection(String configFilename) {
        this.configFilename = configFilename;
    }

    public void openConnection() throws Exception {
        configProps.load(new FileInputStream(configFilename));

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

        retrieveNameStatement = connection.prepareStatement(RETRIEVE_NAME_FROM_USERID);
        retrieveLinkedInURLStatement = connection.prepareStatement(RETRIEVE_LINKEDINURL_FROM_USERID);
        retrieveLatitudeStatement = connection.prepareStatement(RETRIEVE_LATITUDE_FROM_USERID);
        retrieveLongitdueStatement = connection.prepareStatement(RETRIEVE_LONGITUDE_FROM_USERID);


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
    public String retrieve_location(UUID UserId) throws Exception {
        retrieveLocationStatement.clearParameters();
        retrieveLocationStatement.setString(1, UserId.toString());
        retrieveLocationStatement.setString(2, UserId.toString());

        ResultSet locationResult = retrieveLocationStatement.executeQuery();
        return locationResult.toString();
    }

    public void update_location(float Latitude, float Longitude, UUID UserId) throws Exception {
        updateLocationStatement.clearParameters();
        updateLocationStatement.setFloat(1, Latitude);
        updateLocationStatement.setFloat(2, Longitude);
        updateLocationStatement.setString(3, UserId.toString());

        ResultSet locationResult = updateLocationStatement.executeQuery();
    }

    public String get_voice_from_user(UUID UserId) throws Exception {
        retrieveVoiceIdFromUserStatement.clearParameters();
        retrieveVoiceIdFromUserStatement.setString(1, UserId.toString());

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
            (UUID UserId, String Name, String LinkedInURL, UUID VoiceId,
             float LocationLatitude, float LocationLongitude) throws Exception {
        insertNewUserStatement.clearParameters();
        insertNewUserStatement.setString(1, UserId.toString());
        insertNewUserStatement.setString(2, Name);
        insertNewUserStatement.setString(3, LinkedInURL);
        insertNewUserStatement.setString(4, VoiceId.toString());
        insertNewUserStatement.setFloat(5, LocationLatitude);
        insertNewUserStatement.setFloat(6, LocationLongitude);

        ResultSet populationResult = insertNewUserStatement.executeQuery();

    }

    public ArrayList<String> retrieve_all_user_info(UUID UserId)throws Exception{
        retrieveNameStatement.clearParameters();
        retrieveNameStatement.setString(1, UserId.toString());

        retrieveLinkedInURLStatement.clearParameters();
        retrieveLinkedInURLStatement.setString(1, UserId.toString());

        retrieveVoiceIdFromUserStatement.clearParameters();
        retrieveVoiceIdFromUserStatement.setString(1, UserId.toString());

        retrieveLatitudeStatement.clearParameters();
        retrieveLatitudeStatement.setString(1, UserId.toString());

        retrieveLongitdueStatement.clearParameters();
        retrieveLongitdueStatement.setString(1, UserId.toString());

        ResultSet nameResult = retrieveNameStatement.executeQuery();
        ResultSet urlResult = retrieveLinkedInURLStatement.executeQuery();
        ResultSet voiceResult = retrieveVoiceIdFromUserStatement.executeQuery();
        ResultSet latResult = retrieveLatitudeStatement.executeQuery();
        ResultSet longResult = retrieveLongitdueStatement.executeQuery();

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
