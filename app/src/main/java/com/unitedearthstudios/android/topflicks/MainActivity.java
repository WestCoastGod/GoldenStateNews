package com.unitedearthstudios.android.topflicks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String mFileContents;
    private Button btnParse;
    private ListView listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnParse = (Button)findViewById(R.id.btnParse);
        //only when the bottom is clicked the download process will began
        //extracting the fields
        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add parse intelligence
                //this is the actually data that we are passing through
                //to get parsed.
                ParseData parseData = new ParseData(mFileContents);
                parseData.process();
                //the array adapter we go through the array and parse the data that we wanted
                //then show us on the list view
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        //layout that enables to see the list view
                        //after you have create the textview layout
                        //you need to override a to string method in the container class
                        //the array adapter will automatically look for this is your data
                        // comes out with your package name class name and memory code
                        // then you did not override put string method
                        MainActivity.this, R.layout.list_item, parseData.getApplications());

                listData.setAdapter(arrayAdapter);
            }
        });

        listData = (ListView)findViewById(R.id.xmlListView);

        //instance our magic class below
        DownloadData downloadData = new DownloadData();
        //grab all that good data we want to stream
        downloadData.execute("http://www.nba.com/warriors/rss.xml");

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //this inner class contains implicit reference to outer a
    // ctivity could be an issue is not wrote properly (leaks
    //This method will call and get the data we need Async will get data
    // while user does other things. Android will update when ready. This class
    // sole purpose is to down load the info we need.
    // Async Task always take 3 parameters. the 1st parameters is the download location
    // 2nd is used for progress bar small files dont need leave void the 3rd is the actually result
    private class DownloadData extends AsyncTask<String, Void, String> {
        //This string stores the data that we are asking for

        //This is needed to be back for our background work in dots in the
        // parameter are saying you can specify the variables passing through
        // we only have one right now everything we put in this method well be handling automatically
        @Override
        protected String doInBackground(String... params) {
            //Lets tell the param we are passing through
            mFileContents = downloadXMLFile(params[0]);
            //for debugging communication purposes
            if (mFileContents == null) {
                Log.d("DownloadData", "Error downloading");
            }

            return mFileContents;
        }


        //lets override onPost to print out message
        // just to be sure our do in background is working Automatically
        //this method well be called after doin method completes so be sure you want to use for memory purposes
        //until work completes activity stays in memory BUT
        //this is the right place to update the user interface example textview update
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //log for some info on our issue if there is any
            //this helps with debugging this logs are automatically taken out when compiled
            Log.d("DownloadData", "Result was: " + result);
        }


        //This code does the actually downloading
        //I used String because obviously our info we are using is text
        //temp buffer is used to store the xml files.
        // Depending on the size of the
        //xml file it will only down load a certain quantity of character at a time
        private String downloadXMLFile(String urlPath) {
            //temp buffer is used to store the xml files.
            // Depending on the size of the xml file it will only
            // down load a certain quantity of character at a time
            // String builder creates string in a efficient manner
            StringBuilder tempBuffer = new StringBuilder();
            //if there are errors I dont want the program to crash so I put in try catch
            try {
                //first let try to open our url to see if its valid
                URL url = new URL(urlPath);
                //lets physical open it this up on the internet
                // this line opens the connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //if I get a response code my debugging can be done quickly
                int response = connection.getResponseCode();
                Log.d("DownloadData", "The response code was " + response);
                //this 2 lines will stream and put together my data
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);


                //this variables will actually download the the data I want
                int charRead;
                //allocate 500 bits at a time to read depending on the file might take longer
                char[] inputBuffer = new char[500];
                //use a loop for continues reading the file. it will read 500
                // characters at a time until we get to the end OR Less. after exit
                while (true) {
                    charRead = inputStreamReader.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    //next we convert the input buffer to a string always start from 0 then end at charRead
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();


                //only returns if error occurs
            } catch (IOException e) {
                Log.d("DownloadData", "IO Exception reading data: " + e.getMessage());
                //keep getting errors so I wanted a specific line of errors.
                //print stack will help with this
                e.printStackTrace();
                //first time running this received some security exception
                //so I added a catch for the code to clean up the log from all stack trace
                //after running it once again it was obviously a internet permission issue
                // to manifest we go !
            }catch(SecurityException e) {
                Log.d("DownloadData", "Security exception. Needs permissions? " + e.getMessage());
            }

            return null;
        }





        }

    }

