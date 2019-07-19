package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    final private Integer[] reqInputIDs = new Integer[] {
            R.id.account_name,
            R.id.account_start,
            R.id.account_cash
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        List<Category> listItems = new ArrayList<>();

        for(String name: getResources().getStringArray(R.array.reqListItems)) {
            listItems.add(new Category(name));
        }

        Aloft.displayCategoryList(
                AccountActivity.this,
                (ListView) findViewById(R.id.account_categories),
                listItems
            );

        findViewById(R.id.account_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account account = getAccountFromActivity();

                startActivity(Aloft.getCategoryActivityIntent(getApplicationContext()));
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
                List<Integer> viewsNeedingInput = new ArrayList<>();

                for(Integer i: reqInputIDs){
                    if(TextUtils.isEmpty(((EditText)findViewById(i)).getText().toString())){
                        ((EditText)findViewById(i)).setError("A value is needed");
                    }
                }

                if(viewsNeedingInput.size() == 0) {
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

    private void displayCategoryList(List<Category> listItems) {
        ListView listView = findViewById(R.id.account_categories);
        listView.setAdapter(
                new CategoryListAdapter(AccountActivity.this, listItems)
            );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(Aloft.getCategoryActivityIntent(getApplicationContext()));
                finish();
                return;
            }
        });
    }

    private Account getAccountFromActivity() {
        Account account = Aloft.tryGetAccount(
                getIntent().getExtras(),
                getApplicationContext().getString(R.string.extra_account),
                new Account(Calendar.getInstance())
            );

       for(Integer i: reqInputIDs){ account.updateFromView(findViewById(i)); }

        return  account;
    }

    @Override
    protected void onStart(){
        super.onStart();

        for(Integer i : reqInputIDs) { ((EditText)findViewById(i)).setText(""); }
    }

    @Override
    public void onBackPressed(){}
}
