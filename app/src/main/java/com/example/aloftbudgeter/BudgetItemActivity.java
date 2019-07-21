package com.example.aloftbudgeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class BudgetItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_item);

        findViewById(R.id.budget_back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Aloft.getMainActivityIntent(getApplicationContext(), null));
                        finish();

                        return;
                    }
                }
            );
    }

    @Override
    public void onBackPressed(){}
}
