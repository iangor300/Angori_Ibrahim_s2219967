
package com.example.angori_ibrahim_s2219967;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyConverter extends AppCompatActivity implements OnClickListener {

    private TextView rawDataDisplay;
    private String currencyChoice;
    private Button searchButton;

    private EditText insertCurrency;

    private Button clickBack;
    private Button buttonMoveToCC;


    private final String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        insertCurrency = findViewById(R.id.insertCurrency);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);


        buttonMoveToCC = findViewById(R.id.buttonMoveToCC);
        // Set click listener
        buttonMoveToCC.setOnClickListener(this);


        clickBack = findViewById(R.id.clickBack);
        clickBack.setOnClickListener(this);
    }

    public void onClick(View view) {

        if (view == buttonMoveToCC) {
            Intent intent = new Intent(CurrencyConverter.this, Activity_Mains.class);
            startActivity(intent);
        } else if (view == clickBack) {
            Intent intent = new Intent(CurrencyConverter.this, MainActivity.class);
            startActivity(intent);
        } else if (view == searchButton) {
            // Get the user input from the insertCurrency EditText
            currencyChoice = insertCurrency.getText().toString().trim();

            // Check if currencyChoice is empty before starting the progress
            if (!currencyChoice.isEmpty()) {
                startProgress();

            } }
    }

    private void startProgress() {
        // Run network access on a separate thread
        new Thread(new Task(urlSource, currencyChoice)).start();
    }

    class Task implements Runnable {
        private final String url;
        private final String currencyChoice;

        public Task(String aurl, String currencyChoice) {
            url = aurl;
            this.currencyChoice = currencyChoice;
        }

        @Override
        public void run() {
            // Network request to fetch XML data
            String result = fetchXMLData(url);

            if (result != null) {
                // Parse the XML data with the chosen currency filter
                LinkedList<RssFeed> parsedData = DataParser(result, currencyChoice);

                // Update the UI on the main thread with the parsed data
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateCurrencyList(parsedData, currencyChoice);
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

    private static String fetchXMLData(String url) {
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

    static LinkedList<RssFeed> DataParser(String xmlData, String currencyChoice) {
        LinkedList<RssFeed> list1 = new LinkedList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();
            RssFeed rf = null;
            Pattern pattern = Pattern.compile("\\b[A-Z]{3}\\b\\W*", Pattern.CASE_INSENSITIVE);

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
                        if (currentTag.equalsIgnoreCase("description")) {
                            if (text.contains(currencyChoice)) {
                                rf.setDescription(text);
                            } else {
                                rf.setDescription("");
                            }

                            // Extract currency code from description
                            String currencyCode = extractCurrencyCode(text);
                            rf.setCurrencyCode(currencyCode);

                            // Extract exchange rate from description
                            String rateTag = " = ";
                            int rateStartIndex = text.indexOf(rateTag) + rateTag.length();
                            int rateEndIndex = text.indexOf(" ", rateStartIndex);

                            if (rateStartIndex >= rateTag.length() && rateEndIndex > rateStartIndex) {
                                String exchangeRateStr = text.substring(rateStartIndex, rateEndIndex);
                                try {
                                    double exchangeRate = Double.parseDouble(exchangeRateStr);
                                    rf.setRate(exchangeRate);
                                } catch (NumberFormatException e) {
                                    rf.setRate(1.0);
                                    // If exchange rate parsing fails, 1.0 is used as a default value
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

    //using this method for 3 letter

    private static String extractCurrencyCode(String description) {
        // Use a regular expression to extract the three-letter currency code
        Pattern pattern = Pattern.compile("\\b[A-Z]{3}\\b");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
            // Return an empty string if no currency code is found
        }
    }

    private void updateCurrencyList(LinkedList<RssFeed> parsedData, String currencyChoice) {
        // Display parsed data in the TextView
        StringBuilder builder = new StringBuilder();
        boolean hasData = false;
        // Flag to check if there is any data
//these values are adjustable
        double strongestThreshold = 0.5;
        // Define the threshold for a strong currency
        double weakestThreshold = 2.0;
        // Define the threshold for a weak currency

        for (RssFeed feed : parsedData) {
            if (!feed.getDescription().isEmpty()) {
                String description = feed.getDescription().toLowerCase(); // Convert to lowercase for case-insensitive comparison

                // Check if the description contains the desired currency code or full name
                if (description.contains(currencyChoice.toLowerCase()) || description.contains(currencyCodeToFullName(currencyChoice).toLowerCase())) {
                    //if Data is found, set the flag to true
                    hasData = true;

                    double exchangeRate = feed.getRate();
                    String strength;
                    int textColor;

                    // Categorize the currency based on exchange rate if they are strong or weak
                    if (exchangeRate <= strongestThreshold) {
                        strength = "Strong";
                        textColor = Color.parseColor("#008000"); // Green is used for strong currencies
                    } else if (exchangeRate >= weakestThreshold) {
                        strength = "Weak";
                        textColor = Color.parseColor("#FF0000"); // Red for weak
                    } else {
                        strength = "Normal";
                        textColor = Color.parseColor("#A52A2A"); // Brown for if not strong and not weak
                    }

                    builder.append("").append(feed.getDescription()).append(" (").append(strength).append(")\n\n");
                    rawDataDisplay.setTextColor(textColor); // Set the text color based on currency strength
                }
            }
        }

        if (!hasData) {
            rawDataDisplay.setTextColor(Color.parseColor("#FF0000")); // Set text color to red
            builder.append("No data available for ").append(currencyChoice); // Display a message when there is no data
        }

        rawDataDisplay.setText(builder.toString());
    }


    private String currencyCodeToFullName(String currencyCode) {
        return currencyCode;
    }

}