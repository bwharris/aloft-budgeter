package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar weekStart = Aloft.tryGetWeekStart(
                getIntent().getExtras(),
                getApplicationContext(),
                Calendar.getInstance());

        final Account account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext(),
                null
            );

        if(account == null){
            startActivity(Aloft.getAccountActivityIntent(getApplicationContext()));
            finish();
            return;
        }

        ((TextView)findViewById(R.id.main_date)).setText(Aloft.getPrintableDate(weekStart));

        Toast toast = Toast.makeText(
                getApplicationContext(),
                String.format(
                        "Name: %s \nStarting Balance:",
                        account.getName()
                    ),
                Toast.LENGTH_LONG
            );
        toast.show();
    }

    @Override
    public void onBackPressed(){}
}
