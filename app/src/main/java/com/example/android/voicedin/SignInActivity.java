package com.example.android.voicedin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android.voicedin.utils.SpeakerRecognitionUtils;

public class SignInActivity extends AppCompatActivity {

    ImageView view;
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        view = (ImageView) findViewById(R.id.introimage);
        //SpeakerRecognitionUtils.executeCleanTask();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startVoice = new Intent(context, VoiceActivity.class);
                startActivity(startVoice);
            }
        });

    }
}
