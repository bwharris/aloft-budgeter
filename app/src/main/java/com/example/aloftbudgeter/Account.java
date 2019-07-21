package com.example.aloftbudgeter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

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

    public Account(Calendar seedDate) {
        setWeekStart(seedDate);
    }

    public Account(int accountID, String name, Calendar weekStart, List<Category> categories) {
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

    List<String> getCategories(List<Integer> indexes) {
        List<String> categories = new ArrayList<>();
        for(Integer i: indexes){ categories.add(this.categories.get(i).getName()); }
        categories.add("New");

        return categories;
    }

    boolean getHasReqCategories(Activity activity) {
        HashMap<String, Integer> categoryIndexHashMap =
                Aloft.getCategoryIndexHashMap(this.categories);
        for(String name: activity.getResources().getStringArray(R.array.reqCategories)) {
            if(categoryIndexHashMap.get(name) == null) { return false; }
        }

        return true;
    }

    void updateFromView(Activity activity, View view, Calendar calendar) {
        switch (view.getId()){
            case R.id.account_name:
                setName(((EditText)view).getText().toString());
                break;
            case R.id.account_start:
                int value = Aloft.tryParseInteger((EditText)view, 0);
                HashMap<String, Integer> categoryIndexes =
                        Aloft.getCategoryIndexHashMap(this.categories);
                long budgetStartDate = Aloft.getStartDate(activity.getApplicationContext()).getTimeInMillis();
                long endDate = budgetStartDate + (52 * Aloft.weekToMilliSec);
                long currentDate = Aloft.getStartOfWeek(calendar).getTimeInMillis();

                do{
                    Calendar week = Calendar.getInstance();
                    week.setTimeInMillis(currentDate);
                    // add starting balance
                    this.categories.get(categoryIndexes.get(
                            activity.getResources().getString(R.string.core_start_balance)
                        )).addBudgetItem(new BudgetItem(week, value, false));

//                    // reduce by spending cash
//                    value -= this.categories.get(categoryIndexes.get(
//                            activity.getResources().getString(R.string.core_cash)
//                        )).getBudgetItems().get(0).getValue();
//
//                    // add ending balance
//                    this.categories.get(categoryIndexes.get(
//                            activity.getResources().getString(R.string.core_end_balance)
//                        )).addBudgetItem(new BudgetItem(week, value,false));

                    // update date
                    currentDate += Aloft.weekToMilliSec;
                } while (currentDate < endDate);
                break;
            case R.id.account_cash:
                this.categories.get(
                        Aloft.getCategoryIndexHashMap(this.categories).get(
                                activity.getResources().getString(R.string.core_cash)
                            )
                    ).addBudgetItems(
                            activity,
                            calendar,
                            Aloft.tryParseInteger((EditText)view, 0),
                            Aloft.Frequency.weekly
                        );
                break;
            default:
                break;
        }
    }

    int getExpenses(Context context, boolean isActual) {
        int plannedExpenses = 0;

        for(Category category: this.categories){
            if(category.isExcludedFromExpense(context)){ }
            else{ plannedExpenses += category.getBudgetItemSum(isActual); }
        }

        return plannedExpenses;
    }

    public void updateContingency(Context context, DatabaseHandler databaseHandler, int value) {
        HashMap<String, Integer> categoryIndexes = Aloft.getCategoryIndexHashMap(this.categories);
        BudgetItem contingencyItem = this.categories.get(
                categoryIndexes.get(context.getString(R.string.req_contingency))
            ).getBudgetItem(false);

        if(contingencyItem == null){
            contingencyItem = new BudgetItem(
                    databaseHandler.create(
                            new BudgetItem(this.weekStart, 0, false),
                            this.categories.get(
                                    categoryIndexes.get(context.getString(R.string.req_contingency))
                                ).getCategoryID()
                        ),
                    this.categories.get(
                            categoryIndexes.get(context.getString(R.string.req_contingency))
                        ).getCategoryID(),
                    this.weekStart,
                    0,
                    false
                );
        }
        this.categories.get(
                categoryIndexes.get(context.getString(R.string.req_contingency))
            ).updateBudgetItem(contingencyItem.getBudgetItemID(), value);

    }
}
