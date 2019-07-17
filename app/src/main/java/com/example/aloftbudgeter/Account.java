package com.example.aloftbudgeter;

import java.util.Calendar;

class Account {

    private Calendar weekStart;

    public Calendar getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(Calendar seedDate) {
        this.weekStart = Aloft.getStartOfWeek(seedDate);
    }
}
