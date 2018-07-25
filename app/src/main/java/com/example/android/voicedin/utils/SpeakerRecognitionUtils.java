package com.example.android.voicedin.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.android.voicedin.Location;
import com.example.android.voicedin.User;

import com.example.android.voicedin.helper_classes.PersistentDataBase;
import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.identification.CreateProfileResponse;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Identification;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.microsoft.cognitiveservices.speech.AudioInputStreamFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static java.lang.System.in;

/**
 * Created by abhin on 7/24/2018.
 */

public class SpeakerRecognitionUtils {


    private final static String SUBSCRIPTION_KEY = "9c497683c5cf4c4b872058baae39564e";
    private final static String LOCALE = "en-US";
    private static SpeakerIdentificationRestClient client = null;
    private static Activity context;
    private static TextView view;
    private static TextView nameView;
    private final static int SAMPLE_RATE = 16000;
    private AudioRecord recorder;
    private static User userIn = null;


    private static UUID userId = null;

    private static OperationLocation statLocation = null;

    public static UUID getUserId() {
        return userId;
    }

    public static void setUserId(UUID userId) {
        SpeakerRecognitionUtils.userId = userId;
    }

    public static void setNameView(TextView nameView) {
        SpeakerRecognitionUtils.nameView = nameView;
    }

    public static void setView(TextView view) {
        SpeakerRecognitionUtils.view = view;
    }

    public static class EnrollmentTask extends AsyncTask<User, Void, User>{
        @Override
        protected void onPreExecute() {
            client = new SpeakerIdentificationRestClient(SUBSCRIPTION_KEY);
            super.onPreExecute();
        }

        @Override
        protected User doInBackground(User... users) {
            CreateProfileResponse clientProfile = null;
            try {
                 clientProfile = client.createProfile(LOCALE);
            }catch (Exception e){
                e.printStackTrace();
            }
            users[0].setVoiceID(clientProfile.identificationProfileId);
            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            view.setText(user.getVoiceID().toString());
            Log.d(TAG, user.getVoiceID() + "");
            userIn = user;
            userId = user.getVoiceID();
            PersistentDataBase.getUsers().set(4, user);
        }
    }

    public static class AudioEnrollmentTask extends AsyncTask<String, Void, User>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected User doInBackground(String... strings) {
            UUID voiceId = userIn.getVoiceID();
            try{
                FileInputStream in = new FileInputStream(strings[0]);
                client.enroll(in, voiceId,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userIn;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
        }
    }

    public static void runRecognitionTask(String filepath){new RecognitionTask().execute(filepath);}
    public static class RecognitionTask extends AsyncTask<String, Void, OperationLocation>{
        @Override
        protected void onPreExecute() {
            client = new SpeakerIdentificationRestClient(SUBSCRIPTION_KEY);
            super.onPreExecute();
        }

        @Override
        protected OperationLocation doInBackground(String... strings) {
            OperationLocation statusUrl = null;
            try{
                FileInputStream in = new FileInputStream(strings[0]);
                statusUrl = client.identify(in, (List<UUID>) PersistentDataBase.getIds(),true);
            } catch (Exception e){
                e.printStackTrace();
            }
            return statusUrl;
        }

        @Override
        protected void onPostExecute(OperationLocation location) {
            super.onPostExecute(location);
            statLocation = location;
            runOperationCheckInTask(statLocation);
        }

    }

    private static void runOperationCheckInTask(OperationLocation location){new OperationCheckInTask().execute(location);}
    private static class OperationCheckInTask extends AsyncTask<OperationLocation, Void, UUID>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected UUID doInBackground(OperationLocation... locations) {
            Identification id = null;
            try{
                IdentificationOperation operation = client.checkIdentificationStatus(locations[0]);
                id = operation.processingResult;
            } catch (Exception e){
                e.printStackTrace();
            }

            UUID vId = null;
            if(id == null){
                this.cancel(true);
                runOperationCheckInTask(statLocation);
            } else {
                vId = id.identifiedProfileId;
            }
            return vId;
        }

        @Override
        protected void onPostExecute(UUID id) {
            super.onPostExecute(id);
            int i = PersistentDataBase.getIds().indexOf(id);
            Log.d(TAG, "Name of User: " + PersistentDataBase.getUsers().get(i).getName() );
            nameView.setText(PersistentDataBase.getUsers().get(i).getName());

        }
    }





}
