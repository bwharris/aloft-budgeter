package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar weekStart = Aloft.tryGetWeekStart(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                Calendar.getInstance());
        final DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        accounts = databaseHandler.getAccounts(weekStart);
        final Account account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                accounts.size() > 0 ? accounts.get(0) : null
            );

        if(account == null){
            Aloft.saveStartDate(this);
            startActivity(Aloft.getAccountActivityIntent(getApplicationContext(), true));
            finish();
            return;
        }

        final Calendar lastWeek = (Calendar) account.getWeekStart().clone();
        lastWeek.add(Calendar.DATE, -7);

        findViewById(R.id.main_previous).setVisibility(
                lastWeek.getTimeInMillis() >= Aloft.getStartDate(getApplicationContext())
                        .getTimeInMillis() ? View.VISIBLE : View.GONE
            );
        findViewById(R.id.main_previous).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getMainActivityIntent(
                                getApplicationContext(),
                                databaseHandler.getAccountByID(account.getAccountID(), lastWeek)
                            ));
                        finish();

                        return;
                    }
                }
        );

        final Calendar nextWeek = (Calendar)account.getWeekStart().clone();
        nextWeek.add(Calendar.DATE, 7);

        findViewById(R.id.main_next).setVisibility(
                nextWeek.getTimeInMillis() <= Aloft.getStartDate(getApplicationContext())
                        .getTimeInMillis() + (52 * Aloft.weekToMilliSec) ?
                        View.VISIBLE : View.GONE
            );
        findViewById(R.id.main_next).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getMainActivityIntent(
                                getApplicationContext(),
                                databaseHandler.getAccountByID(account.getAccountID(), nextWeek)
                            ));
                        finish();

                        return;
                    }
                }
        );

        ((TextView)findViewById(R.id.main_date)).setText(Aloft.getPrintableDate(weekStart));
    }

    @Override
    public void onBackPressed(){}
}
