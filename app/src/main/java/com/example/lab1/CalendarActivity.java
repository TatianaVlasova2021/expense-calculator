package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    static final int NUM_YEARS = 3;
    String selectedMonth = "";
    String selectedYear = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar_back_button);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Spinner monthsSpinner = findViewById(R.id.months);
        Spinner yearsSpinner = findViewById(R.id.years);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        ArrayList<String> years = new ArrayList<String>();

        for(int i = year - NUM_YEARS + 1; i <= year; i++) {
            years.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsSpinner.setAdapter(adapter);

        monthsSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int month = position + 1;
                    if (month < 10) {
                        selectedMonth = '0' + Integer.toString(month);
                    }
                    else {
                        selectedMonth = Integer.toString(month);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            }
        );

        yearsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedYear = (String)parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    public void showExpenseCalculator(View view) {
        Intent intent = new Intent(this, ExpenseCalculatorActivity.class);
        intent.putExtra("selectedMonth", selectedMonth);
        intent.putExtra("selectedYear", selectedYear);
        startActivity(intent);
    }
}