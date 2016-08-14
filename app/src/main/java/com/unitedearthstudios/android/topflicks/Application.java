package com.unitedearthstudios.android.topflicks;

/**
 * Created by Tem on 7/24/2016.
 */

//This class is the info we have specifically chosen to be pulled to the screen.
public class Application {

    private String title;
    private String description;
    private String pubDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    //must be overriden to formate correctly
    @Override
    public String toString() {
        return "Title:" + getTitle() + "\n" +
                "Description:" + getDescription() +"\n"+
                "Date:"+ getPubDate() + "\n";

    }
}
