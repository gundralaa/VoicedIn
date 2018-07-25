package com.example.android.voicedin.DBAsyncTasks;

import android.os.AsyncTask;

public class CreateNewUserResult {


    public class CreateNewUserAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... queries) {
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
        }
    }
}
