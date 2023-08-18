/*  Starter project for Mobile Platform Development in Resit 2022/2023
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Ibrahim Angori
// Student ID           s2219967
// Programme of Study   Mobile Platform Development
//

// UPDATE THE PACKAGE NAME to include your Student Identifier

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.angori_ibrahim_s2219967;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
// Import statements

public class MainActivity extends AppCompatActivity implements OnClickListener {

    // Declare UI elements
    private TextView rawDataDisplay;
    private TextView ed1;
    private TextView ed2;
    private Button startButton;
    private Button button;
    private Button buttonMoveToMains;
    private Button More_Currencies_Conversion;
    private Button Currencies_Conversion;

    private Button Cc_Page_Button;

    // URL to fetch XML data
    private final String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        rawDataDisplay = findViewById(R.id.rawDataDisplay);

        startButton = findViewById(R.id.startButton);
        // Set click listener for the start button
        startButton.setOnClickListener(this);




        Cc_Page_Button = findViewById(R.id.Cc_Page_Button);
        // Set click listener for the start button
        Cc_Page_Button.setOnClickListener(this);

        More_Currencies_Conversion = findViewById(R.id.More_Currencies_Conversion);
        // Set click listener for the start button
        More_Currencies_Conversion.setOnClickListener(this);
        Currencies_Conversion = findViewById(R.id.Currencies_Conversion);
        // Set click listener for the start button
        Currencies_Conversion.setOnClickListener(this);

//        ed1 = findViewById(R.id.ed1);
//        ed2 = findViewById(R.id.ed2);

        // Initialize and set click listener for the button to move to Activity_Mains
//        Button moveToMainsButton = findViewById(R.id.buttonMoveToMains);
//        moveToMainsButton.setOnClickListener(new View.OnClickListener()
    }
    @Override
    public void onClick(View view) {
         if (view == Cc_Page_Button) {
            Intent intent = new Intent(MainActivity.this, Activity_Mains.class);
            startActivity(intent);
        }
       else if (view == More_Currencies_Conversion) {
            Intent intent = new Intent(MainActivity.this, PoundRate_Converter.class);
            startActivity(intent);
        } else if (view == Currencies_Conversion) {
            Intent intent = new Intent(MainActivity.this, CurrencyConverter.class);
            startActivity(intent);
        }

        else if (view == startButton){
            startProgress();
        }
    }


    private void startProgress() {

        new Thread(new Task(urlSource)).start();
    }

    // Task class to fetch XML data and update UI
    private class Task implements Runnable {
        private final String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
            // Network request to fetch XML data
            String result = fetchXMLData(url);

            if (result != null) {
                // Parse the XML data
                LinkedList<RssFeed> parsedData = DataParser(result);


                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";


                Log.e("MyTag","in run");

                try
                {
                    Log.e("MyTag","in try");
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    while ((inputLine = in.readLine()) != null)
                    {
                        result = result + inputLine;
                        Log.e("MyTag",inputLine);

                    }
                    in.close();
                }
                catch (IOException ae)
                {
                    Log.e("MyTag", "ioexception");
                }

                //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
                int i = result.indexOf(">");
                result = result.substring(i+1);
                //Get rid of the 2nd tag <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
                i = result.indexOf(">");
                result = result.substring(i+1);
                Log.e("MyTag - cleaned",result);
                //LOWERCASE THE UPPERCASED TAGS </lastBuildDate> and </pubDate>
                result = result.replace("</lastBuildDate>","</lastbuilddate>");
                result = result.replace("</pubDate>","</pubdate>");

                // Need separate thread to access the internet resource over network
                // Other neater solutions should be adopted in later iterations.
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateCurrencyList(parsedData);
                    }
                });
            } else {
                // Handle the case when data fetch failed
                runOnUiThread(new Runnable() {
                    public void run() {
                        rawDataDisplay.setText("Failed to fetch data");
                    }
                });
            }
        }
    }

    // Method to fetch XML data from the URL
    private String fetchXMLData(String url) {
        StringBuilder resultBuilder = new StringBuilder();

        try {
            URL aurl = new URL(url);
            URLConnection yc = aurl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                resultBuilder.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return resultBuilder.toString();
    }

    // Data parsing method (parse XML data using XmlPullParser and populate the list with RssFeed objects)
    private LinkedList<RssFeed> DataParser(String xmlData) {
        // Data parsing logic
        LinkedList<RssFeed> list1 = new LinkedList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();
            RssFeed rf = null;

            String currentTag = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equalsIgnoreCase("item")) {
                        rf = new RssFeed();
                    }



                    currentTag = tagName;
                } else if (eventType == XmlPullParser.TEXT && rf != null) {
                    String text = parser.getText();
                    if (currentTag != null) {
                        // Set the value in the current RssFeed object based on the current tag
                        if (currentTag.equalsIgnoreCase("title")) {
                            rf.setTitle(text);
                        } else if (currentTag.equalsIgnoreCase("guid")) {
                            rf.setGuid(text);
                        } else if (currentTag.equalsIgnoreCase("pubDate")) {
                            rf.setPubdate(text);
                        } else if (currentTag.equalsIgnoreCase("category")) {
                            rf.setCategory(text);
                        } else if (currentTag.equalsIgnoreCase("description")) {
                            rf.setDescription(text);
                            // Extract exchange rate from description
                            String rateTag = "= ";
                            int rateStartIndex = text.indexOf(rateTag) + rateTag.length();
                            int rateEndIndex = text.indexOf(" ", rateStartIndex);
                            if (rateStartIndex >= rateTag.length() && rateEndIndex > rateStartIndex) {
                                String exchangeRateStr = text.substring(rateStartIndex, rateEndIndex);
                                try {
                                    double exchangeRate = Double.parseDouble(exchangeRateStr);
                                    rf.setRate(exchangeRate);
                                } catch (NumberFormatException e) {
                                    rf.setRate(1.0); // If exchange rate parsing fails, use 1.0 as a default value
                                }
                            }
                        } else if (currentTag.equalsIgnoreCase("lastBuildDate")) {
                            rf.setLastbuilddate(text);
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equalsIgnoreCase("item") && rf != null) {
                        list1.add(rf);
                        rf = null;
                    }
                    currentTag = null;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return list1;
    }

    // Update the currency list and display parsed data in TextView
    private void updateCurrencyList(LinkedList<RssFeed> parsedData) {
        // Display parsed data in the TextView
        StringBuilder builder = new StringBuilder();
        for (RssFeed feed : parsedData) {
            builder.append("Title: ").append(feed.getTitle()).append("\n");
            builder.append("Guid: ").append(feed.getGuid()).append("\n");
            builder.append("Category: ").append(feed.getCategory()).append("\n");
            builder.append("Description: ").append(feed.getDescription()).append("\n");
            builder.append("Pub Date: ").append(feed.getPubdate()).append("\n\n\n");
        }
        rawDataDisplay.setText(builder.toString());

    }}



