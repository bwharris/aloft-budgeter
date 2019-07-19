package com.example.aloftbudgeter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

class Account implements Serializable {

    private Calendar weekStart = Calendar.getInstance();
    private String name;
    private ArrayList<Category> categories = new ArrayList<>();

    public Account(Calendar seedDate) {
        setWeekStart(seedDate);
    }

    //Account(){}

//    Account(Calendar seedDate, String name, ArrayList<Category> categories) {
//        setWeekStart(seedDate);
//        setName(name);
//        setCategories(categories);
//    }

    private void setWeekStart(Calendar seedDate) { this.weekStart = Aloft.getStartOfWeek(seedDate); }

    Calendar getWeekStart() { return weekStart; }

    private void setName(String name) { this.name = name; }

    String getName() { return name; }

    private void setCategories(ArrayList<Category> categories) { this.categories = categories; }

    ArrayList<Category> getCategories() { return categories; }

    void addCategory(Category category) {
        this.categories.add(category);
    }

    void updateFromView(Activity activity, View view, HashMap<String, Integer> categoryIndexHashMap) {
        Category category;

        switch (view.getId()){
            case R.id.account_name:
                setName(((EditText)view).getText().toString());
                break;
            case R.id.account_start:
                this.categories.get(categoryIndexHashMap.get(
                        activity.getResources().getString(R.string.core_start_balance)
                    )).addBudgetItem(new BudgetItem(
                        Calendar.getInstance(),
                        Aloft.tryParseInteger(((EditText)view), 0),
                        true
                    ));
                break;
            case R.id.account_cash:
                this.categories.get(categoryIndexHashMap.get(
                        activity.getResources().getString(R.string.core_cash)
                    )).addBudgetItem(new BudgetItem(
                        Calendar.getInstance(),
                        Aloft.tryParseInteger(((EditText)view), 0),
                        false
                    ));

                break;
            default:
                break;
        }
    }
}
