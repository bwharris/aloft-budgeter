package com.example.aloftbudgeter;

import java.io.Serializable;
import java.util.Calendar;

class BudgetItem implements Serializable {
    private Calendar weekStart;
    private int value;
    private boolean isActual;
    private int budgetItemID;
    private int catgoryID;

    BudgetItem(Calendar seedDate, int value, boolean isActual) {
        setWeekStart(seedDate);
        setValue(value);
        setIsActual(isActual);
    }

    public BudgetItem(int budgetItemID, int categoryID, Calendar seedDate, int value, boolean isActual) {
        setBudgetItemID(budgetItemID);
        setCatgoryID(categoryID);
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

    public void setBudgetItemID(int budgetItemID) { this.budgetItemID = budgetItemID; }

    public int getBudgetItemID() { return budgetItemID; }

    public void setCatgoryID(int catgoryID) { this.catgoryID = catgoryID; }

    public int getCatgoryID() { return catgoryID; }
}
