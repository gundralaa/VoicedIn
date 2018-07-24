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
    private boolean isRecording;
    MediaRecorder recorder;
    String filePath = "recording.3gp";
    int recordingNumber = 1;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);
    }

    public void startStopRecording(View view) throws IOException, IllegalStateException {
        Button recordingButton = (Button)view.findViewById(R.id.recordingButton);
        if(isRecording){ //if recording is started, stop recording
            recorder.stop();
            //recorder.release();
            recordingNumber++;
            recordingButton.setText("Start Recording");
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
