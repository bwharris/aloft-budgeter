package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    Account account = null;
    List<Integer> catDisplayIndexes = null;
    int position = -1;
    final Integer[] frequencyView = new Integer[] {
            R.id.category_once_month_selector,
            R.id.category_month_label,
            R.id.category_once_day_selector,
            R.id.category_weeks_every_label,
            R.id.category_week_selector,
            R.id.category_weeks_label,
            R.id.category_monthly_on_label,
            R.id.category_monthly_day_selector
        };

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
                new ArrayList<Integer>()
            );
        position = Aloft.tryGetPosition(
                getIntent().getExtras(),
                getApplication().getString(R.string.extra_position),
                catDisplayIndexes.size()
            );

        if(account == null || catDisplayIndexes == null) {
            Aloft.saveStartDate(this);
            startActivity(Aloft.getAccountActivityIntent(getApplicationContext(), true));
            finish();
            return;
        }

        ArrayAdapter<String> categoryDataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                account.getCategories(catDisplayIndexes)
            );
        categoryDataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ((Spinner)findViewById(R.id.category_selector)).setAdapter(categoryDataAdapter);
        ((Spinner)findViewById(R.id.category_selector)).setSelection(position);
        ((Spinner)findViewById(R.id.category_selector)).setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    findViewById(R.id.category_name).setVisibility(
                            i == catDisplayIndexes.size() ? View.VISIBLE : View.GONE
                    );
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        findViewById(R.id.category_name).setVisibility(
                position == catDisplayIndexes.size() ? View.VISIBLE : View.GONE
            );

        ArrayAdapter<Integer> weekDataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Aloft.getIntegerSequence(1,10,1)
            );
        weekDataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ((Spinner)findViewById(R.id.category_week_selector)).setAdapter(weekDataAdapter);

        ArrayAdapter<Integer> dayDataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Aloft.getIntegerSequence(1, 31, 1)
            );
        weekDataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ((Spinner)findViewById(R.id.category_monthly_day_selector)).setAdapter(dayDataAdapter);

        ArrayAdapter<Integer> onceMonthDataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Aloft.getIntegerSequence(1,12, 1)
            );
        onceMonthDataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ((Spinner)findViewById(R.id.category_once_month_selector)).setAdapter(onceMonthDataAdapter);

        ((Spinner)findViewById(R.id.category_once_day_selector)).setAdapter(dayDataAdapter);


    }

    public void onRadioButtonClick(View view){
        for(Integer i : frequencyView){
            findViewById(i).setVisibility(View.GONE);
        }
        switch (view.getId()){
            case R.id.category_once:
                findViewById(R.id.category_once_month_selector).setVisibility(View.VISIBLE);
                findViewById(R.id.category_month_label).setVisibility(View.VISIBLE);
                findViewById(R.id.category_once_day_selector).setVisibility(View.VISIBLE);
                break;
            case R.id.category_weekly:
                findViewById(R.id.category_weeks_every_label).setVisibility(View.VISIBLE);
                findViewById(R.id.category_week_selector).setVisibility(View.VISIBLE);
                findViewById(R.id.category_weeks_label).setVisibility(View.VISIBLE);
                break;
            case R.id.category_monthly:
                findViewById(R.id.category_monthly_on_label).setVisibility(View.VISIBLE);
                findViewById(R.id.category_monthly_day_selector).setVisibility(View.VISIBLE);
                break;
        }
    }
}
