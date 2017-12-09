package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.upload.adeogo.dokita.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HealthRecordsActivity extends AppCompatActivity {

    private LinearLayout mHealthRecordsLinearLayout;
    private LinearLayout mDataSourcesLinearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/open_sans_semibold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_health_records);

        mDataSourcesLinearLayout = (LinearLayout) findViewById(R.id.data_sources_ll);
        mHealthRecordsLinearLayout = (LinearLayout) findViewById(R.id.show_records_ll);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Health Records");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDataSourcesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HealthRecordsActivity.this, "No Data Sources to show!", Toast.LENGTH_SHORT).show();
            }
        });

        mHealthRecordsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HealthRecordsActivity.this, "No Health Records to show!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
