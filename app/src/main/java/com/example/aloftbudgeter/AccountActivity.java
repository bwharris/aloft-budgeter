package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    Integer[] reqInputIDs = new Integer[] {
            R.id.account_name,
            R.id.account_start,
            R.id.account_cash
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        List<Category> listItems = new ArrayList<>();
        for(String name: getApplicationContext().getString(R.string.reqListItems).split(";")) {
            listItems.add(new Category(name));
        }

        displayCategoryList(listItems);

        findViewById(R.id.account_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        viewsNeedingInput.add(i);
                    }
                }

                if(viewsNeedingInput.size() == 0) {
                    Account account = new Account(
                            Calendar.getInstance(),
                            ((EditText) findViewById(R.id.account_name)).getText().toString(),
                            new ArrayList<Category>()
                        );

                    startActivity(Aloft.getMainActivityIntent(getApplicationContext(), account));
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
    }

    @Override
    protected void onStart(){
        super.onStart();

        for(Integer i : reqInputIDs) { ((EditText)findViewById(i)).setText(""); }
    }

    @Override
    public void onBackPressed(){}
}
