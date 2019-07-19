package com.example.aloftbudgeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Aloft {

    static void displayCategoryList(
            final Activity activity,
            ListView listView,
            final Account account,
            final List<Integer> catDisplayIndexes) {
        listView.setAdapter(new CategoryListAdapter(activity, account.getCategories()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.startActivity(getCategoryActivityIntent(
                        activity.getApplicationContext(),
                        account,
                        catDisplayIndexes,
                        i
                    ));
                activity.finish();
                return;
            }
        });
    }

    static Intent getAccountActivityIntent(Context context, boolean needsReCats) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra(context.getString(R.string.extra_needsReqCats), needsReCats);

        return intent;
    }

    static Intent getCategoryActivityIntent(
            Context context,
            Account account,
            List<Integer> catDisplayIndexes,
            int position
    ) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(context.getString(R.string.extra_account), account);
        intent.putIntegerArrayListExtra(context.getString(
                R.string.extra_catDisplayIndexes),
                (ArrayList<Integer>) catDisplayIndexes
            );
        intent.putExtra(context.getString(R.string.extra_position), position);

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

    private static boolean hasNeedReqCats(Bundle extras, String name) {
        return extras != null &&
                extras.get(name) != null &&
                extras.get(name).getClass() == Boolean.class;
    }

    static Account tryGetAccount(Bundle extras, String name, Account defaultValue) {
        return hasAccount(extras, name) ?
                (Account)extras.get(name)
                : defaultValue;
    }

    static boolean tryGetNeedsReqCats(Bundle extras, String name, boolean defaultVaue) {
        return hasNeedReqCats(extras, name) ? (Boolean)extras.get(name) : defaultVaue;
    }

    static Calendar tryGetWeekStart(Bundle extras, String name, Calendar defaultSeedDate) {
        return hasAccount(extras, name) ?
                ((Account)extras.get(name)).getWeekStart()
                : getStartOfWeek(defaultSeedDate);
    }
}