package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Account account = null;
        List<Category> listItems = new ArrayList<>();
        //List<Integer> categoryIndexes = new ArrayList<>();

        displayCategoryList(listItems);

        findViewById(R.id.account_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                finish();
            }
        });

        findViewById(R.id.account_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account account = new Account(
                        Calendar.getInstance(),
                        ((EditText)findViewById(R.id.account_name)).getText().toString(),
                        new ArrayList<Category>()
                    );

                startActivity(Aloft.getMainActivityIntent(getApplicationContext(), account));
                finish();
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

        for(Integer i : new Integer[]{
                R.id.account_name,
                R.id.account_start,
                R.id.account_income,
                R.id.account_cash
            }
        ){
            ((EditText)findViewById(i)).setText("");
        }
    }

    @Override
    public void onBackPressed(){}
}
