package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private Account account = null;
    private List<Integer> catDisplayIndexes = null;
    private int position = -1;
    private final Integer[] frequencyView = new Integer[] {
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

        if(account == null || catDisplayIndexes == null || position == -1) {
            Aloft.saveStartDate(this);
            startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
            finish();
            return;
        }

        findViewById(R.id.category_back).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(Aloft.getBudgetItemActivityIntent(
                            getApplicationContext(),
                            account,
                            catDisplayIndexes,
                            position
                        ));
                }
            }
        );
        ((TextView)findViewById(R.id.category_name)).setText(
                account.getCategories().get(catDisplayIndexes.get(position)).getName()
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
        ((Spinner)findViewById(R.id.category_monthly_day_selector))
                .setSelection((Calendar.getInstance().get(Calendar.DATE)) - 1);

        ArrayAdapter<Integer> onceMonthDataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Aloft.getIntegerSequence(1,12, 1)
            );
        onceMonthDataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Calendar currentDate = Calendar.getInstance();

        ((Spinner)findViewById(R.id.category_once_month_selector)).setAdapter(onceMonthDataAdapter);
        ((Spinner)findViewById(R.id.category_once_month_selector))
                .setSelection(currentDate.get(Calendar.MONTH));

        ((Spinner)findViewById(R.id.category_once_day_selector)).setAdapter(dayDataAdapter);
        ((Spinner)findViewById(R.id.category_once_day_selector))
                .setSelection((currentDate.get(Calendar.DATE)) - 1);

        findViewById(R.id.category_next).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(
                        TextUtils.isEmpty(((EditText)findViewById(R.id.category_value))
                            .getText().toString())
                    ){
                        ((EditText) findViewById(R.id.category_value)).setError("A value is needed");
                    }
                    else{
                        account.updateFromView(CategoryActivity.this, findViewById(R.id.category_value));
                        startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                        finish();

                        return;
                    }

                }
            }
        );
    }

    @Override
    public void onBackPressed(){}

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
