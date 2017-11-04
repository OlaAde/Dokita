package com.work.adeogo.dokita;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.work.adeogo.dokita.models.Appointment;
import com.firebase.ui.auth.AuthUI;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;

public class OrderActivity extends AppCompatActivity {

    private TextView mBookAppointmentTextView;
    private TextView mNameTextView;
    private TextView mSpecialityTextView;

    private TextView mTenTextView;
    private TextView mTwelveTextView;
    private TextView mFourTextView;
    private ImageView mProfileImageView;

    private DatePickerTimeline timeline;
    private int timeSetter;
    private String mDoctorName;
    private String mDoctorSpecialist;
    private String mPictureUrl;
    private String mDoctorNumber;

    private String mDateText;
    private int mTimeInt = 0;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String Description;

    private String mOrderTxt;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;
    private String doctor_id;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Appointment");
        mBookAppointmentTextView = (TextView) findViewById(R.id.book_appointment);
        mNameTextView = (TextView) findViewById(R.id.profile_name);
        mSpecialityTextView = (TextView) findViewById(R.id.profile_speciality);
        mTenTextView = (TextView) findViewById(R.id.ten);
        mTwelveTextView = (TextView) findViewById(R.id.twelve);
        mFourTextView = (TextView) findViewById(R.id.four);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image);

        Intent intent = getIntent();
        doctor_id = intent.getStringExtra("doctor_id");
        mDoctorName = intent.getStringExtra("name");
        mDoctorSpecialist =  intent.getStringExtra("speciality");
        mPictureUrl =  intent.getStringExtra("picture_url");
        mPhoneNumber = intent.getStringExtra("phone_number");


        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();
                    mUsername = user.getDisplayName();
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.GreenTheme)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mDatabaseReference = mFirebaseDatabase.getReference().child("doctors/" +doctor_id+ "/appointments");


        //Custom DatePicker
        timeline = (DatePickerTimeline) findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        int thisMonth = calendar.get(Calendar.MONTH);

        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        timeline.setFirstVisibleDate(thisYear, thisMonth, thisDay);
        timeline.setLastVisibleDate(2090, Calendar.JULY, 19);

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                Toast.makeText(OrderActivity.this, year + " " + month +" " + day , Toast.LENGTH_SHORT).show();
                mDateText = day + "/" + month + "/" + year;
            }
        });

        mBookAppointmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeInt == 0){
                    Toast.makeText(OrderActivity.this, "You forgot to pick a Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mDateText == null){
                    Toast.makeText(OrderActivity.this, "You forgot to pick a Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendAppointment();
            }
        });



        mNameTextView.setText(mDoctorName);
        mSpecialityTextView.setText(mDoctorSpecialist);
        Picasso.with(this)
                .load(mPictureUrl)
                .into(mProfileImageView);

        mTenTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSetter = 0;
                checkTimeSetter();
            }
        });
        mFourTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSetter = 2;
                checkTimeSetter();
            }
        });
        mTwelveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSetter = 1;
                checkTimeSetter();
            }
        });
    }

    private void checkTimeSetter(){
        if(timeSetter == 0){
            mFourTextView.setTextColor(getResources().getColor(R.color.white));
            mTenTextView.setTextColor(getResources().getColor(R.color.black));
            mTwelveTextView.setTextColor(getResources().getColor(R.color.white));
            mTimeInt = 10;
        }else if (timeSetter == 1){
            mTenTextView.setTextColor(getResources().getColor(R.color.white));
            mFourTextView.setTextColor(getResources().getColor(R.color.white));
            mTwelveTextView.setTextColor(getResources().getColor(R.color.black));
            mTimeInt = 12;
        }else if (timeSetter == 2){
            mTwelveTextView.setTextColor(getResources().getColor(R.color.white));
            mFourTextView.setTextColor(getResources().getColor(R.color.black));
            mTenTextView.setTextColor(getResources().getColor(R.color.white));
            mTimeInt = 16;
        }
    }

    private void sendAppointment(){
        mDatabaseReference.push().setValue(new Appointment(userId, mTimeInt, 0, 0, mYear, mMonth, mDay, mDoctorNumber ));
        mPhoneNumber.trim();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",  mPhoneNumber, null));
        mOrderTxt = mUsername + " would like to make an appointment with you on " + mDateText + ".";
        intent.putExtra("sms_body", mOrderTxt);
        startActivity(intent);

    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}