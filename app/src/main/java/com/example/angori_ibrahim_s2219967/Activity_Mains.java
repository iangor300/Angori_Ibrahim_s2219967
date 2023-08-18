
package com.example.angori_ibrahim_s2219967;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Mains extends AppCompatActivity {
    private ImageView flagImageView;

    private Button backButton;
    private Button convertButton;
    private String parsedData;
    private String amountStr;
    private TextView conversionTxtView;
    private Spinner currencySpinner;
    private Spinner convertToCurrencySpinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);

        currencySpinner = findViewById(R.id.currencySpinner);
        convertToCurrencySpinner = findViewById(R.id.convertToCurrencySpinner);
        // Set up the raw links to the graphical components

        flagImageView = findViewById(R.id.flagImageView);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.currency_array, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        convertToCurrencySpinner.setAdapter(adapter);

        conversionTxtView = findViewById(R.id.conversionTxtView);

        Intent intent = getIntent();
        parsedData = intent.getStringExtra("parsedData");

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCurrency = currencySpinner.getSelectedItem().toString();
                updateFlagImage(selectedCurrency, flagImageView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        backButton = findViewById(R.id.itemCurrency);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Mains.this, MainActivity.class);
                startActivity(intent);
            }
        });

        convertButton = findViewById(R.id.convertCurrenciesButton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.edtText);
                amountStr = editText.getText().toString();

                if (amountStr.isEmpty()) {
                    Toast.makeText(Activity_Mains.this, "Please enter the amount", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    String selectedCurrency = currencySpinner.getSelectedItem().toString();
                    String targetCurrency = convertToCurrencySpinner.getSelectedItem().toString();

                    double exchangeRateToBase = getExchangeRateForSelectedCurrency(selectedCurrency);
                    double exchangeRateToTarget = getExchangeRateForSelectedCurrency(targetCurrency);

                    double result = (amount / exchangeRateToBase) * exchangeRateToTarget;

                    // Display the result with appropriate color
                    if (result > amount) {
                        conversionTxtView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else if (result < amount) {
                        conversionTxtView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        conversionTxtView.setTextColor(getResources().getColor(android.R.color.black));
                    }

                    conversionTxtView.setText(" " + result);
                } catch (NumberFormatException e) {
                    Toast.makeText(Activity_Mains.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private double getExchangeRateForSelectedCurrency(String selectedCurrency) {
        if (parsedData != null) {
            // Parse the data and retrieve the exchange rate for the selected currency
            double exchangeRate = parseExchangeRateFromData(parsedData, selectedCurrency);
            if (exchangeRate != -1.0) {
                return exchangeRate;
            }
        }

        // If parsedData is null or exchange rate is not found, use default rates
        switch (selectedCurrency) {
            case "USD":
                return 1.28; // 1 GBP = 1.28 USD
            case "EUR":
                return 1.16; // 1 GBP = 1.16 EUR
            case "JPY":
                return 180.93; // 1 GBP = 180.93 JPY
            // Add more cases for other currencies as needed
            default:
                return 1.0; // Default exchange rate (1.0 means no conversion, same currency)
        }
    }

    private double parseExchangeRateFromData(String data, String currencyCode) {
        // Implement your logic to parse the exchange rate for the specified currency from the data
        // Return -1.0 if the currency code is not found or parsing fails
        // Otherwise, return the parsed exchange rate
        double exchangeRate = -1.0;
        // Add your parsing code here
        return exchangeRate;
    }

    private void updateFlagImage(String selectedCurrency, ImageView flagImageView) {
        int flagResId = 0;

        switch (selectedCurrency) {
            case "USD":
                flagResId = R.drawable.us; // Use the actual resource name for the flag
                break;
            case "EUR":
                flagResId = R.drawable.gr; // Use the actual resource name for the flag
                break;
            case "JPY":
                flagResId = R.drawable.jp; // Use the actual resource name for the flag
                break;
            // Add more cases for other currencies as needed
            default:
                // Set a default flag image or handle as needed
                break;
        }

        if (flagResId != 0) {
            flagImageView.setImageResource(flagResId);
        }
    }
}
