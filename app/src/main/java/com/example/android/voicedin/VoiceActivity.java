package com.example.android.voicedin;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.voicedin.helper_classes.PersistentDataBase;
import com.example.android.voicedin.helper_classes.RiffHeader;
import com.example.android.voicedin.utils.AudioRecordingUtils;
import com.example.android.voicedin.utils.SpeakerRecognitionUtils;

import java.io.File;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;


import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.android.voicedin.StartRecordActivity.RequestPermissionCode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONObject;

public class VoiceActivity extends AppCompatActivity {
    private static final String TAG = "VoiceActivity";
    String userURL;
    String firstName;
    String lastName;
    ImageView recordButton;
    TextView textView;
    UUID userId;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = 0;
    final int totalMilliseconds = 30000; //30 seconds
    final int countDownInterval = 1000;

    List<FireBaseUser> mUsers;
    boolean isUserRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int permission = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        recordButton = (ImageView) findViewById(R.id.recordButton);
        textView = (TextView) findViewById(R.id.textView);

        handleLogin();
        getPackageHash();
        //PersistentDataBase.initializeUsers();
        //getUsers().add(new User("","", 5, null));

        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, permission);
        //SpeakerRecognitionUtils.setUserId(userId);

        //handleLogin();

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

        User user = new User( firstName+" "+lastName, userURL,null, null);

        SpeakerRecognitionUtils.setView(textView);
        AudioRecordingUtils.setRecordingButton(recordButton);
        SpeakerRecognitionUtils.setContext(this);
        recordButton.setOnClickListener(new View.OnClickListener() {
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
                mCountDownTimer = new CountDownTimer(totalMilliseconds,countDownInterval) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.v("Log_tag", "Tick of Progress"+ i + millisUntilFinished);
                        i++;
                        mProgressBar.setProgress((int)i*100/(totalMilliseconds/countDownInterval));
                    }

                    @Override
                    public void onFinish() {
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

    public void runEnrollmentTask(User user){new SpeakerRecognitionUtils.EnrollmentTask().execute(user);}
    public void runAudioEnrollmentTask(String filepath){new SpeakerRecognitionUtils.AudioEnrollmentTask().execute(filepath);}

    private void convertToWave(){
        File audioFile = new File(AudioRecordingUtils.getFilePath());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                AudioRecordingUtils.setFilePathWAV();
                SpeakerRecognitionUtils.userIn.setName(firstName+" "+lastName);
                SpeakerRecognitionUtils.userIn.setLinkedInURL(userURL);
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

    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope()//pass the build scope here
                , new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        // Authentication was successful. You can now do
                        // other calls with the SDK.
                        setUpdateState();
                        Toast.makeText(VoiceActivity.this, "Successfully authenticated with LinkedIn.", Toast.LENGTH_SHORT).show();
                        Log.v("Login", "Login successful");
                        fetchPersonalInfo();
                    }


                    @Override
                    public void onAuthError(LIAuthError error) {
                        // Handle authentication errors
                        Log.e("LinkedIn Login", "Auth Error :" + error.toString());
                        setUpdateState();
                        Toast.makeText(VoiceActivity.this, "Failed to authenticate with LinkedIn. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.v("Login", "Login failed");
                    }
                }, true);//if TRUE then it will show dialog if
        // any device has no LinkedIn app installed to download app else won't show anything
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }


    private void fetchPersonalInfo()
    {
        //String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
        String host = "api.linkedin.com";
        String topCardUrl = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url)";


        final APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, topCardUrl, new ApiListener(){
            @Override
            public void onApiSuccess(ApiResponse apiResponse){
                Log.v("Fetch Info","apiResponse:"+apiResponse);
                try {
                    JSONObject json = new JSONObject(apiResponse.getResponseDataAsString());
                    userURL = json.getString("publicProfileUrl");
                    firstName = json.getString("firstName");
                    lastName = json.getString("lastName");


                    //getUsers().set(4, new User(firstName + " " + lastName, userURL,5, null));
                    getUsers();

                    Log.v("LinkedIn link", "userURL"+userURL);
                }
                catch (Exception ex)
                {
                    Log.e("LinkedIn link","failed to get linkedin url");
                }
            }
            @Override
            public void onApiError(LIApiError liApiError){
                Log.v("Fetch Info","api failure response:"+liApiError);

            }
        });
    }

    private void getPackageHash() {
        try {

            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.android.voicedin",//give your package name here
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d(TAG, "Hash  : " + Base64.encodeToString(md.digest(), Base64.NO_WRAP));//Key hash is printing in Log
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }

    }

    private void setUpdateState() {
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();
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
                        if(user.getLinkedinUrl() != null){
                            if(user.getLinkedinUrl().equals(userURL))
                            {
                                startActivity(new Intent(VoiceActivity.this,StartRecordActivity.class));
                                finish();
                            }
                        }
                    }
                    Log.v("","");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*private void saveUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FireBaseUser user = new FireBaseUser();
        user.setId("test");
        user.setFirstName("test");
        user.setLastName("test");
        user.setLinkedinUrl("test");

        databaseReference.child("users").child("uid").setValue(user);
    }*/
}
