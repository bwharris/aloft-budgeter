package com.example.aloftbudgeter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

class Account implements Serializable {

    private Calendar weekStart = Calendar.getInstance();
    private String name;
    private List<Category> categories = new ArrayList<>();
    private int accountID;

    Account(Calendar seedDate) {
        setWeekStart(seedDate);
    }

    Account(int accountID, String name, Calendar weekStart, List<Category> categories) {
        setAccountID(accountID);
        setName(name);
        setWeekStart(weekStart);
        setCategories(categories);
    }

    private void setWeekStart(Calendar seedDate) { this.weekStart = Aloft.getStartOfWeek(seedDate); }

    Calendar getWeekStart() { return weekStart; }

    private void setName(String name) { this.name = name; }

    String getName() { return name; }

    private void setCategories(List<Category> categories) { this.categories = categories; }

    List<Category> getCategories() { return categories; }

    private void setAccountID(int accountID) { this.accountID = accountID; }

    int getAccountID() { return accountID; }

    void addCategory(Category category) {
        this.categories.add(category);
    }

    int getExpenses(Context context, boolean isActual) {
        int plannedExpenses = 0;

        for(Category category: this.categories){
            if(category.isExcludedFromExpense(context)){ }
            else{ plannedExpenses += category.getBudgetItemSum(isActual); }
        }

        return plannedExpenses;
    }

    void updateFromView(Activity activity, View view){
        DatabaseHandler databaseHandler = null;
        HashMap<String, Integer> categoryIndexes = null;
        int value = 0;
        BudgetItem budgetItem;
        switch (view.getId()){
            case R.id.account_name:
                setName(((EditText)view).getText().toString());

                break;

            case R.id.account_start:
                value = Aloft.tryParseInteger((EditText)view, 0);
                categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
                this.categories.get(categoryIndexes.get(
                        activity.getResources().getString(R.string.core_start_balance)
                    )).addBudgetItem(new BudgetItem(this.weekStart, value, false));

                break;

            case R.id.account_cash:
                categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
                this.categories.get(
                        categoryIndexes.get(activity.getResources().getString(R.string.core_cash))
                    ).addBudgetItems(
                            activity,
                            this.weekStart,
                            Aloft.tryParseInteger((EditText)view, 0),
                            Aloft.Frequency.weekly
                        );

                break;

            case R.id.category_value:
                databaseHandler = new DatabaseHandler(activity);
                categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
                value = ((RadioGroup)activity.findViewById(R.id.category_freq))
                        .getCheckedRadioButtonId();
                Category category = this.categories.get(categoryIndexes.get(
                        ((TextView)activity.findViewById(R.id.category_name)).getText().toString()
                    ));
                category.removeBudgetItems(false);
                category.removeBudgetItems(true);
                category.addBudgetItems(
                        activity,
                        this.weekStart,
                        Aloft.tryParseInteger((EditText)view, 0),
                        value == R.id.category_once ? Aloft.Frequency.once
                                : value == R.id.category_monthly ? Aloft.Frequency.monthly
                                : Aloft.Frequency.weekly
                    );
                for(BudgetItem item: category.getBudgetItems()){
                    databaseHandler.create(item, category.getCategoryID());
                }

                break;

            case R.id.main_contingency:
            case R.id.main_end_bal:
                databaseHandler = new DatabaseHandler(activity);
                value = Aloft.tryParseInteger((TextView)view, 0);
                categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
                budgetItem = this.categories.get(categoryIndexes.get(
                        view.getId() == R.id.main_contingency ?
                            activity.getApplicationContext().getString(R.string.req_contingency)
                                : activity.getApplicationContext().getString(R.string.core_end_balance)
                    )).getBudgetItem(false);

                if(budgetItem == null){
                    budgetItem = new BudgetItem(
                            databaseHandler.create(
                                    new BudgetItem(this.weekStart, 0, false),
                                    this.categories.get(categoryIndexes.get(
                                            view.getId() == R.id.main_contingency ?
                                                activity.getApplicationContext()
                                                        .getString(R.string.req_contingency)
                                                    : activity.getApplicationContext()
                                                        .getString(R.string.core_end_balance)
                                        )).getCategoryID()
                                ),
                            this.categories.get(categoryIndexes.get(
                                    view.getId() == R.id.main_contingency ?
                                            activity.getApplicationContext()
                                                    .getString(R.string.req_contingency)
                                                : activity.getApplicationContext()
                                                    .getString(R.string.core_end_balance)
                                )).getCategoryID(),
                            this.weekStart,
                            0,
                            false
                        );
                    this.categories.get(categoryIndexes.get(
                            view.getId() == R.id.main_contingency ?
                                    activity.getApplicationContext()
                                            .getString(R.string.req_contingency)
                                        : activity.getApplicationContext()
                                            .getString(R.string.core_end_balance)
                        )).addBudgetItem(budgetItem);
                }

                this.categories.get(categoryIndexes.get(
                        view.getId() == R.id.main_contingency ?
                                activity.getApplicationContext().getString(R.string.req_contingency)
                                    : activity.getApplicationContext()
                                        .getString(R.string.core_end_balance)
                    )).updateBudgetItem(budgetItem.getBudgetItemID(), value);
                databaseHandler.update(budgetItem.getBudgetItemID(), value);

                if(view.getId() == R.id.main_end_bal){
                    Calendar nextWeek = (Calendar) this.weekStart.clone();
                    nextWeek.add(Calendar.DATE, 7);
                    budgetItem = databaseHandler.getCategoryByAccountID(this.accountID, nextWeek)
                                .get(categoryIndexes.get(activity.getApplicationContext()
                                    .getString(R.string.core_start_balance)
                            )).getBudgetItem(false);

                    if(budgetItem == null){
                        databaseHandler.create(
                            new BudgetItem(nextWeek, value, false),
                            this.categories.get(categoryIndexes.get(activity.getApplicationContext()
                                    .getString(R.string.core_start_balance)
                                )).getCategoryID()
                        );
                    }
                    else{ databaseHandler.update(budgetItem.getBudgetItemID(), value); }
                }

                break;

            case R.id.budget_actual:
            case R.id.budget_plan:
                databaseHandler = new DatabaseHandler(activity);
                categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
                value = Aloft.tryParseInteger((TextView)view, 0);
                for(
                    BudgetItem item: this.categories.get(categoryIndexes.get(
                            ((TextView)activity.findViewById(R.id.budget_category)).getText().toString()
                        )).getBudgetItems(view.getId() == R.id.budget_actual)
                ){
                    databaseHandler.removeBudgetItem(item.getBudgetItemID());
                }

                databaseHandler.create(
                        new BudgetItem(this.weekStart, value, view.getId() == R.id.budget_actual),
                        this.categories.get(categoryIndexes.get(
                                ((TextView)activity.findViewById(R.id.budget_category)).getText().toString()
                            )).getCategoryID()
                    );
                break;

            default: break;
        }
    }
}
