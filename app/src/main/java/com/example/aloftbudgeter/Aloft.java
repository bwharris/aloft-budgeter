package com.example.aloftbudgeter;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class Aloft {

    enum Frequency{
        once,
        weekly,
        monthly
    }

    final static long weekToMilliSec = 604800000;
    final static int contingencyPercent = 10;

    static void displayCategoryList(
            final Activity activity,
            ListView listView,
            final Account account,
            final List<Integer> catDisplayIndexes
    ) {
        List<Category> displayCategories = new ArrayList<>();
        for(Integer i: catDisplayIndexes){ displayCategories.add(account.getCategories().get(i)); }

        listView.setAdapter(new CategoryListAdapter(activity, displayCategories));

        if(activity.getClass() == MainActivity.class){
            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {
                            activity.startActivity(getBudgetItemActivityIntent(
                                    activity.getApplicationContext(),
                                    account,
                                    catDisplayIndexes,
                                    i
                                ));
                            activity.finish();
                            return;
                        }
                    }
                );
        }
    }

    static Intent getAccountActivityIntent(Context context, boolean needsReqCats) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra(context.getString(R.string.extra_needsReqCats), needsReqCats);

        return intent;
    }

    static Intent getBudgetItemActivityIntent(
            Context context,
            Account account,
            List<Integer> catDisplayIndexes,
            int position
    ) {
        Intent intent = new Intent(context, BudgetItemActivity.class);
        intent.putExtra(context.getString(R.string.extra_account), account);
        intent.putIntegerArrayListExtra(context.getString(
                R.string.extra_catDisplayIndexes),
                (ArrayList<Integer>) catDisplayIndexes
        );
        intent.putExtra(context.getString(R.string.extra_position), position);

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

    static HashMap<String, Integer> getCategoryIndexHashMap(List<Category> categories) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        int index = 0;

        for(Category category: categories){ hashMap.put(category.getName(), index++); }

        return hashMap;
    }

    static StringBuffer getCreateAccountTableQuery(Context context) {
        StringBuffer query = new StringBuffer("CREATE TABLE ");
        query.append(context.getString(R.string.table_account));
        query.append('(');
        query.append(context.getString(R.string.table_account_id));
        query.append(" INTEGER PRIMARY KEY, ");
        query.append(context.getString(R.string.table_account_name));
        query.append(" TEXT)");
        return query;
    }

    static StringBuffer getCreateBudgetItemTableQuery(Context context) {
        StringBuffer query = new StringBuffer("CREATE TABLE ");
        query.append(context.getString(R.string.table_budget_item));
        query.append('(');
        query.append(context.getString(R.string.table_budget_item_id));
        query.append(" INTEGER PRIMARY KEY, ");
        query.append(context.getString(R.string.table_category_id));
        query.append(" INTEGER, ");
        query.append(context.getString(R.string.table_budget_item_date));
        query.append(" INTEGER,");
        query.append(context.getString(R.string.table_budget_item_value));
        query.append(" INTEGER, ");
        query.append(context.getString(R.string.table_budget_item_is_actual));
        query.append(" INTEGER, CONSTRAINT fk_category FOREIGN KEY (");
        query.append(context.getString(R.string.table_category_id));
        query.append(") REFERENCES ");
        query.append(context.getString(R.string.table_category));
        query.append('(');
        query.append(context.getString(R.string.table_category_id));
        query.append("))");

        return query;
    }

    static StringBuffer getCreateCategoryTableQuery(Context context) {
        StringBuffer query = new StringBuffer("CREATE TABLE ");
        query.append(context.getString(R.string.table_category));
        query.append('(');
        query.append(context.getString(R.string.table_category_id));
        query.append(" INTEGER PRIMARY KEY, ");
        query.append(context.getString(R.string.table_account_id));
        query.append(" INTEGER, ");
        query.append(context.getString(R.string.table_category_name));
        query.append(" TEXT, ");
        query.append("CONSTRAINT fk_account FOREIGN KEY (");
        query.append(context.getString(R.string.table_account_id));
        query.append(") REFERENCES ");
        query.append(context.getString(R.string.table_account));
        query.append('(');
        query.append(context.getString(R.string.table_account_id));
        query.append("))");

        return query;
    }

    static StringBuffer getDropAccountTableQuery(Context context) {
        StringBuffer query = new StringBuffer("DROP TABLE IF EXISTS ");
        query.append(context.getString(R.string.table_account));

        return  query;
    }

    static StringBuffer getDropBudgetItemTableQuery(Context context) {
        StringBuffer query = new StringBuffer("DROP TABLE IF EXISTS ");
        query.append(context.getString(R.string.table_budget_item));

        return query;
    }

    static StringBuffer getDropCategoryTableQuery(Context context) {
        StringBuffer query = new StringBuffer("DROP TABLE IF EXISTS ");
        query.append(context.getString(R.string.table_category));

        return query;
    }

    static List<Integer> getIntegerSequence(int start, int numberOfIntegers, int step) {
        List<Integer> sequence = new ArrayList<>();
        for(int i = start - 1; i < numberOfIntegers * step;){ sequence.add(i += step); }

        return sequence;
    }

    static Intent getMainActivityIntent(Context context, Account account) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(context.getString(R.string.extra_account), account);

        return intent;
    }

    static Intent getMainActivityIntent(Context context, int accountID){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(context.getString(R.string.extra_accountID), accountID);

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

    public static Intent getReconcilerActivity(Context context, Account account) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getString(R.string.app_reconciler));
        if(intent != null) {
            intent.putExtra(context.getString(R.string.extra_accountID), account);
            return intent; 
        }

        return getMainActivityIntent(context, account);
    }

    static Calendar getStartDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.pref),0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pref.getLong(context.getString(R.string.pref_start_date), 0));

        return calendar;
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

    private static boolean hasCatDisplayIndexes(Bundle extras, String name) {
        return extras != null &&
                extras.get(name) != null &&
                extras.get(name).getClass() == ArrayList.class;
    }

    private static boolean hasNeedReqCats(Bundle extras, String name) {
        return extras != null &&
                extras.get(name) != null &&
                extras.get(name).getClass() == Boolean.class;
    }

    private static boolean hasPosition(Bundle extras, String name) {
        return extras != null &&
                extras.get(name) != null &&
                extras.get(name).getClass() == Integer.class;
    }

    static void saveStartDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.pref),0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(context.getString(R.string.pref_start_date), getStartOfWeek(Calendar.getInstance()).getTimeInMillis());
        editor.commit();
    }

    static Account tryGetAccount(Bundle extras, String name, Account defaultValue) {
        return hasAccount(extras, name) ? (Account)extras.get(name) : defaultValue;
    }

    static List<Integer> tryGetCatDisplayIndexes(Bundle extras, String name, List<Integer> defaultValue) {
        return hasCatDisplayIndexes(extras, name) ? (List<Integer>) extras.get(name) : defaultValue;
    }

    static boolean tryGetNeedsReqCats(Bundle extras, String name, boolean defaultVaue) {
        return hasNeedReqCats(extras, name) ? (Boolean)extras.get(name) : defaultVaue;
    }

    static int tryGetPosition(Bundle extras, String name, int defaultPosition) {
        return hasPosition(extras, name) ? (int)extras.get(name) : defaultPosition;
    }

    static int tryGetValue(BudgetItem budgetItem, int defaultValue) {
        return budgetItem == null ? defaultValue : budgetItem.getValue();
    }

    static Calendar tryGetWeekStart(Bundle extras, String name, Calendar defaultSeedDate) {
        return hasAccount(extras, name) ?
                ((Account)extras.get(name)).getWeekStart()
                : getStartOfWeek(defaultSeedDate);
    }

    static int tryParseInteger(EditText editText, int defaultValue) {
        try{ return Integer.parseInt(editText.getText().toString()); }
        catch (NumberFormatException e){ return defaultValue; }
    }

    static int tryParseInteger(TextView textView, int defaultValue){
        try{ return Integer.parseInt(textView.getText().toString()); }
        catch (NumberFormatException e) { return defaultValue; }
    }
}