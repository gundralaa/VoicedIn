package com.example.android.voicedin;

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
    private boolean isRecording = false;
    MediaRecorder recorder;
    String filePath = "recording.3gp";
    int recordingNumber = 1;
    public static final int RequestPermissionCode = 1;
    boolean voiceCommandStart = false; //start recording with voice command
    boolean voiceCommandStop = false; //stop recording with voice command
    private final String[] startCommands = {"Hi", "Hello", "Hey"};
    private final String[] stopCommands = {"Bye", "Goodbye", "See you"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);
    }

    public void checkVoiceCommands(String transcript) { //TODO: figure out cases for containng both start and stop commands in increment
        if(containsStartCommand(transcript)) { //if transcript contains a start command, start recording
            voiceCommandStart = true;
        } else {
            voiceCommandStart = false;
        }
        if(containsStopCommand(transcript)){ //if transcript contains a stop command, stop recording
            voiceCommandStop = true;
        } else {
            voiceCommandStop = false;
        }
    }

    public boolean containsStartCommand(String transcript) {
        for(String startCommand : startCommands) {
            if(transcript.contains(startCommand))
                return true;
        }
        return false;
    }

    public boolean containsStopCommand(String transcript) {
        for(String stopCommand : stopCommands) {
            if(transcript.contains(stopCommand))
                return true;
        }
        return false;
    }

    public void startStopRecording(View view) throws IOException, IllegalStateException {
        Button recordingButton = (Button)view.findViewById(R.id.recordingButton);
        if(isRecording){ //if recording is started, stop recording
            recorder.stop();
            //recorder.release();
            recordingNumber++;
            ((EditText)findViewById(R.id.recordedTranscript)).setText("Woah it's a transcript"); //TODO: replace with method call to speech to text API
            isRecording = false;
        } else{ //if recording is stopped, start recording
            ActivityCompat.requestPermissions(StartRecordActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + recordingNumber + ".3gp";
            prepareMediaRecorder();
            recorder.prepare();
            recorder.start();
            recordingButton.setText("Stop Recording");
            ((EditText)findViewById(R.id.recordedTranscript)).setText("");
            isRecording = true;
        }
    }

    public void prepareMediaRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(filePath);
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
}
