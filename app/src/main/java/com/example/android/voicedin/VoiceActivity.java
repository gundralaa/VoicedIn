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
import java.util.UUID;

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

    String userURL;
    String firstName;
    String lastName;
    Button button;
    TextView textView;
    UUID userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int permission = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, permission);
        SpeakerRecognitionUtils.setUserId(userId);

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

        /*APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.postRequest(ApiActivity.this, shareUrl, shareJsonText, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                ((TextView) findViewById(R.id.response)).setText(apiResponse.toString());
            }

            @Override
            public void onApiError(LIApiError error) {
                ((TextView) findViewById(R.id.response)).setText(error.toString());
            }
        });*/
    }

    private void setUpdateState() {
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();

        /*((TextView) findViewById(R.id.at)).setText(
                accessTokenValid ? session.getAccessToken().toString() : "Sync with LinkedIn to enable these buttons");
        ((Button) findViewById(R.id.apiCall)).setEnabled(accessTokenValid);
        ((Button) findViewById(R.id.deeplink)).setEnabled(accessTokenValid);*/
    }

}
