package com.example.aloftbudgeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BUDGETER";
    private static final int DATABASE_VERSION = 9;
    private Context context;

    DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Aloft.getCreateAccountTableQuery(context).toString());
        db.execSQL(Aloft.getCreateCategoryTableQuery(context).toString());
        db.execSQL(Aloft.getCreateBudgetItemTableQuery(context).toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(Aloft.getDropAccountTableQuery(context).toString());
        db.execSQL(Aloft.getDropCategoryTableQuery(context).toString());
        db.execSQL(Aloft.getDropBudgetItemTableQuery(context).toString());
        onCreate(db);
    }

    int create(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(context.getString(R.string.table_account_name), account.getName());

        int accountID = (int)(long)db.insert(
                context.getString(R.string.table_account),
                null,
                values
            );

        for(Category category: account.getCategories()){ create(category, accountID); }
        db.close();

        return accountID;
    }

    int create(BudgetItem budgetItem, int categoryID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(context.getString(R.string.table_category_id), categoryID);
        values.put(
                context.getString(R.string.table_budget_item_date),
                budgetItem.getWeekStart().getTimeInMillis()
        );
        values.put(context.getString(R.string.table_budget_item_value), budgetItem.getValue());
        values.put(context.getString(R.string.table_budget_item_is_actual), budgetItem.getIsActual());

        int budgetItemID = (int)(long)db.insert(
                context.getString(R.string.table_budget_item),
                null,
                values
        );
        db.close();

        return budgetItemID;
    }

    int create(Category category, int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(context.getString(R.string.table_account_id), accountID);
        values.put(context.getString(R.string.table_category_name), category.getName());

        int categoryID = (int)(long)db.insert(
                context.getString(R.string.table_category),
                null,
                values
            );

        for(BudgetItem budgetItem: category.getBudgetItems()) { create(budgetItem, categoryID); }
        db.close();

        return categoryID;
    }

    Account getAccountByID(int accountID, Calendar seedDate) {
        Account account = null;
        Calendar weekStart = Aloft.getStartOfWeek(seedDate);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                context.getString(R.string.table_account),
                new String[] {
                        context.getString(R.string.table_account_id),
                        context.getString(R.string.table_account_name)
                },
                context.getString(R.string.table_account_id) + " = ?",
                new String[] { String.valueOf(accountID) },
                null, null, null
        );

        if(cursor == null){ return account; }

        cursor.moveToFirst();

        account = new Account(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                weekStart,
                getCategoryByAccountID(Integer.parseInt(cursor.getString(0)), weekStart)
        );

        cursor.close();
        db.close();

        return account;
    }

    List<Account> getAccounts(Calendar weekStart) {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                context.getString(R.string.table_account),
                new String[] {
                        context.getString(R.string.table_account_id),
                        context.getString(R.string.table_account_name)
                    },
                null, null, null, null, null
            );

        if(cursor.moveToFirst()){
            do{ accounts.add(new Account(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        weekStart,
                        getCategoryByAccountID(Integer.parseInt(cursor.getString(0)), weekStart)
                    ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return accounts;
    }

    List<BudgetItem> getBudgetItemsByCategoryID(int categoryID, Calendar seedDate) {
        List<BudgetItem> budgetItems= new ArrayList<>();
        StringBuffer selectionBuffer = new StringBuffer();
        selectionBuffer.append(context.getString(R.string.table_category_id));
        selectionBuffer.append(" = ? AND ");
        selectionBuffer.append(context.getString(R.string.table_budget_item_date));
        selectionBuffer.append(" >= ? AND ");
        selectionBuffer.append(context.getString(R.string.table_budget_item_date));
        selectionBuffer.append(" < ?");

        Calendar startOfWeek = Aloft.getStartOfWeek(seedDate);
        Calendar endOfWeek = (Calendar)startOfWeek.clone();
        endOfWeek.add(Calendar.DATE, 7);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                context.getString(R.string.table_budget_item),
                new String[]{
                        context.getString(R.string.table_budget_item_id),
                        context.getString(R.string.table_category_id),
                        context.getString(R.string.table_budget_item_date),
                        context.getString(R.string.table_budget_item_value),
                        context.getString(R.string.table_budget_item_is_actual)
                },
                selectionBuffer.toString(),
                new String[]{
                        String.valueOf(categoryID),
                        String.valueOf(startOfWeek.getTimeInMillis()),
                        String.valueOf(endOfWeek.getTimeInMillis())
                },
                null, null, null, null
        );

        if(cursor.moveToFirst()){
            do{ budgetItems.add(new BudgetItem(
                    Integer.parseInt(cursor.getString(0)),
                    Integer.parseInt(cursor.getString(1)),
                    startOfWeek,
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)) == 1
            ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return budgetItems;
    }

    List<Category> getCategoryByAccountID(int accountID, Calendar seedDate) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                context.getString(R.string.table_category),
                new String[]{
                        context.getString(R.string.table_category_id),
                        context.getString(R.string.table_account_id),
                        context.getString(R.string.table_category_name)
                    },
                context.getString(R.string.table_account_id) + " = ?",
                new String[]{ String.valueOf(accountID) },
                null, null, null, null
            );

        if(cursor.moveToFirst()){
            do{ categories.add(new Category(
                        Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2),
                        getBudgetItemsByCategoryID(Integer.parseInt(cursor.getString(0)), seedDate)
                    ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }

    void removeBudgetItem(int budgetItemID) {
        StringBuffer whereBuffer =
                new StringBuffer(context.getString(R.string.table_budget_item_id));
        whereBuffer.append(" = ?");

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                context.getString(R.string.table_budget_item),
                whereBuffer.toString(),
                new String[] { String.valueOf(budgetItemID) }
            );
    }

    void update(int budgetItemID, int value) {
        StringBuffer whereBuffer = new StringBuffer(context.getString(R.string.table_budget_item_id));
        whereBuffer.append(" = ?");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(context.getString(R.string.table_budget_item_value), value);

        db.update(
                context.getString(R.string.table_budget_item),
                values,
                whereBuffer.toString(),
                new String[]{ String.valueOf(budgetItemID) }
            );
        db.close();
    }
}
