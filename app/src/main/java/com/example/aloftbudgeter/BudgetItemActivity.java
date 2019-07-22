package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class BudgetItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_item);

        final Account account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                null
            );

        if(account == null){
            startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
            finish();

            return;
        }

        ((TextView)findViewById(R.id.budget_date))
                .setText(Aloft.getPrintableDate(account.getWeekStart()));

        findViewById(R.id.budget_back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                        finish();

                        return;
                    }
                }
            );
    }

    @Override
    public void onBackPressed(){}
}
