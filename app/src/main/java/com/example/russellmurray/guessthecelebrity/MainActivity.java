package com.example.russellmurray.guessthecelebrity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    // Implementation of class that downloads content for celebrity data download. Class extends AsyncTask
    public class DownloadTask extends AsyncTask<String, Void, String > {
        // Override the doInBackground method. Method is protected meaning it can be accessed outside of the class.
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            // Declaring URL variable for assigning URL object from array that is passed into function call
            URL url;
            // Declaring HTTP URL connection for opening connection with URL that is passed into function call
            HttpURLConnection urlConnection = null;

            // Attempting to open URL connection. Segment is surrounded in try-catch block to prevent app from crashing
            // in event internet connection is not available
            try {
                // Assigning URL to first object in strings array that is passed into function call
                url = new URL(urls[0]);
                // Attempting to open URL connection
                urlConnection = (HttpURLConnection) url.openConnection();
                // Assigning Inputstream to URL connection
                InputStream inputStream = urlConnection.getInputStream();
                // Assigning reader to Inputstream
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // Assigning int variable to read data
                int data = inputStreamReader.read();
                // Conditional loop that ensures all data is read
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Declaring new class variable as declared above. Class extends AsyncTask. Does not require any arguments to declare
        DownloadTask task = new DownloadTask();
        // Declaring variable to store result from calling the DownloadTask
        String result = null;
        // Calls involving DownloadTask must be enclosed in try catch block to prevent app from cash in the event
        // internet services are not availabe and variables are unable to be initialized
        try {
            // Attempting to invoke execute method on DownloadTask object that makes the GET HTTP request
            result = task.execute("http://www.posh24.se/kandisar").get();
            
            Log.i("Contents of URL", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
