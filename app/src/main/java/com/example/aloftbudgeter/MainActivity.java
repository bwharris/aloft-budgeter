package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar weekStart = Aloft.tryGetWeekStart(
                getIntent().getExtras(),
                getApplicationContext(),
                Calendar.getInstance()
            );

        ((TextView)findViewById(R.id.main_date)).setText(Aloft.getPrintableDate(
                //  TODO: change to date off an account object
                weekStart
        ));
    }
}
