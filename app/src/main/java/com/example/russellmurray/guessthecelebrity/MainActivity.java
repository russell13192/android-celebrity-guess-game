package com.example.russellmurray.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    // ArrayList for celebrity photo links
    ArrayList<String> celebURLs = new ArrayList<String>();
    // ArrayList for celebrity names
    ArrayList<String> celebNames = new ArrayList<String>();
    // Declaring int for choosing which celebrity to display
    int chosenCeleb = 0;
    // Declaring int for storing location of correct answer
    int locationOfCorrectAnswer = 0;
    // Declaring array for storing answer options
    String[] answers = new String[4];
    // Declaring ImageView that will display celebrity picture
    ImageView imageView;
    // Declaring Buttons
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    // Funtion for handling user making selection
    public void celebChosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Incorrect! It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        // Calling function to create new question after user makes selection
        createNewQuestion();
    }

    // Implementation of class that will download photos, class will be passed in a string, it will
    // not be required to do anything while class is working and it will return a BItmap
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                // Declaring URL variable for assigning URL object from array that is passed into function call
                URL url = new URL(urls[0]);
                // Declaring HTTP URL connection for opening connection with URL that is passed into function call
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Attempting to connect to URL via HTTPURLConnection connect method
                connection.connect();
                // Delcaring and initializing inputstream to receive encoded data
                InputStream in = connection.getInputStream();
                // Storing encoded image data in bitmap object
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    // Implementation of class that downloads content for celebrity data download. Class extends AsyncTask
    public class DownloadTask extends AsyncTask<String, Void, String> {
        // Override the doInBackground method. Method is protected meaning it can be accessed outside of the class.
        @Override
        protected String doInBackground(String... urls) {
            // Declaring empty string for iteration to append char data that is returned into a string
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
    public void createNewQuestion() {
        // Randomly generating which celeb is shown
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());
        // Declaring ImageDownloader object for downloading image once celebrity to display has been randomly generated
        ImageDownloader imageTask = new ImageDownloader();
        // Declaring Bitmap to store Bitmap that is returned from ImageTask object execute method
        Bitmap celebImage;
        // Initializing Bitmap via ImageTask execute method as implemented above
        try {
            celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            // Updating imageView with downloaded image
            imageView.setImageBitmap(celebImage);
            // Randomly generating location of correct answer
            locationOfCorrectAnswer = random.nextInt(4);
            // Variable for generating incorrect answer locations
            int incorrectAnswerLocation;
            // Iterating to set up button layout
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                }
                else {
                    incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        // Declaring new class variable as declared above. Class extends AsyncTask. Does not require any arguments to declare
        DownloadTask task = new DownloadTask();
        // Finding buttons in layout
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        // Declaring variable to store result from calling the DownloadTask
        String result = null;
        // Calls involving DownloadTask must be enclosed in try catch block to prevent app from cash in the event
        // internet services are not availabe and variables are unable to be initialized
        try {
            // Attempting to invoke execute method on DownloadTask object that makes the GET HTTP request
            result = task.execute("http://www.posh24.se/kandisar").get();
            // Declaring an initializing String Array object to parse HTML that is returned from DownloadTask execute call
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            // Declaring and initializing pattern to aid in parsing HTML document, finds text in HTML that contains src=
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            // Declaring a matcher to find all components of the pattern in the split up HTML document that has been split on <div class="sidebarContainer">
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                //System.out.println(m.group(1));

                // Adding celebrity photo urls to ArrayList
                celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                //System.out.println(m.group(1));

                // Adding celebrity names to ArrayList
                celebNames.add(m.group(1));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Calling function to generate question
        createNewQuestion();
    }
}
