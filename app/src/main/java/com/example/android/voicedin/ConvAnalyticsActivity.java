package com.example.android.voicedin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.voicedin.ConvAnalyticsUtils.SentimentAnalysisResult;

public class ConvAnalyticsActivity extends AppCompatActivity {

    private final String demoText = "Satya Narayana Nadella (born 19 August 1967) is an Indian American business executive. He is the Chief Executive Officer (CEO) of Microsoft, succeeding Steve Ballmer in 2014.[6][7] Before becoming CEO, he was Executive Vice President of Microsoft's cloud and enterprise group, responsible for building and running the company's computing platforms, developer tools and cloud computing services.";

    // Components
    private EditText inputText;
    private Button analyzeButton;
    private TextView sentimentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_analytics);

        inputText = findViewById(R.id.TextInput_EditText);
        analyzeButton = findViewById(R.id.AnalyzeTextSentiment_Button);
        sentimentScore = findViewById(R.id.SentimentScore_TextView);

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputText.getText().toString();
                SentimentAnalysisResult result = new SentimentAnalysisResult(text);
                result.runSentimentAnalysis();

                try {
                    while (!result.SearchComplete) {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ie) {
                    System.out.println(ie.getMessage());
                }

                sentimentScore.setText(result.SentimentScore);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
