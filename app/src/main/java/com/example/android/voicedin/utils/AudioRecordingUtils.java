package com.example.android.voicedin.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.voicedin.StartRecordActivity;
import com.example.android.voicedin.helper_classes.RiffHeader;

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
    private static String filePath = "recording.3gp";
    private static int recordingNumber = 1;
    private static ImageView recordingButton;
    private final static int SAMPLE_RATE = 8000;
    private static MediaRecorder record = null;

    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static AudioRecord recorder = null;
    private static Thread recordingThread = null;
    private final int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    private static final int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private static final int BytesPerElement = 2; // 2 bytes in 16bit format

    public static void setRecordingButton(ImageView recordingButton) {
        AudioRecordingUtils.recordingButton = recordingButton;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static boolean isIsRecording() {
        return isRecording;
    }

    public static void startRecording() throws IOException, IllegalStateException {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        recorder.startRecording();
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        isRecording = true;
    }

    public static void stopRecording() throws IOException, IllegalStateException {
        // stops the recording activity
        if (null != recorder) {
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            isRecording = false;
        }
        isRecording = false;
        recordingNumber++;
        recordingButton.setOnClickListener(null);
    }

    //Conversion of short to byte
    private static byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private static void writeAudioDataToFile() {
        // Write the output audio in byte
        filePath = Environment.getExternalStorageDirectory().getPath() + "/record" + recordingNumber + ".wav" ;
        short sData[] = new short[BufferElements2Rec];
        short channels = 1;
        short bits = 16;
        FileOutputStream os = null;
        RiffHeader header = new RiffHeader(RiffHeader.FORMAT_PCM, channels,16000, bits, BufferElements2Rec);
        try {
            os = new FileOutputStream(filePath);
            header.write(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
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

    public static void startRecordingMP3() throws IOException, IllegalStateException {
        //if recording is stopped, start recording
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + recordingNumber + ".mp3";
        prepareMediaRecorder();
        record.prepare();
        record.start();
        isRecording = true;
    }

    public static void stopRecordingMP3() {
        record.stop();
        record.release();
        //recordingNumber++;
        isRecording = false;
    }

    private static void prepareMediaRecorder() {
        record = new MediaRecorder();
        record.setAudioChannels(1);
        record.setAudioSamplingRate(16000);
        record.setAudioSource(MediaRecorder.AudioSource.MIC);
        record.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        record.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        record.setOutputFile(filePath);
    }

    public static void setFilePathWAV() {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + recordingNumber + ".wav";
        recordingNumber++;
    }
}