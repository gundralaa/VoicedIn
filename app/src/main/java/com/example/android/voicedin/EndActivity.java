package com.example.android.voicedin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.voicedin.ConvAnalyticsUtils.SentimentAnalysisResult;
import com.example.android.voicedin.ConvAnalyticsUtils.KeyPhrasesAnalysisResult;

public class EndActivity extends AppCompatActivity {

    private static String transcript;
    private static String conversationAnalysis = "";
    private static TextView name;
    private static TextView urlView;
    private static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Intent startAct = getIntent();

        ((TextView)findViewById(R.id.name)).setText(name.getText());
        transcript = startAct.getStringExtra("Transcript");
        url = startAct.getStringExtra("Url");

        ((TextView)findViewById(R.id.transcript)).setText(transcript);
        ((TextView)findViewById(R.id.transcript)).setMovementMethod(new ScrollingMovementMethod());
        urlView = findViewById(R.id.linkedInURL);

        urlView.setText(url);
        SentimentAnalysisResult sentimentResult = new SentimentAnalysisResult(transcript);
        sentimentResult.runSentimentAnalysis();
        KeyPhrasesAnalysisResult keyphraseResult = new KeyPhrasesAnalysisResult(transcript);
        keyphraseResult.runKeyPhrasesAnalysis();
        try {
            while (!sentimentResult.SearchComplete || !keyphraseResult.SearchComplete) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
    }

        conversationAnalysis += "Sentiment Analysis: " + (Double.parseDouble(sentimentResult.SentimentScore) * 100) + "% positive\n\n";
        conversationAnalysis += "Keywords: " + keyphraseResult.KeyPhrasesArrayAsString;

        ((TextView)findViewById(R.id.conversationAnalysis)).setText(conversationAnalysis);
        ((TextView)findViewById(R.id.conversationAnalysis)).setMovementMethod(new ScrollingMovementMethod());
    }

    public static void setNameView(TextView nameView) {
        name = nameView;
    }

    public static void setTranscript(String transcript){
        EndActivity.transcript = transcript;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
