package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    final private Integer[] editableViews = new Integer[] {
            R.id.account_name,
            R.id.account_start,
            R.id.account_cash
        };
    private Account account = null;
    private List<Integer> catDisplayIndexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        int index = 0;
        account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                new Account(Calendar.getInstance())
            );

        if(account.getCategories().size() == 0){
            for(String name: getResources().getStringArray(R.array.coreCategories)){
                account.addCategory(new Category(name));
                index++;
            }
        }

        if(
            Aloft.tryGetNeedsReqCats(
                    getIntent().getExtras(),
                    getResources().getString(R.string.extra_needsReqCats),
                    true
            )
        ){
            for(String name: getResources().getStringArray(R.array.reqCategories)) {
                account.addCategory(new Category(name));
                catDisplayIndexes.add(index++);
            }
        }

        Aloft.displayCategoryList(
                this,(ListView)findViewById(R.id.account_categories),account,catDisplayIndexes
            );

        findViewById(R.id.account_add).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int viewID = R.id.account_new_cat;

                    if(TextUtils.isEmpty(((EditText)findViewById(viewID)).getText().toString())){
                        ((EditText)findViewById(viewID)).setError("A name is needed");
                    }
                    else{
                        account.addCategory(
                                new Category(((EditText)findViewById(viewID)).getText().toString())
                            );
                        catDisplayIndexes.add(account.getCategories().size() - 1);
                        Aloft.displayCategoryList(
                                AccountActivity.this,
                                (ListView) findViewById(R.id.account_categories),
                                account,
                                catDisplayIndexes
                            );
                        ((EditText)findViewById(viewID)).setText("");
                    }
                }
            }
        );

        findViewById(R.id.account_back).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                    finish();

                    return;
                }
            }
        );

        findViewById(R.id.account_next).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Integer> editableViewsMissingValues = new ArrayList<>();

                    for(Integer i: editableViews){
                        if(TextUtils.isEmpty(((EditText)findViewById(i)).getText().toString())){
                            ((EditText)findViewById(i)).setError("A value is needed");
                            editableViewsMissingValues.add(i);
                        }
                    }

                    if(editableViewsMissingValues.size() == 0) {
                        DatabaseHandler databaseHandler =
                                new DatabaseHandler(getApplicationContext());
                        databaseHandler.create(getAccountFromActivity());

                        startActivity(Aloft.getMainActivityIntent(getApplicationContext(),null));
                        finish();

                        return;
                    }
                }
            }
        );
    }

    private Account getAccountFromActivity() {
        account = account != null ? account
                : Aloft.tryGetAccount(
                        getIntent().getExtras(),
                        getString(R.string.extra_account),
                        new Account(Calendar.getInstance())
                    );

        for(Integer i: editableViews){
            if(i.equals(R.id.account_start)) { continue; }
            account.updateFromView(this, findViewById(i));
        }

        account.updateFromView(this, findViewById(R.id.account_start));

        return  account;
    }

    @Override
    protected void onStart(){
        super.onStart();

        for(Integer i : editableViews) { ((EditText)findViewById(i)).setText(""); }
    }

    @Override
    public void onBackPressed(){}
}
