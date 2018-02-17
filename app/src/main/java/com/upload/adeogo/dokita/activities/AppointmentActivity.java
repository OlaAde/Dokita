package com.upload.adeogo.dokita.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.upload.adeogo.dokita.R;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentActivity extends AppCompatActivity {

    private String mDoctorPhone, mClientPhone, mDoctorName = "", mClientName, mLocation, mTime, mDoctorId, mUserId, mMessage, mKey, mProfileImageUrl, mPhoneNumber;
    private int mStatus, mYear, mMonth, mDay;
    private ActionBar mActionBar;

    private TextView mMessageTextview, mDoctorNameTextView, mDateTextView;
    private ImageView mCallImageView, mChatImageView;
    private CircleImageView mDoctorImageView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDoctorProfileReference;
    private ChildEventListener mProfileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        int thisMonth = calendar.get(Calendar.MONTH);

        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);


        mCallImageView = findViewById(R.id.dialImageView);
        mDoctorImageView = findViewById(R.id.doctorImageView);
        mChatImageView = findViewById(R.id.chatImageView);

        mDoctorNameTextView = findViewById(R.id.doctorNameTextVIew);
        mMessageTextview = findViewById(R.id.messageTextView);
        mDateTextView = findViewById(R.id.dateTextView);

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

        mChatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentActivity.this, QuestionActivity.class);
                intent.putExtra("doctor_name", mDoctorName);
                intent.putExtra("doctor_id", mDoctorId);
                intent.putExtra("which", 0);
                startActivity(intent);
            }
        });

        mDoctorProfileReference = mFirebaseDatabase.getReference().child("new_doctors").child("all_profiles").child(mDoctorId);

        switch (mStatus){
            case 0:
                mActionBar.setTitle(getString(R.string.action_pending));
                break;
            case 1:
                mActionBar.setTitle(getString(R.string.action_approved));
                break;
            case 2:
                mActionBar.setTitle(getString(R.string.action_cancelled));
                mChatImageView.setVisibility(View.GONE);
                break;
            case 3:
                mActionBar.setTitle(getString(R.string.action_done));
                break;
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

    private void attachDatabaseReadListener() {
        if (mProfileListener == null) {
            mProfileListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (TextUtils.equals(dataSnapshot.getKey(), "pictureUrl")){
                        mProfileImageUrl = dataSnapshot.getValue().toString();

                        Glide.with(AppointmentActivity.this)
                                .load(mProfileImageUrl)
                                .into(mDoctorImageView);
//
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

}
