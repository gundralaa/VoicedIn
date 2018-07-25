package com.example.android.voicedin.ConvAnalyticsUtils;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class KeyPhrasesAnalysisResult {
    private static String subscriptionKey = "854a143d9ac14f78a87f61b7ce9210a5";
    private static String host = "https://westus.api.cognitive.microsoft.com";
    private static String sentimentPath = "/text/analytics/v2.0/keyPhrases";
    private static String defaultQueryText = "Hello world!";

    private String QueryText = "";
    private String QueryTextAsJsonDocs = "";

    // Complete Statuses
    public Boolean SearchComplete;

    // Responses
    public String Response = "";
    public String KeyPhrasesArrayAsString = "";

    public KeyPhrasesAnalysisResult(String queryText) {
        this.SearchComplete = false;

        // use default query if none specified
        this.QueryText = (queryText == null || queryText.isEmpty()) ? defaultQueryText : queryText;
    }

    public void runKeyPhrasesAnalysis() {
        try {
            KeyPhrasesAnalysisTask task = new KeyPhrasesAnalysisTask();
            task.execute(this.QueryText);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Class for running sentiment analysis on a given text
     */
    public class KeyPhrasesAnalysisTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SearchComplete = false;
            KeyPhrasesArrayAsString = "Incomplete";
        }

        @Override
        protected String doInBackground(String... queries) {
            String textToAnalyze = queries[0];
            QueryTextAsJsonDocs = new Gson().toJson(translateTextToDocument(textToAnalyze));

            try {
                byte[] encoded_text = QueryTextAsJsonDocs.getBytes("UTF-8");

                URL url = new URL(host + sentimentPath);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

                // Make API call with encoded text
                OutputStream os = connection.getOutputStream();
                os.write(encoded_text);
                os.flush();
                os.close();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("failed: HTTP error code " + connection.getResponseCode());
                }

                // Get Response from API
                StringBuilder response = new StringBuilder ();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // parse JSON
                try {
                    JSONObject jsonText = new JSONObject(response.toString());
                    JSONArray documents = jsonText.getJSONArray("documents");
                    JSONObject firstValue = documents.getJSONObject(0);
                    JSONArray phrases = firstValue.getJSONArray("keyPhrases");

                    String[] array = new String[phrases.length()];
                    for (int i = 0; i < phrases.length(); i++) {
                        array[i] = phrases.getString(i);
                    }
                    KeyPhrasesArrayAsString = Arrays.toString(array);
                } catch (JSONException je) {
                    KeyPhrasesArrayAsString = "Exception thrown";
                    System.out.println(je.getMessage());
                }

                SearchComplete = true;
                Response = response.toString();
            } catch (UnsupportedEncodingException uee) {
                System.out.println(uee.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (RuntimeException re) {
                System.out.println(re.getMessage());
            }

            return Response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
        }
    }

    protected Documents translateTextToDocument(String queryText) {
        Documents documents = new Documents ();
        documents.add ("1", "en", queryText);
        return documents;
    }

    protected static String prettifyJsonText(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    private class Document {
        public String id, language, text;

        public Document(String id, String language, String text){
            this.id = id;
            this.language = language;
            this.text = text;
        }
    }

    private class Documents {
        public List<Document> documents;

        public Documents() {
            this.documents = new ArrayList<Document>();
        }
        public void add(String id, String language, String text) {
            this.documents.add (new Document (id, language, text));
        }
    }
}
