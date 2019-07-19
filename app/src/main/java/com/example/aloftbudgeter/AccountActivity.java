package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    final private Integer[] editableViews = new Integer[] {
            R.id.account_name,
            R.id.account_start,
            R.id.account_cash
        };
    Account account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //test
//        StringBuffer testMessage =
//                new StringBuffer(Aloft.getPrintableDate(Aloft.getStartDate(getApplicationContext())));
//
//        Toast test = Toast.makeText(this, testMessage.toString(), Toast.LENGTH_LONG);
//        test.show();
        //end of test

        int index = 0;
        final List<Integer> catDisplayIndexes = new ArrayList<>();
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
                AccountActivity.this,
                (ListView) findViewById(R.id.account_categories),
                account,
                catDisplayIndexes
            );

        findViewById(R.id.account_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Aloft.getCategoryActivityIntent(
                        getApplicationContext(),
                        getAccountFromActivity(),
                        catDisplayIndexes,
                        catDisplayIndexes.size()
                    ));
                finish();

                return;
            }
        });

        findViewById(R.id.account_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                finish();

                return;
            }
        });

        findViewById(R.id.account_next).setOnClickListener(new View.OnClickListener() {
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
                    startActivity(Aloft.getMainActivityIntent(
                            getApplicationContext(),
                            getAccountFromActivity()
                        ));
                    finish();

                    return;
                }
            }
        });
    }

    private Account getAccountFromActivity() {
        account = account != null ? account
                : Aloft.tryGetAccount(
                        getIntent().getExtras(),
                        getString(R.string.extra_account),
                        new Account(Calendar.getInstance())
                    );

        for(Integer i: editableViews){ account.updateFromView(this, findViewById(i)); }

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
