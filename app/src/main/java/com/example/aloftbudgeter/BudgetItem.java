package com.example.aloftbudgeter;

import java.io.Serializable;
import java.util.Calendar;

class BudgetItem implements Serializable {
    private Calendar weekStart;
    private int value;
    private boolean isActual;
    private int budgetItemID;
    private int categoryID;

    BudgetItem(Calendar seedDate, int value, boolean isActual) {
        setWeekStart(seedDate);
        setValue(value);
        setIsActual(isActual);
    }

    BudgetItem(int budgetItemID, int categoryID, Calendar seedDate, int value, boolean isActual) {
        setBudgetItemID(budgetItemID);
        setCategoryID(categoryID);
        setWeekStart(seedDate);
        setValue(value);
        setIsActual(isActual);
    }

    private void setWeekStart(Calendar seedDate) { this.weekStart =  Aloft.getStartOfWeek(seedDate); }

    Calendar getWeekStart() { return weekStart; }

    private void setValue(int value) { this.value = value; }

    int getValue() { return value; }

    private void setIsActual(boolean isActual) { this.isActual = isActual; }

    boolean getIsActual() { return isActual; }

    private void setBudgetItemID(int budgetItemID) { this.budgetItemID = budgetItemID; }

    int getBudgetItemID() { return budgetItemID; }

    private void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    int getCategoryID() { return categoryID; }

    void updateValue(int value) { setValue(value); }
}
