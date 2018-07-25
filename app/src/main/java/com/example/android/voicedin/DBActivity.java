package com.example.android.voicedin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.voicedin.ConvAnalyticsUtils.SentimentAnalysisResult;
import com.example.android.voicedin.utils.DBManager;

import java.util.UUID;

public class DBActivity extends AppCompatActivity{

    private TextView userId;
    private TextView name;
    private TextView url;
    private TextView latitude;
    private TextView longitude;

    private Button newUser;
    private Button updateName;
    private Button updateURL;
    private Button updateLat;
    private Button updateLong;

    private EditText newName;
    private EditText newURL;
    private EditText newLat;
    private EditText newLong;

    DBManager dbMan = new DBManager();

    private String placeholderUserId = "754FDF28-3D4E-4424-80C5-A9756D04352C";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        userId = findViewById(R.id.UserId_TextView);
        name = findViewById(R.id.Name_TextView);
        url = findViewById(R.id.URL_TextView);
        latitude = findViewById(R.id.Lat_TextView);
        longitude = findViewById(R.id.Long_TextView);

        newUser = findViewById(R.id.NewUser_Button);
        updateName = findViewById(R.id.UpdateName_Button);
        updateURL = findViewById(R.id.UpdateURL_Button);
        updateLat =  findViewById(R.id.UpdateLat_Button);
        updateLong = findViewById(R.id.UpdateLong_Button);

        newName = findViewById(R.id.Name_EditText);
        newURL = findViewById(R.id.URL_EditText);
        newLat = findViewById(R.id.Lat_EditText);
        newLong = findViewById(R.id.Long_EditText);

        //create new user button
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String newUserId = dbMan.generateUSERID().toString();
                    userId.setText(newUserId);
                    dbMan.createNewUser(
                            newUserId,
                            newName.toString(),
                            newURL.toString(),
                            UUID.randomUUID().toString(),
                            Float.parseFloat(newLat.toString()),
                            Float.parseFloat(newLong.toString()));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbMan.updateName(newName.toString(), placeholderUserId);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        updateURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbMan.updateURL(newURL.toString(), placeholderUserId);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        updateLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbMan.updateLat(newLat.toString(), placeholderUserId);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        updateLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbMan.updateLong(newLong.toString(), placeholderUserId);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


}
