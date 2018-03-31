package com.upload.adeogo.dokita.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.upload.adeogo.dokita.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AppointmentActivity.class);
        startActivity(intent);
        finish();
    }
}
