package com.example.android.voicedin;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.voicedin.helper_classes.RiffHeader;
import com.example.android.voicedin.utils.AudioRecordingUtils;
import com.example.android.voicedin.utils.SpeakerRecognitionUtils;

import java.io.File;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.android.voicedin.StartRecordActivity.RequestPermissionCode;

public class VoiceActivity extends AppCompatActivity {

    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int permission = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, permission);

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

        User user = new User("","",1, null);
        SpeakerRecognitionUtils.setView(textView);
        AudioRecordingUtils.setRecordingButton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioRecordingUtils.isIsRecording()){
                    button.setText("Done");
                    try {
                        AudioRecordingUtils.stopRecordingMP3();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //runAudioEnrollmentTask(AudioRecordingUtils.getFilePath());
                    convertToWave();
                } else {
                    try {
                        AudioRecordingUtils.startRecordingMP3();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    runEnrollmentTask(user);
                    button.setText("Stop");
                }
            }
        });
    }

    public void runEnrollmentTask(User user){new SpeakerRecognitionUtils.EnrollmentTask().execute(user);}
    public void runAudioEnrollmentTask(String filepath){new SpeakerRecognitionUtils.AudioEnrollmentTask().execute(filepath);}

    private void convertToWave(){
        File audioFile = new File(AudioRecordingUtils.getFilePath());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                AudioRecordingUtils.setFilePathWAV();
                runAudioEnrollmentTask(AudioRecordingUtils.getFilePath());
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
