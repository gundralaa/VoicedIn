package com.example.android.voicedin.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.microsoft.cognitiveservices.speech.RecognitionStatus;
import com.microsoft.cognitiveservices.speech.SpeechFactory;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

public class SpeechToTextAPICall {
    private static String speechSubscriptionKey = "7641b5bb27724a35b0f143df03e2ad5c";
    private static String serviceRegion = "westus";

    private static String transcript = "";

    public static void speechCollect(TextView viewTxt){
        try{
            SpeechFactory factory = SpeechFactory.fromSubscription(speechSubscriptionKey, serviceRegion);
            com.microsoft.cognitiveservices.speech.SpeechRecognizer recognizer = factory.createSpeechRecognizer();
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

    public static String speechWithFile(String audioFilePath){
        try {
            factory = SpeechFactory.fromSubscription(speechSubscriptionKey, serviceRegion);
        } catch (Exception e){
            e.printStackTrace();
        }

        com.microsoft.cognitiveservices.speech.SpeechRecognizer reco = null;
        ArrayList<String> content = new ArrayList<>();

        //clearTextBox();

        try {
            content.clear();
            reco = factory.createSpeechRecognizerWithFileInput(audioFilePath);

            reco.IntermediateResultReceived.addEventListener(((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                content.add(s);
                transcript = getRecognizedText(TextUtils.join(" ", content));
                content.remove(content.size() - 1);
            }));

            reco.FinalResultReceived.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                content.add(s);
                transcript = getRecognizedText(TextUtils.join(" ", content));
            });

            //final Future<Void> task = reco.startContinuousRecognitionAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transcript;
    }

    private static SpeechFactory factory = null;
    private static Activity context;
    private static TextView view;

    public static void setContext(Activity context) {
        SpeechToTextAPICall.context = context;
    }

    public static void setView(TextView view) {
        SpeechToTextAPICall.view = view;
    }

    private static void clearTextBox() {
        AppendTextLine("", true);
    }

    private static String getRecognizedText(final String s){
        return s;
    }

    private static void setRecognizedText(final String s) {
        AppendTextLine(s, true);
    }

    private static void AppendTextLine(final String s, final Boolean erase) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (erase) {
                    view.setText(s);
                } else {
                    String txt = view.getText().toString();
                    view.setText(txt + System.lineSeparator() + s);
                }
            }
        });
    }

    public <T> void setOnTaskCompletedLister(final Future<T> task, final OnTaskCompletedListener<T> listener){
        s_executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                T result = task.get();
                listener.onCompleted(result);
                return null;
            }
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
