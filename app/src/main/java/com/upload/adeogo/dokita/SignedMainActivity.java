package com.upload.adeogo.dokita;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignedMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
