
//
// Name                 ________Ibrahim Angori_________
// Student ID           _______s2219967__________
// Programme of Study   ________Mobile Platform Development_________
//

package com.example.angori_ibrahim_s2219967;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.DecimalFormat;

public class PoundRate_Converter extends AppCompatActivity implements OnClickListener {


    private Button Button1;
    private TextView TV1;
    private String Result;
private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private TextView TV2;
    private TextView TV3;
    private EditText ET3;
    private Button Button3;
    private TextView ET4;
    private EditText ET5;
    private EditText ET1;
    private EditText ET2;

    private Button Button4;
    private Button Button0;
    private Button Button2;
    private Button Button6;
    private Button revers_Back;
    private TextView TV4;
    private EditText ET6;
    private TextView TV5;
    private String result;
    private String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LinkedList<RssFeed> alist = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.poundrate_converter);

        ET1 = findViewById(R.id.ET1);
        ET2 = findViewById(R.id.ET2);

        Button1 =  findViewById(R.id.Button1);
        Button1.setOnClickListener(this);

        Button2 =  findViewById(R.id.Button2);
        Button2.setOnClickListener(this);

       TV2 =  findViewById(R.id.TV2);
       TV1 =  findViewById(R.id.TV1);
        Button0 = findViewById(R.id.revers_Back);
        Button0.setOnClickListener(this);


        TV3 =  findViewById(R.id.TV3);

        ET3 =  findViewById(R.id.ET3);

      Button3 =  findViewById(R.id.Button3);
        Button3.setOnClickListener(this);

        ET4 =  findViewById(R.id. ET4);
    }
    public void mainActivity() {
        Intent intent = new Intent(PoundRate_Converter.this,MainActivity.class);
        startActivity(intent);
    }

    public void onClick(View aview) {
        if (aview == revers_Back || aview == Button0) {
            mainActivity();
        } else if (aview == Button1) {
            String inputText = ET1.getText().toString();
            if (inputText.isEmpty()) {
                Toast.makeText(this, "Insert Currency or Country name (adjective)", Toast.LENGTH_LONG).show();
            } else {
                startedProgress();
            }
        } else if (aview == Button2) {
            poundToOtherCurrency();
        } else if (aview == Button3) {
            otherCurrencyToPound();

        }
    }

    public void startedProgress() {
        // Run network access on a separate thread
        new Thread(new Task5(urlSource)).start();
    }
    private class Task5 implements Runnable {
        private String url;

        public Task5(String aurl) {
            url = aurl;
        }
        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine;
            StringBuilder Result = new StringBuilder(); // Use StringBuilder for better performance

            try {
                aurl = new URL(url);
                yc = aurl.openConnection();

                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                while ((inputLine = in.readLine()) != null) {
                    Result.append(inputLine); // Append to StringBuilder
                }
                in.close();
            } catch (IOException ae) {
                ae.printStackTrace();
                // Print the exception details
            }

            String xmlData = Result.toString();

            // Process the XML data as needed
            parseData(xmlData);
        }
    }
        private LinkedList<RssFeed> parseData(String Result) {

            PoundRate_Converter.this.runOnUiThread(new Runnable() {

                public void run() {
                    RssFeed rf = null;
                    LinkedList<RssFeed> alist = null;

                    try {
                        String myChosenDescription = "";

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser parse = factory.newPullParser();
                        parse.setInput(new StringReader(Result));
                        int eventType = parse.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {

                            if (eventType == XmlPullParser.START_TAG) {


                                if (parse.getName().equalsIgnoreCase("channel")) {
                                    rf = new RssFeed();
                                } else if (parse.getName().equalsIgnoreCase("description")) {

                                    String temp = parse.nextText();

                                    if (temp.contains(ET1.getText().toString())) {

                                        myChosenDescription = myChosenDescription + temp + "\n" + "\n";


                                       TV1.setText(myChosenDescription);


                                        color1();


                                    }
                                }


                            } else if (eventType == XmlPullParser.END_TAG) {
                                if (parse.getName().equalsIgnoreCase("description")) {
                                    Log.e("MyTag", "rf is " + rf.toString());
                                    alist.add(rf);
                                }
                            }

                            eventType = parse.next();

                        }



                    } catch (XmlPullParserException ae1) {
                        Log.e("MyTag", "Parsing error" + ae1.toString());
                    } catch (IOException ae1) {
                        Log.e("MyTag", "IO error during parsing");
                    }
                    Log.e("MyTag", "End document");
                }


            });


            return null;

        }



    public void color1() {
        System.out.println( TV1.getText() + "answer");
        Matcher matcher0 = Pattern.compile("(?!=\\d\\.)([\\d.]+)").matcher( TV1.getText());

        while (matcher0.find()) {
            double double0 = Double.parseDouble(matcher0.group(1));
            System.out.println(double0);


            // red is the weakest
            if (double0 <= 70000 && double0 > 7000){
                TV1.setTextColor(Color.parseColor("#FF0000"));
            }
            // Brown is better then red
            else if (double0 < 700 && double0 >= 70){
                TV1.setTextColor(Color.parseColor("#8B4513"));
            }
            // blue is good
            else if (double0 < 70 && double0 >= 2){
                TV1.setTextColor(Color.parseColor("#0000FF"));
            }
            // Green is very good
            else if (double0 < 2 && double0 >= 0){
                TV1.setTextColor(Color.parseColor("#008000"));
            }
        }
    }






    public void poundToOtherCurrency() {

//
        Matcher matcher1 = Pattern.compile("(?!=\\d\\.)([\\d.]+)").matcher( TV1.getText());

        while (matcher1.find()) {
            double parseDouble = Double.parseDouble(matcher1.group(1));


            if (parseDouble > 100 && parseDouble <= 200) {
                //it shows as a light green the colour is adjustable
                TV1.setTextColor(Color.parseColor("#96ff96"));
            }


            double poundToOtherCurrency = Double.parseDouble(ET2.getText().toString());

            double poundResult = poundToOtherCurrency * parseDouble;

            TV2.setText("Value  of pound to chosen currency: " + String.valueOf(decimalFormat.format(poundResult)));

            TV3.setText(String.valueOf(decimalFormat.format(parseDouble)));

        }
    }

    public void otherCurrencyToPound() {
        Matcher matcher = Pattern.compile("(?!=\\d\\.)([\\d.]+)").matcher( TV1.getText());

        while (matcher.find()) {
            double aDouble = Double.parseDouble(matcher.group(1));
            System.out.println(aDouble);

            double otherToGBP = Double.parseDouble(ET3.getText().toString());


            double otherCurrencyToGBP = otherToGBP / aDouble;

            ET4.setText("Value of GBP is : " + String.valueOf(decimalFormat.format(otherCurrencyToGBP)));


        }
    }


}
