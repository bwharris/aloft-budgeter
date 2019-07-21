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
    private ArrayList<Category> categories = new ArrayList<>();

    public Account(Calendar seedDate) {
        setWeekStart(seedDate);
    }

    private void setWeekStart(Calendar seedDate) { this.weekStart = Aloft.getStartOfWeek(seedDate); }

    Calendar getWeekStart() { return weekStart; }

    private void setName(String name) { this.name = name; }

    String getName() { return name; }

    private void setCategories(ArrayList<Category> categories) { this.categories = categories; }

    ArrayList<Category> getCategories() { return categories; }

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

                    // reduce by spending cash
                    value -= this.categories.get(categoryIndexes.get(
                            activity.getResources().getString(R.string.core_cash)
                        )).getBudgetItems().get(0).getValue();

                    // add ending balance
                    this.categories.get(categoryIndexes.get(
                            activity.getResources().getString(R.string.core_end_balance)
                        )).addBudgetItem(new BudgetItem(week, value,false));

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
}
