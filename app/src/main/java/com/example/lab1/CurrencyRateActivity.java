package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyRateActivity extends AppCompatActivity {
    String selectedCurrencyFrom = "";
    String selectedCurrencyTo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_rate);

        Toolbar toolbar = findViewById(R.id.toolbar_back_button);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Spinner sCurrencyFrom = findViewById(R.id.currency_from);
        Spinner sCurrencyTo = findViewById(R.id.currency_to);


        sCurrencyFrom.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCurrencyFrom = (String)parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        sCurrencyTo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCurrencyTo = (String)parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

    }

    public void convert(View view) {
        EditText etAmount = findViewById(R.id.amount);
        String amount = etAmount.getText().toString();

        if (!amount.isEmpty()) {
            OkHttpClient client = new OkHttpClient();

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("currency-exchange.p.rapidapi.com")
                    .addPathSegment("exchange")
                    .addQueryParameter("to", selectedCurrencyTo)
                    .addQueryParameter("from", selectedCurrencyFrom)
                    .addQueryParameter("q", "1.0")
                    .build();


            Request request = new Request.Builder()
                    .url(httpUrl)
                    .get()
                    .addHeader("x-rapidapi-key", "f41805c0bfmsha0a1c802afbb81ep1719c5jsne475c779cedd")
                    .addHeader("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {


                        EditText etResult = findViewById(R.id.result);

                        Double responseVal = Double.parseDouble(response.body().string());
                        Double result = responseVal * Double.parseDouble(amount);
                        System.out.println(result);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etResult.setText(Double.toString(result));
                            }
                        });

                    }
                }

            });
        }
    }
}