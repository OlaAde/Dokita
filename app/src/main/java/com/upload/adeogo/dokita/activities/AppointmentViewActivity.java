package com.upload.adeogo.dokita.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upload.adeogo.dokita.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentViewActivity extends AppCompatActivity {

    private String mDoctorPhone, mClientPhone, mDoctorName = "", mClientName, mLocation, mTime, mDoctorId, mUserId, mMessage, mKey, mProfileImageUrl, mPhoneNumber;
    private int mStatus, mYear, mMonth, mDay;
    private ActionBar mActionBar;

    @BindView(R.id.messageTextView) TextView mMessageTextview;
    @BindView(R.id.doctorNameTextVIew) TextView mDoctorNameTextView;
    @BindView(R.id.dateTextView) TextView mDateTextView;

    @BindView(R.id.doctorImageView) ImageView mDoctorImageView;
    @BindView(R.id.dialImageView) FloatingActionButton mCallImageView;

    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingBar;
    @BindView(R.id.app_bar) AppBarLayout mAppBar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDoctorProfileReference;
    private ChildEventListener mProfileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_view);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();

        mActionBar.setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        int thisMonth = calendar.get(Calendar.MONTH);

        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();

        mKey = intent.getStringExtra("key");
        mUserId = intent.getStringExtra("userId");
        mDoctorId = intent.getStringExtra("doctorId");
        mTime = intent.getStringExtra("time");
        mDay = intent.getIntExtra("day", 0);
        mMonth = intent.getIntExtra("month", 0);
        mYear = intent.getIntExtra("year", 0);
        mDoctorPhone = intent.getStringExtra("doctor_phone");
        mClientPhone = intent.getStringExtra("client_phone");
        mDoctorName = intent.getStringExtra("doctorName");
        mClientName = intent.getStringExtra("clientName");
        mLocation = intent.getStringExtra("location");
        mStatus = intent.getIntExtra("status", 0);
        mMessage = intent.getStringExtra("message");



        mDoctorProfileReference = mFirebaseDatabase.getReference().child("new_doctors").child("all_profiles").child(mDoctorId);

        if (mActionBar!= null){
            switch (mStatus){
                case 0:
                    mActionBar.setTitle(getString(R.string.action_pending));
//                    mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPending)));
                    mAppBar.setBackgroundColor(getResources().getColor(R.color.colorPending));
                    mCollapsingBar.setContentScrim(ContextCompat.getDrawable(this, R.color.colorPending));
                    break;
                case 1:
                    mActionBar.setTitle(getString(R.string.action_approved));
//                    mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorApproved)));
                    mAppBar.setBackgroundColor(getResources().getColor(R.color.colorApproved));
                    mCollapsingBar.setContentScrim(ContextCompat.getDrawable(this, R.color.colorApproved));
                    break;
                case 2:
                    mActionBar.setTitle(getString(R.string.action_cancelled));
//                    mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCancelled)));
                    mAppBar.setBackgroundColor(getResources().getColor(R.color.colorCancelled));
                    mCollapsingBar.setContentScrim(ContextCompat.getDrawable(this, R.color.colorCancelled));
                    break;
                case 3:
                    mActionBar.setTitle(getString(R.string.action_done));
//                    mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorDone)));
                    mAppBar.setBackgroundColor(getResources().getColor(R.color.colorDone));
                    mCollapsingBar.setContentScrim(ContextCompat.getDrawable(this, R.color.colorDone));
                    break;
            }
        }

        mMessageTextview.setText(mMessage);
        mDoctorNameTextView.setText(mDoctorName);

        String Date = "";
        if (mDay == thisDay && mMonth == thisMonth && mYear == thisYear){
            Date = "Today, " + mTime;
        }else if (mDay == thisDay + 1 && mMonth == thisMonth + 1 && mYear == thisYear + 1){
            Date = "Tomorrow, " + mTime;
        }else
            Date = mTime + " on " + mDay + "/" + mMonth + 1 + "/" + mYear;

        mDateTextView.setText(Date);

        if (mStatus == 2){
            mCallImageView.setVisibility(View.INVISIBLE);
        }else {
            mCallImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mClientPhone));
                    startActivity(intent);
                }
            });
        }

        attachDatabaseReadListener();

    }

//
    private void attachDatabaseReadListener() {
        if (mProfileListener == null) {
            mProfileListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (TextUtils.equals(dataSnapshot.getKey(), "pictureUrl")){
                        mProfileImageUrl = dataSnapshot.getValue().toString();

                        Glide.with(AppointmentViewActivity.this)
                                .load(mProfileImageUrl)
                                .into(mDoctorImageView);

//                        Picasso.with(AppointmentActivity.this)
//                                .load(mProfileImageUrl)
//                                .resize(500, 500)
//                                .centerCrop()
//                                .into();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mDoctorProfileReference.addChildEventListener(mProfileListener);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_appointment_view, menu);
//        return true;
//    }
}
