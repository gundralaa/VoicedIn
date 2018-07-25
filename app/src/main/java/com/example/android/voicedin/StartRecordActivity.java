package com.example.android.voicedin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

public class StartRecordActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);

        ImageView recordButton = (ImageView) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    startStopRecording(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
