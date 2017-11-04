package com.work.adeogo.dokita;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SignedMainActivity extends AppCompatActivity {

    private LinearLayout mBodyMeasurementsLinearLayout;
    private LinearLayout mResultsLinearLayout;
    private LinearLayout mHeartLinearLayout;
    private LinearLayout mReproductiveHealthLinearLayout;
    private LinearLayout mVitalsLinearLayout;
    private LinearLayout mHealthRecordsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBodyMeasurementsLinearLayout = (LinearLayout) findViewById(R.id.body_measurements_ll);
        mVitalsLinearLayout = (LinearLayout) findViewById(R.id.vitals_ll);
        mResultsLinearLayout = (LinearLayout) findViewById(R.id.results_ll);
        mHealthRecordsLinearLayout = (LinearLayout) findViewById(R.id.health_records_ll);

        mBodyMeasurementsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignedMainActivity.this, BodyMeasurementActivity.class);
                startActivity(intent);
            }
        });


        mVitalsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignedMainActivity.this, VitalsActivity.class);
                startActivity(intent);
            }
        });

        mResultsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignedMainActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

        mHealthRecordsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignedMainActivity.this, HealthRecordsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
