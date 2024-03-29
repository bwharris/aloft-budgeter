package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Account account = null;
    private List<Account> accounts;
    private List<Integer> catDisplayIndexes = new ArrayList<>();

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
        account = Aloft.tryGetAccount(
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

        HashMap<String, Integer> categoryIndexes =
                Aloft.getCategoryIndexHashMap(account.getCategories());
        ((TextView)findViewById(R.id.main_date))
                .setText(Aloft.getPrintableDate(weekStart));

        int startingBalance = account.getCategories().get(categoryIndexes.get(
                getApplicationContext().getString(R.string.core_start_balance)
            )).getBudgetItemSum(false);
        int contingency = account
                .getExpenses(getApplicationContext(), false) / Aloft.contingencyPercent;

        ((TextView)findViewById(R.id.main_contingency)).setText(String.valueOf(contingency));

        account.updateFromView(this, findViewById(R.id.main_contingency));

        int plannedIncome = Aloft.tryGetValue(
                account.getCategories()
                    .get(categoryIndexes.get(getApplicationContext().getString(R.string.req_income)))
                    .getBudgetItem(false),
                0
            );
        int actualIncome = Aloft.tryGetValue(
                account.getCategories()
                        .get(categoryIndexes.get(getApplicationContext().getString(R.string.req_income)))
                        .getBudgetItem(true),
                0
            );

        ((TextView)findViewById(R.id.main_start_bal)).setText(String.valueOf(startingBalance));
        ((TextView)findViewById(R.id.main_changes_planned)).setText(String.valueOf(
                plannedIncome - account.getExpenses(getApplicationContext(), false)
            ));
        ((TextView)findViewById(R.id.main_changes_actual)).setText(String.valueOf(
                actualIncome - account.getExpenses(getApplicationContext(), true)
            ));

        int index = 0;
        for(Category category: account.getCategories()){
            if(!category.isExcludedFromMainListView(getApplicationContext())) {
                catDisplayIndexes.add(index++);
            }
            else{ index++; }
        }

        Aloft.displayCategoryList(
                this, (ListView)findViewById(R.id.main_categories), account, catDisplayIndexes
            );

        findViewById(R.id.main_add).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int viewID = R.id.main_new_cat;

                    if(TextUtils.isEmpty(((EditText)findViewById(viewID)).getText().toString())){
                        ((EditText)findViewById(viewID)).setError("A name is needed");
                    }
                    else {
                        account.addCategory(new Category(
                                databaseHandler.create(
                                        new Category(
                                                ((EditText)findViewById(viewID)).getText().toString()
                                            ),
                                        account.getAccountID()
                                    ),
                                account.getAccountID(),
                                ((EditText)findViewById(viewID)).getText().toString(),
                                new ArrayList<BudgetItem>()
                            ));
                        catDisplayIndexes.add(account.getCategories().size() - 1);
                        Aloft.displayCategoryList(
                                MainActivity.this,
                                (ListView)findViewById(R.id.main_categories),
                                account,
                                catDisplayIndexes
                            );
                        ((EditText)findViewById(viewID)).setText("");
                    }
                }
            }
        );

        int endingBalance = startingBalance
                + plannedIncome
                - account.getExpenses(getApplicationContext(), false);

        ((TextView)findViewById(R.id.main_end_bal)).setText(String.valueOf(endingBalance));
        account.updateFromView(this, findViewById(R.id.main_end_bal));

        findViewById(R.id.main_save).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getReconcilerActivity(getApplicationContext(), account));
                    }
                }
        );
        findViewById(R.id.main_save).setVisibility(View.GONE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.clear();

        for(Account userAccount: accounts){
            menu.add(Menu.NONE, userAccount.getAccountID(), Menu.NONE, userAccount.getName())
                .setShowAsAction(
                        account.getAccountID() == userAccount.getAccountID() ?
                                MenuItem.SHOW_AS_ACTION_ALWAYS
                                : MenuItem.SHOW_AS_ACTION_NEVER
                    );
        }

        menu.add(Menu.NONE, -1, Menu.NONE, "Add An Account").setShowAsAction(
                accounts.size() == 0 ? MenuItem.SHOW_AS_ACTION_ALWAYS : MenuItem.SHOW_AS_ACTION_NEVER
            );

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case -1:
                startActivity(Aloft.getAccountActivityIntent(getApplicationContext(), true));
                finish();

                return false;

            default:
                if(item.getItemId() == account.getAccountID()) { return false;}

                for(Account userAccount: accounts){
                    if(userAccount.getAccountID() == item.getItemId()){
                        account = userAccount;
                        break;
                    }
                }

                if(account == null){ return false; }

                startActivity(Aloft.getMainActivityIntent(getApplicationContext(), account));
                finish();

                return false;
        }
    }

    @Override
    public void onBackPressed(){}
}
