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
    public static final int RequestPermissionCode = 1;
    boolean voiceCommandStart = false; //start recording with voice command
    boolean voiceCommandStop = false; //stop recording with voice command
    private final String[] startCommands = {"Hi", "Hello", "Hey"};
    private final String[] stopCommands = {"Bye", "Goodbye", "See you"};
    //for PCM
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private final int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    private final int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private final int BytesPerElement = 2; // 2 bytes in 16bit format

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
        if(isRecording){ //if recording is started, stop recording
            stopRecording(view);
        } else{ //if recording is stopped, start recording
            startRecording(view);
        }
    }

    public void startRecording(View view) throws IOException, IllegalStateException {
        ActivityCompat.requestPermissions(StartRecordActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        recorder.startRecording();
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        ((Button)view.findViewById(R.id.recordingButton)).setText("Stop Recording");
        ((EditText)findViewById(R.id.recordedTranscript)).setText("");
        isRecording = true;
    }

    public void stopRecording(View view) throws IOException, IllegalStateException {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
        ((Button)view.findViewById(R.id.recordingButton)).setText("Start Recording");
        ((EditText)findViewById(R.id.recordedTranscript)).setText("Woah it's a transcript"); //TODO: replace with method call to speech to text API
        isRecording = false;
    }

    //Conversion of short to byte
    public byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    public void writeAudioDataToFile() {
        // Write the output audio in byte
        String filePath = "/sdcard/8k16bitMono.pcm";
        short sData[] = new short[BufferElements2Rec];
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // writes the data to file from buffer stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
