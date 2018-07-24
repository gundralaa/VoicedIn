package com.example.android.voicedin.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;

import com.example.android.voicedin.StartRecordActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.android.voicedin.StartRecordActivity.RequestPermissionCode;

/**
 * Created by abhin on 7/24/2018.
 */

public class AudioRecordingUtils {
    private static boolean isRecording = false;
    private static MediaRecorder recorder;
    private static String filePath = "recording.3gp";
    private static int recordingNumber = 1;
    private static Button recordingButton;
    private final static int SAMPLE_RATE = 16000;
    private static AudioRecord record;
    private static Thread recordingThread = null;

    public static void setRecordingButton(Button recordingButton) {
        AudioRecordingUtils.recordingButton = recordingButton;
    }

    public static boolean isIsRecording() {
        return isRecording;
    }

    public static void startStopRecording() throws IOException, IllegalStateException {
        if(isRecording){ //if recording is started, stop recording
            recorder.stop();
            //recorder.release();
            recordingNumber++;
            recordingButton.setText("Start Recording");
            isRecording = false;
            SpeechToTextUtils.speechWithFile(filePath);
        } else{ //if recording is stopped, start recording
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + recordingNumber + ".3gp";
            prepareMediaRecorder();
            recorder.prepare();
            recorder.start();
            recordingButton.setText("Stop Recording");
            isRecording = true;
        }
    }

    private static void prepareMediaRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(16000);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(filePath);
    }

    private static int initMic(){
        AudioFormat af = new AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();

        record = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                .setAudioFormat(af)
                .build();
        return AudioRecord.getMinBufferSize(af.getSampleRate(), af.getChannelMask(), af.getEncoding());
    }

    public static void startRecording(){
        int bufferSize = initMic();
        byte Data[] = new byte[bufferSize];
        record.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                filePath = Environment.getExternalStorageDirectory().getPath();
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(filePath + " /record" + recordingNumber +".pcm");
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                while (isRecording){
                    record.read(Data, 0, Data.length);
                    try {
                        os.write(Data, 0, bufferSize);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    try {
                        os.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }, "Audio Recorder Thread");
        recordingThread.start();
    }

    public static void stopRecording(){
        if(null != record){
            isRecording = false;
            record.stop();
            record.release();
            record = null;
            recordingThread = null;
        }
    }
}
