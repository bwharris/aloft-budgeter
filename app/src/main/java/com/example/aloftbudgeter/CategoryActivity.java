package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ((Spinner)findViewById(R.id.category_week_selector)).setAdapter(
                new ArrayAdapter<Integer>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1, 10, 1)
                    )
            );
        ((Spinner)findViewById(R.id.category_monthly_day_selector)).setAdapter(
                new ArrayAdapter<Integer>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1, 31, 1)
                    )
            );
        ((Spinner)findViewById(R.id.category_once_month_selector)).setAdapter(
                new ArrayAdapter<Integer>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1,12,1)
                    )
            );
        ((Spinner)findViewById(R.id.category_once_day_selector)).setAdapter(
                new ArrayAdapter<Integer>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Aloft.getIntegerSequence(1,31,1)
                    )
            );
    }
}
