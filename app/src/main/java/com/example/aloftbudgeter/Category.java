package com.example.aloftbudgeter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Category implements Serializable {
    private String name;
    private List<BudgetItem> budgetItems = new ArrayList<>();

    Category(String name) {
        this.setName(name);
    }

    String getName() { return name; }

    private void setName(String name) { this.name = name; }

    void addBudgetItem(BudgetItem budgetItem) {
        this.budgetItems.add(budgetItem);
    }
}
