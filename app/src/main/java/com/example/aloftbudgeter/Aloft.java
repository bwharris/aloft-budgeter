package com.example.aloftbudgeter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Aloft {

    static Intent getAccountActivityIntent(Context context) {
        Intent intent = new Intent(context, AccountActivity.class);

        return intent;
    }

    static Intent getCategoryActivityIntent(Context context) {
        Intent intent = new Intent(context, CategoryActivity.class);

        return intent;
    }

    static List<Integer> getIntegerSequence(int start, int numberOfIntegers, int step) {
        List<Integer> sequence = new ArrayList<>();
        for(int i = 0; i < numberOfIntegers * step;){ sequence.add(i += step); }

        return sequence;
    }

    static Intent getMainActivityIntent(Context context, Account account) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(context.getString(R.string.extra_account), account);

        return intent;
    }

    static StringBuffer getPrintableDate(Calendar calendar) {
        StringBuffer dateString = new StringBuffer();
        dateString.append(calendar.get(Calendar.MONTH) +1);
        dateString.append('/');
        dateString.append(calendar.get(Calendar.DAY_OF_MONTH));
        dateString.append('/');
        dateString.append(calendar.get(Calendar.YEAR));

        return  dateString;
    }

    static Calendar getStartOfWeek(Calendar seedDate) {
        Calendar calendar = (Calendar) seedDate.clone();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    static boolean hasAccount(Bundle extras, String name) {
        return extras != null &&
                extras.get(name) != null &&
                extras.get(name).getClass() == Account.class;
    }

    static Account tryGetAccount(Bundle extras, String name, Account defaultValue) {
        return hasAccount(extras, name) ?
                (Account)extras.get(name)
                : defaultValue;
    }

    static Calendar tryGetWeekStart(Bundle extras, String name, Calendar defaultSeedDate) {
        return hasAccount(extras, name) ?
                ((Account)extras.get(name)).getWeekStart()
                : getStartOfWeek(defaultSeedDate);
    }
}