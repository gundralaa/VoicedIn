package com.example.android.voicedin.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.android.voicedin.User;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.identification.CreateProfileResponse;
import com.microsoft.cognitiveservices.speech.AudioInputStreamFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final static int SAMPLE_RATE = 16000;
    private AudioRecord recorder;
    private static User userIn = null;

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

    public static class RecognitionTask extends AsyncTask<String,>





}
