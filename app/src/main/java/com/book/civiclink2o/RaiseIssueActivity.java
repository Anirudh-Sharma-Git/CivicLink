package com.book.civiclink2o;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RaiseIssueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This connects our Java file to the layout we created earlier
        setContentView(R.layout.activity_raise_issue);
    }
}