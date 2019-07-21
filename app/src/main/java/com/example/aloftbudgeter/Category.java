package com.example.aloftbudgeter;

import android.app.Activity;
import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Category implements Serializable {
    private String name;
    private List<BudgetItem> budgetItems = new ArrayList<>();
    private int categoryID;
    private int accountID;

    Category(String name) {
        this.setName(name);
    }

    public Category(int categoryID, int accountID, String name, List<BudgetItem> budgetItems) {
        setCategoryID(categoryID);
        setAccountID(accountID);
        setName(name);
        setBudgetItems(budgetItems);
    }

    private void setName(String name) { this.name = name; }

    String getName() { return name; }

    private void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    int getCategoryID() { return categoryID; }

    private void setAccountID(int accountID) { this.accountID = accountID; }

    int getAccountID() { return accountID; }

    private void setBudgetItems(List<BudgetItem> budgetItems) { this.budgetItems = budgetItems; }

    List<BudgetItem> getBudgetItems() { return budgetItems; }

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

    BudgetItem getBudgetItem(boolean isActual) {
        for(BudgetItem budgetItem: this.budgetItems){
            if(budgetItem.getIsActual() == isActual) { return budgetItem; }
        }

        return null;
    }

    int getBudgetItemSum(boolean isActual) {
        int sum = 0;
        for(BudgetItem budgetItem: this.budgetItems){
            sum += budgetItem.getIsActual() == isActual ? budgetItem.getValue() : 0;
        }

        return sum;
    }

    boolean isExcludedFromExpense(Context context) {
        for(String name: context.getResources().getStringArray(R.array.excludeFromExpense)){
            if(this.name.equals(name)){ return true; }
        }

        return false;
    }

    public void updateBudgetItem(int budgetItemID, int value) {
        for(BudgetItem budgetItem: this.budgetItems){
            if(budgetItem.getBudgetItemID() == budgetItemID) { budgetItem.updateValue(value); }
        }
    }
}
