package com.example.android.voicedin;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.voicedin.helper_classes.CustomLocationListener;
import com.example.android.voicedin.helper_classes.PersistentDataBase;
import com.example.android.voicedin.utils.AudioRecordingUtils;
import com.example.android.voicedin.utils.SpeakerRecognitionUtils;
import com.example.android.voicedin.utils.SpeechToTextUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.cognitiveservices.speech.SpeechFactory;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.media.*;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.*;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class StartRecordActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;

    private static final String TAG = "StartRecordActivity" ;
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView speechView;
    ImageView recordButton;
    TextView nameView;
    StartRecordActivity context = this;
    ArrayList<FireBaseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);

        speechView = (TextView) findViewById(R.id.speech_text_view);
        recordButton = (ImageView) findViewById(R.id.recordButton);
        nameView = (TextView) findViewById(R.id.textView2);

        speechView.setMovementMethod(new ScrollingMovementMethod());

        //SpeakerRecognitionUtils.initializeUsers();
        getUsers();

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, INTERNET, WRITE_EXTERNAL_STORAGE}, requestCode);

        try {
            // Note: required once after app start.
            SpeechFactory.configureNativePlatformBindingWithDefaultCertificate(this.getCacheDir().getAbsolutePath());
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
        }
        SpeechToTextUtils.setContext(this);
        SpeechToTextUtils.setView(speechView);
        SpeakerRecognitionUtils.setNameView(nameView);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ImageView recordButton = (ImageView) findViewById(R.id.recordButton);
                    recordButton.setImageResource(R.drawable.record_button_stop);

                    AudioRecordingUtils.startRecordingMP3();
                    ((TextView)findViewById(R.id.recordPrompt)).setText("");
                } catch (Exception e){
                    e.printStackTrace();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        AudioRecordingUtils.stopRecordingMP3();
                        convertToWave();

                        SpeechToTextUtils.continuousSpeechCollect(recordButton, context);
                    }
                }, 10000);
            }
        });

    }
    private void getUsers(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mUsers = new ArrayList<>();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        FireBaseUser user = snapshot.getValue(FireBaseUser.class);
                        mUsers.add(user);
                    }
                    SpeakerRecognitionUtils.initializeUsers(mUsers);
                    Log.v("","");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(StartRecordActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StartRecordActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void runEnrollmentTask(User user){new SpeakerRecognitionUtils.EnrollmentTask().execute(user);}

    private void convertToWave(){
        File audioFile = new File(AudioRecordingUtils.getFilePath());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                AudioRecordingUtils.setFilePathWAV();
                SpeakerRecognitionUtils.runRecognitionTask(AudioRecordingUtils.getFilePath());
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                error.printStackTrace();
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(audioFile)
                // Your desired audio format
                .setFormat(AudioFormat.WAV)
                // An callback to know when conversion is finished
                .setCallback(callback)
                // Start conversion
                .convert();
    }
}
