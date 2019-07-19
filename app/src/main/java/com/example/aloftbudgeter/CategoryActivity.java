package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    Account account = null;
    List<Integer> catDisplayIndexes = null;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                new Account(Calendar.getInstance())
            );
        catDisplayIndexes = Aloft.tryGetCatDisplayIndexes(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_catDisplayIndexes),
                null
            );

        if(account == null || catDisplayIndexes == null) { //|| position < 0){
            Aloft.saveStartDate(this);
            startActivity(Aloft.getAccountActivityIntent(getApplicationContext(), true));
            finish();
            return;
        }

        ((Spinner)findViewById(R.id.category_selector)).setAdapter(
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        account.getCategories(catDisplayIndexes)
                    )
            );

        ((Spinner)findViewById(R.id.category_week_selector)).setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1, 10, 1)
                    )
            );
        ((Spinner)findViewById(R.id.category_monthly_day_selector)).setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1, 31, 1)
                    )
            );
        ((Spinner)findViewById(R.id.category_once_month_selector)).setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1,12,1)
                    )
            );
        ((Spinner)findViewById(R.id.category_once_day_selector)).setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1,31,1)
                    )
            );
    }
}
