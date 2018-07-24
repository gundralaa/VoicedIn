package com.example.android.voicedin.utils;

import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.RecognitionStatus;
import com.microsoft.cognitiveservices.speech.SpeechFactory;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

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
                viewTxt.setText(result.toString());
            } else {
                viewTxt.setText("Error Recognizing");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
