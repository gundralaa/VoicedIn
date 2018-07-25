package com.example.android.voicedin;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = 0;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        int permission = 1;
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

        (ImageView)findViewById(R.id.recordButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AudioRecordingUtils.startRecordingMP3();
                } catch (Exception e){
                    e.printStackTrace();
                }
                runEnrollmentTask(user);

                mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
                mProgressBar.setProgress(i);
                mCountDownTimer = new CountDownTimer(5000,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.v("Log_tag", "Tick of Progress"+ i + millisUntilFinished);
                        i++;
                        mProgressBar.setProgress((int)i*100/(5000/1000));
                    }

                    @Override
                    public void onFinish() { //TODO: do something to go to next activity
                        i++;
                        mProgressBar.setProgress(100);
                        try {
                            AudioRecordingUtils.stopRecordingMP3();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        convertToWave();
                        Intent intent = new Intent(VoiceActivity.this, StartRecordActivity.class);
                        startActivity(intent);
                    }
                };
                mCountDownTimer.start();
            }
        });
    }
}
