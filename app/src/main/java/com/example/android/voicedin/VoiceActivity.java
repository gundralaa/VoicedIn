package com.example.android.voicedin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.voicedin.utils.AudioRecordingUtils;
import com.example.android.voicedin.utils.SpeakerRecognitionUtils;

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

        User user = new User("","",1, null);
        SpeakerRecognitionUtils.setView(textView);
        AudioRecordingUtils.setRecordingButton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioRecordingUtils.isIsRecording()){
                    button.setText("Done");
                    try {
                        AudioRecordingUtils.stopRecording();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    runAudioEnrollmentTask(AudioRecordingUtils.getFilePath());

                } else {
                    try {
                        AudioRecordingUtils.startRecording();
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
}
