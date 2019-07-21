package com.example.aloftbudgeter;

import android.app.Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Category implements Serializable {
    private String name;
    private List<BudgetItem> budgetItems = new ArrayList<>();

    Category(String name) {
        this.setName(name);
    }

    String getName() { return name; }

    private void setName(String name) { this.name = name; }

    List<BudgetItem> getBudgetItems() { return this.budgetItems; }

    void addBudgetItem(BudgetItem budgetItem) { this.budgetItems.add(budgetItem); }

    void addBudgetItems(Activity activity, Calendar startDate, int value, Aloft.Frequency frequency) {
        long budgetStartDate = Aloft.getStartDate(activity.getApplicationContext()).getTimeInMillis();
        long endDate = budgetStartDate + (52 * Aloft.weekToMilliSec);
        long currentDate = Aloft.getStartOfWeek(startDate).getTimeInMillis();

        do{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentDate);
            this.budgetItems.add(new BudgetItem(calendar, value, false));

            currentDate += frequency == Aloft.Frequency.once ? endDate
                    : frequency == Aloft.Frequency.weekly ? Aloft.weekToMilliSec
                    : 0;

            if(frequency == Aloft.Frequency.monthly){
                Calendar nextDate = (Calendar) startDate.clone();
                nextDate.add(Calendar.MONTH, 1);
                currentDate = nextDate.getTimeInMillis();
            }
        } while (currentDate < endDate);
    }
}
