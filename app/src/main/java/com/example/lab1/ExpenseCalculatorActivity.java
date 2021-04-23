package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;


public class ExpenseCalculatorActivity extends AppCompatActivity {
    String selectedMonth = "";
    String selectedYear = "";

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_calculator);

        Toolbar toolbar = findViewById(R.id.toolbar_back_button);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Bundle arguments = getIntent().getExtras();
        selectedMonth = arguments.get("selectedMonth").toString();
        selectedYear = arguments.get("selectedYear").toString();

        dbHelper = new DatabaseHelper(this);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etOperationName = findViewById(R.id.operation);
                EditText etOperationCost = findViewById(R.id.cost);

                String operationName = etOperationName.getText().toString();
                String operationCost = etOperationCost.getText().toString();
                String operationDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                if (!operationName.isEmpty() || !operationCost.isEmpty()) {
                    db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    switch (view.getId()) {
                        case R.id.add_expense_button:
                            db.beginTransaction();
                            try {
                                contentValues.put(dbHelper.COLUMN_NAME, operationName);
                                contentValues.put(dbHelper.COLUMN_COST,
                                                  Integer.parseInt("-" + operationCost));
                                contentValues.put(dbHelper.COLUMN_DATE, operationDate);
                                db.insert(dbHelper.TABLE_OPERATIONS, null, contentValues);
                                db.setTransactionSuccessful();
                            } finally {
                                db.endTransaction();
                            }
                            break;
                        case R.id.add_income_button:
                            db.beginTransaction();
                            try {
                                contentValues.put(dbHelper.COLUMN_NAME, operationName);
                                contentValues.put(dbHelper.COLUMN_COST,
                                        Integer.parseInt(operationCost));
                                contentValues.put(dbHelper.COLUMN_DATE, operationDate);
                                db.insert(dbHelper.TABLE_OPERATIONS, null, contentValues);
                                db.setTransactionSuccessful();
                            } finally {
                                db.endTransaction();
                            }
                            break;
                    }

                    getAllData();
                    dbHelper.close();
                    etOperationName.setText("");
                    etOperationCost.setText("");
                }
            }
        };

        Button addExpenseButton = findViewById(R.id.add_expense_button);
        addExpenseButton.setOnClickListener(onClickListener);

        Button addIncomeButton = findViewById(R.id.add_income_button);
        addIncomeButton.setOnClickListener(onClickListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        getAllData();
    }

    public void getAllData() {
        LinearLayout operationsList = findViewById(R.id.operations_list);
        operationsList.removeAllViews();

        EditText etTotalExpenses = findViewById(R.id.total_expenses);
        EditText etTotalIncome = findViewById(R.id.total_income);
        EditText etTotalBalance = findViewById(R.id.total_balance);

        int totalExpenses = 0;
        int totalIncome = 0;
        int totalBalance = 0;

        db = dbHelper.getReadableDatabase();

        cursor =  db.rawQuery("SELECT * FROM "+ dbHelper.TABLE_OPERATIONS + " WHERE " +
                              dbHelper.COLUMN_DATE + " LIKE '%-" + selectedMonth + "-" +
                              selectedYear + "'", null);

        if (cursor.moveToFirst()) {
            do {
                String operationID =
                        cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_ID));
                String operationName =
                        cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NAME));
                String operationCost =
                        cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_COST));
                String operationDate =
                        cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_DATE));

                if (Integer.parseInt(operationCost) > 0) {
                    totalIncome += Integer.parseInt(operationCost);
                    createOperationCard(operationName, operationCost, operationDate, operationID,true);
                }
                else  {
                    totalExpenses += Integer.parseInt(operationCost);
                    createOperationCard(operationName, operationCost, operationDate, operationID,false);
                }
            } while (cursor.moveToNext());
        }
        else {
            TextView tvNoData = new TextView(this);
            tvNoData.setText("Данные отсутствуют");

            operationsList.addView(tvNoData);
        }

        totalBalance = totalExpenses + totalIncome;

        etTotalExpenses.setText(Integer.toString(totalExpenses) + " ₽");
        etTotalIncome.setText(Integer.toString(totalIncome) + " ₽");
        etTotalBalance.setText(Integer.toString(totalBalance) + " ₽");
        cursor.close();
        db.close();
    }

    public void createOperationCard(String operationName, String operationCost,
                                    String operationDate, String operationID, Boolean isIncome) {
        LinearLayout operationsList = findViewById(R.id.operations_list);

        View operation = getLayoutInflater().inflate(R.layout.operation_card, null);
        LinearLayout operationData = operation.findViewById(R.id.operation_data);

        if (isIncome) {
            operationData.setBackgroundResource(R.drawable.green_operation_background);
        }
        else {
            operationData.setBackgroundResource(R.drawable.red_operation_background);
        }

        TextView tvOperationName = operation.findViewById(R.id.operation_name);
        tvOperationName.setText(operationName);

        TextView tvOperationDate = operation.findViewById(R.id.operation_date);
        tvOperationDate.setText(operationDate);

        TextView tvOperationCost = operation.findViewById(R.id.operation_cost);
        tvOperationCost.setText(operationCost + " ₽");

        operationsList.addView(operation);

        ImageButton deleteButton = operation.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    db.delete(dbHelper.TABLE_OPERATIONS, "_id = " + operationID, null);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getAllData();
                db.close();
                cursor.close();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        cursor.close();
    }
}