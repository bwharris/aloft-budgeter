package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class BudgetItemActivity extends AppCompatActivity {
    private Account account = null;
    private List<Integer> catDisplayIndexes = null;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_item);

        account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                null
            );
        catDisplayIndexes = Aloft.tryGetCatDisplayIndexes(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_catDisplayIndexes),
                null
            );
        position = Aloft.tryGetPosition(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_position),
                position
            );

        if(account == null || catDisplayIndexes == null || position == -1){
            startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
            finish();

            return;
        }

        findViewById(R.id.budget_back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getMainActivityIntent(getApplicationContext(), account));
                        finish();

                        return;
                    }
                }
        );

        int plan = account.getCategories()
                .get(catDisplayIndexes.get(position)).getBudgetItemSum(false);
        int actual = account.getCategories()
                .get(catDisplayIndexes.get(position)).getBudgetItemSum(true);
        int diff = account.getCategories().get(catDisplayIndexes.get(position)).getName()
                == getApplicationContext().getString(R.string.req_income) ? actual - plan
                : plan - actual;

        ((TextView)findViewById(R.id.budget_date))
                .setText(Aloft.getPrintableDate(account.getWeekStart()));
        ((TextView)findViewById(R.id.budget_category))
                .setText(account.getCategories().get(catDisplayIndexes.get(position)).getName());
        ((EditText)findViewById(R.id.budget_plan)).setText(String.valueOf(plan));
        ((EditText)findViewById(R.id.budget_actual)).setText(String.valueOf(actual));
        ((TextView)findViewById(R.id.budget_diff)).setText(String.valueOf(diff));
    }

    @Override
    public void onBackPressed(){}
}
