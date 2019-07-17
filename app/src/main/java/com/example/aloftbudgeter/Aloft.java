package com.example.aloftbudgeter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

class Aloft {


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

    static boolean hasAccount(Bundle extras, Context context) {
        return extras != null &&
                extras.get(context.getString(R.string.extra_account)) != null &&
                extras.get(context.getString(R.string.extra_account)).getClass() == Account.class;
    }

    static Calendar tryGetWeekStart(
            Bundle extras,
            Context context,
            Calendar defaultSeedDate)
    {
        return hasAccount(extras, context) ?
                ((Account)extras.get(context.getString(R.string.extra_account))).getWeekStart()
                : getStartOfWeek(defaultSeedDate);
    }
}