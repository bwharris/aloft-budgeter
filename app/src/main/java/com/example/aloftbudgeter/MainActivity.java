package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

        ((TextView)findViewById(R.id.main_date)).setText(Aloft.getPrintableDate(weekStart));

//        // test
//        StringBuffer testText = new StringBuffer("Account Name : ");
//        testText.append(account.getName());
//        testText.append("\nIncludes Req Categories: ");
//        testText.append(account.getHasReqCategories(this));
//        for(Category category: account.getCategories()){
//            testText.append("\n");
//            testText.append(category.getName());
//            testText.append(": ");
//            for(BudgetItem budgetItem: category.getBudgetItems()){
//                testText.append(Aloft.getPrintableDate(budgetItem.getWeekStart()));
//                testText.append("| ");
//                testText.append(budgetItem.getValue());
//                testText.append("; ");
//            }
//        }
//        ((TextView)findViewById(R.id.main_date)).setText(testText.toString());
//        // end of test
    }

    @Override
    public void onBackPressed(){}
}
