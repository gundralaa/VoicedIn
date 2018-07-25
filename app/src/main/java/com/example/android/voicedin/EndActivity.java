package com.example.android.voicedin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.voicedin.ConvAnalyticsUtils.SentimentAnalysisResult;

public class EndActivity extends AppCompatActivity {
    private static String transcript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        ((EditText)findViewById(R.id.transcript)).setText(transcript);

        SentimentAnalysisResult result = new SentimentAnalysisResult(transcript);
        result.runSentimentAnalysis();
        try {
            while (!result.SearchComplete) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
        ((EditText)findViewById(R.id.conversationAnalysis)).setText(result.SentimentScore);
    }

    public static void setTranscript(String transcript){
        EndActivity.transcript = transcript;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
