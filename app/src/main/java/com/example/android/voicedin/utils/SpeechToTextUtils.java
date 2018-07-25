package com.example.android.voicedin.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.voicedin.EndActivity;
import com.example.android.voicedin.StartRecordActivity;
import com.example.android.voicedin.VoiceActivity;
import com.microsoft.cognitiveservices.speech.RecognitionStatus;
import com.microsoft.cognitiveservices.speech.SpeechFactory;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by abhin on 7/23/2018.
 */

public class SpeechToTextUtils {
    private static String speechSubscriptionKey = "7641b5bb27724a35b0f143df03e2ad5c";
    private static String serviceRegion = "westus";

    public static void speechCollect(TextView viewTxt){
        try{
            SpeechFactory factory = SpeechFactory.fromSubscription(speechSubscriptionKey, serviceRegion);
            SpeechRecognizer recognizer = factory.createSpeechRecognizer();
            Future<SpeechRecognitionResult> task = recognizer.recognizeAsync();

            SpeechRecognitionResult result = task.get();
            if(result.getReason() == RecognitionStatus.Recognized){
                viewTxt.setText(result.getText());
            } else {
                viewTxt.setText("Error Recognizing");
            }
            recognizer.close();
            factory.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }



    public static void speechWithFile(String audioFile){
        try {
            factory = SpeechFactory.fromSubscription(speechSubscriptionKey, serviceRegion);
        } catch (Exception e){
            e.printStackTrace();
        }

        SpeechRecognizer reco = null;
        ArrayList<String> content = new ArrayList<>();


        clearTextBox();

        try {
            content.clear();
            reco = factory.createSpeechRecognizerWithFileInput(audioFile);

            reco.IntermediateResultReceived.addEventListener(((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                content.add(s);
                setRecognizedText(TextUtils.join(" ", content));
                content.remove(content.size() - 1);
            }));

            reco.FinalResultReceived.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                content.add(s);
                setRecognizedText(TextUtils.join(" ", content));
            });

            final Future<Void> task = reco.startContinuousRecognitionAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static MicrophoneStream microphoneStream;
    private static SpeechFactory factory = null;
    private static Activity context;
    private static TextView view;

    public static void setContext(Activity context) {
        SpeechToTextUtils.context = context;
    }

    public static void setView(TextView view) {
        SpeechToTextUtils.view = view;
    }

    public static MicrophoneStream createMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }

    public static void continuousSpeechCollect(ImageView bt, StartRecordActivity activity){
        try {
            factory = SpeechFactory.fromSubscription(speechSubscriptionKey, serviceRegion);
        } catch (Exception e){
            e.printStackTrace();
        }

        bt.setOnClickListener(new View.OnClickListener() {
            boolean continuousListeningStarted = false;
            SpeechRecognizer reco = null;
            ArrayList<String> content = new ArrayList<>();

            @Override
            public void onClick(View view) {
                if(continuousListeningStarted){
                    if (reco != null){ //STOP
                        final Future<Void> task = reco.stopContinuousRecognitionAsync();
                        String transcript = "";
                        for(String word : content){
                            transcript += word + " ";
                        }
                        EndActivity.setTranscript(transcript);
                        continuousListeningStarted = false;
                    } else {
                        continuousListeningStarted = false;
                    }

                    return;
                }

                clearTextBox();

                try {
                    content.clear();
                    reco = factory.createSpeechRecognizerWithStream(SpeechToTextUtils.createMicrophoneStream());

                    reco.IntermediateResultReceived.addEventListener(((o, speechRecognitionResultEventArgs) -> {
                        final String s = speechRecognitionResultEventArgs.getResult().getText();
                        content.add(s);
                        setRecognizedText(TextUtils.join(" ", content));
                        content.remove(content.size() - 1);
                    }));

                    reco.FinalResultReceived.addEventListener((o, speechRecognitionResultEventArgs) ->{
                        final String s = speechRecognitionResultEventArgs.getResult().getText();
                        content.add(s);
                        setRecognizedText(TextUtils.join(" ", content));
                    });

                    final Future<Void> task = reco.startContinuousRecognitionAsync();

                    continuousListeningStarted = true;

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        bt.performClick();
    }

    

    private static void clearTextBox() {
        AppendTextLine("", true);
    }

    private static void setRecognizedText(final String s) {
        AppendTextLine(s, true);
    }

    private static void AppendTextLine(final String s, final Boolean erase) {
        context.runOnUiThread(() -> {
            if (erase) {
                view.setText(s);
            } else {
                String txt = view.getText().toString();
                view.setText(txt + System.lineSeparator() + s);
            }
        });
    }

    public <T> void setOnTaskCompletedLister(Future<T> task, OnTaskCompletedListener<T> listener){
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });

    }

    private interface OnTaskCompletedListener <T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;
    static {
        s_executorService = Executors.newCachedThreadPool();
    }

}
