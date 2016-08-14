package com.unitedearthstudios.android.topflicks;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Tem on 7/24/2016.
 */

//here is the working class that turns or data into beautiful readable words
    //this class tells android how to process our data
public class ParseData {
    private String xmlData;
    //it would make sense to use an array to store multiple entries
    //we put our DataContainer class data into an array
    private ArrayList<Application> applications;

    public ParseData(String xmlData) {
        this.xmlData = xmlData;
        applications = new ArrayList<Application>();
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    //code to identify if we have errors or if it was a success
    public boolean process(){
        //has process failed?
        boolean status = true;
        //Application record
        Application currentRecord = null;
        boolean inItem = false;
        String textValue = "";

        try {
            //before we use the pullparser we must call and initianize the set up
            //factory is the key to starting the parser does all the lifting for us
            //create new instance to get access
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            //the code that was sent to us is xpp variable
            //we pass the code that we want the pull parser to work on
            XmlPullParser xpp = factory.newPullParser();
            //After we receive the data we need to tell android were to put it
            //so we can work on it more I put String reader in after error was shown
            //Android needs this in specific format to be parsed now it is happy
            //string reader converts the string to a more readable format
            xpp.setInput(new StringReader(this.xmlData));
            //capture the event type
            //the event type depends on which part of our data we want to capture
            //it will extract data from the xml file and collect what we want then exit
            //event type example is artist author title
            //if you write yourself takes forever I have deadlines to meet.
            int eventType = xpp.getEventType();
            //continue to loop until we get to the end
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    //process starting from the beginning tagname of the document page
                    case XmlPullParser.START_TAG:
                        //Log.d("ParseApplications", "Starting tag for " + tagName);

                        //if we are at the beginning on item tag on document then get ready
                        // to extract tags "item" must match with bottom or wont work
                        if (tagName.equalsIgnoreCase("item")) {
                            inItem = true;
                            currentRecord = new Application();
                            break;
                        }
                        //this gets the actually text not tags
                        //this is getting all the text from our xml doc
                        //the data we get from below we will use in the end tag case
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    //this is the ending tag name that matches the beginning tag name
                        //to better understand please run and properly check logs
                    case XmlPullParser.END_TAG:
                        //Log.d("ParseApplications", "Ending tag for " + tagName);
                        //only when we are in item tags we will start extracting between each tag
                        //please check your reader tags name can vary sometimes "entry"
                        if(inItem) {
                            //if we reached the end save the record
                            //must be matching tags to xml doc case sensitive so make sure
                            if(tagName.equalsIgnoreCase("item")) {
                                applications.add(currentRecord);
                                //once we hit the ending tag  set item to false
                                //to stop checking and add another otherwise we would keep checking
                                inItem = false;
                            } else if(tagName.equalsIgnoreCase("title")) {
                                currentRecord.setTitle(textValue);
                            } else if(tagName.equalsIgnoreCase("description")) {
                                currentRecord.setDescription(textValue);
                            } else if(tagName.equalsIgnoreCase("pubDate")) {
                                currentRecord.setPubDate(textValue);
                            }
                        }
                        break;

                    default:
                        // Nothing else to do.


                }
                eventType = xpp.next();


            }
        }catch (Exception e){
            //I want to exit if there is a download error
            status = false;
            //catches all exceptions
            e.printStackTrace();
        }



        //go through each element in the array list
        //for each element create object called app
        //this for loop os just to see in the log
        for(Application app : applications) {
            Log.d("ParseApplications", "****************");
            Log.d("ParseApplications", "Title: " + app.getTitle());
            Log.d("ParseApplications", "Description: " + app.getDescription());
            Log.d("ParseApplications", "Release Date: " + app.getPubDate());


        }

        return true;

    }
}
