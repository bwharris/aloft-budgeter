package com.example.aloftbudgeter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

class Account implements Serializable {

    private Calendar weekStart = Calendar.getInstance();
    private String name;
    private ArrayList<Category> categories = new ArrayList<>();

    Account(){}

    Account(Calendar seedDate, String name, ArrayList<Category> categories) {
        setWeekStart(seedDate);
        setName(name);
        setCategories(categories);
    }

    private void setWeekStart(Calendar seedDate) { this.weekStart = Aloft.getStartOfWeek(seedDate); }

    Calendar getWeekStart() { return weekStart; }

    private void setName(String name) { this.name = name; }

    String getName() { return name; }

    private void setCategories(ArrayList<Category> categories) { this.categories = categories; }

    ArrayList<Category> getCategories() { return categories; }
}