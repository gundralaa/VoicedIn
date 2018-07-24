package com.example.android.voicedin.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.android.voicedin.User;

import com.microsoft.cognitiveservices.speech.AudioInputStreamFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by abhin on 7/24/2018.
 */

public class SpeakerRecognitionUtils {


    private final static String SUBSCRIPTION_KEY = "";
    private final static String LOCALE = "en-US";
    private static SpeakerIdentificationRestClient client = null;
    private static Activity context;
    private static TextView view;
    private final static int SAMPLE_RATE = 16000;
    private AudioRecord recorder;

    private class EnrollmentTask extends AsyncTask<User, Void, User>{
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
        }
    }

    private class AudioEnrollmentTask extends AsyncTask<User, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(User... users) {
            UUID voiceId = users[0].getVoiceID();
            //client.enroll(stream, voiceId)
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }





}
