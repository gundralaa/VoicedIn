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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.voicedin.helper_classes.CustomLocationListener;
import com.example.android.voicedin.utils.AudioRecordingUtils;
import com.example.android.voicedin.utils.SpeakerRecognitionUtils;
import com.example.android.voicedin.utils.SpeechToTextUtils;
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
import android.media.*;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class StartRecordActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;

    private static final String TAG = "StartRecordActivity" ;
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView gpsView;
    TextView speechView;
    Button recordingButton;
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);

        // Start Location Management Activities
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        gpsView = (TextView) findViewById(R.id.gps_view);
        speechView = (TextView) findViewById(R.id.speech_text_view);
        recordingButton = (Button) findViewById(R.id.recordingButton);

        /*
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (!gpsEnabled) {
            enableLocationSettings();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "System Permissions not Present");
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
            LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            CustomLocationListener listener = new CustomLocationListener();
            listener.setGpsView(gpsView);
            locationManager.requestLocationUpdates(provider.getName(), 100000, 10, listener);
        }
        */


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
        //AudioRecordingUtils.setRecordingButton(recordingButton);
        User user = new User("John","hi",1,null);
        SpeechToTextUtils.continuousSpeechCollect(recordingButton, this);
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
}
