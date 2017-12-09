package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.TimeAdapter;
import com.upload.adeogo.dokita.models.Appointment;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.upload.adeogo.dokita.models.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderActivity extends AppCompatActivity implements TimeAdapter.TimeAdapterOnclickHandler {

    private TextView mBookAppointmentTextView;
    private TextView mNameTextView;
    private TextView mSpecialityTextView;

    private TimeAdapter mTimeAdapter;
    private RecyclerView mTimeRecyclerView;
    private LinearLayoutManager mManager;
    private List<Time> mTimeList = new ArrayList<>();

    private CircleImageView mProfileImageView;

    private DatePickerTimeline timeline;
    private int timeSetter;
    private String mDoctorName;
    private String mDoctorSpecialist;
    private String mPictureUrl;
    private String mDoctorNumber;
    private String mClientName;

    private String mDateText;
    private String mTimeInt = null;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String Description;

    private String mOrderTxt;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mSelfDatabaseReference;
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

        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Appointment");
        mBookAppointmentTextView = (TextView) findViewById(R.id.book_appointment);
        mNameTextView = (TextView) findViewById(R.id.profile_name);
        mSpecialityTextView = (TextView) findViewById(R.id.profile_speciality);

        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);
        mTimeRecyclerView = findViewById(R.id.timePickingRecyclerView);

        Typeface semiBoldTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_semibold.ttf");
        Typeface italicsTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_light_italic.ttf");
        mNameTextView.setTypeface(semiBoldTypeface);
        mSpecialityTextView.setTypeface(italicsTypeface);
        mBookAppointmentTextView.setTypeface(semiBoldTypeface);

        Intent intent = getIntent();
        doctor_id = intent.getStringExtra("doctor_id");
        mDoctorName = intent.getStringExtra("name");
        mDoctorSpecialist =  intent.getStringExtra("speciality");
        mPictureUrl =  intent.getStringExtra("picture_url");
        mPhoneNumber = intent.getStringExtra("phone_number");


        mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        mTimeAdapter = new TimeAdapter(this, this);

        mTimeRecyclerView.setAdapter(mTimeAdapter);
        mTimeRecyclerView.setLayoutManager(mManager);
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
                    mClientName = mUsername;
                    mSelfDatabaseReference = mFirebaseDatabase.getReference().child("users/" + userId + "/appointments");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(OrderActivity.this, LoginActivity.class));
                }
            }
        };

        mDatabaseReference = mFirebaseDatabase.getReference().child("new_doctors/" + doctor_id + "/appointments/appointments");

        //Custom DatePicker
        timeline = (DatePickerTimeline) findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        int thisMonth = calendar.get(Calendar.MONTH);

        final int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        timeline.setFirstVisibleDate(thisYear, thisMonth, thisDay - 1);

        timeline.setLastVisibleDate(2090, Calendar.JULY, 19);

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                mDateText = day + "/" + month + "/" + year;
                if (day < thisDay){
                    Toast.makeText(OrderActivity.this, "Pick a day not in the past", Toast.LENGTH_SHORT).show();
                }else {
                    mDay = day;
                    mMonth = month;
                    mYear = year;
                }

            }
        });

        mBookAppointmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeInt == null){
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

        setTimes();

        mNameTextView.setText(mDoctorName);
        mSpecialityTextView.setText(mDoctorSpecialist);
        Picasso.with(this)
                .load(mPictureUrl)
                .into(mProfileImageView);

    }


    private void sendAppointment(){
        mDatabaseReference.push().setValue(new Appointment(userId, doctor_id, mTimeInt, 0, 0, mYear, mMonth, mDay, mDoctorNumber, mDoctorName, mClientName, "The General Hospital"));
        mSelfDatabaseReference.push().setValue(new Appointment(userId,doctor_id, mTimeInt, 0, 0, mYear, mMonth, mDay, mDoctorNumber, mDoctorName, mClientName, "The General Hospital"));
        mPhoneNumber.trim();
        Toast.makeText(this, "Appointment Request Sent!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(OrderActivity.this, ProfileActivity.class);
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

    private void setTimes(){
        mTimeList.add(new Time("10:00", 1));
        mTimeList.add(new Time("10:30", 1));
        mTimeList.add(new Time("11:00", 1));
        mTimeList.add(new Time("11:30", 1));
        mTimeList.add(new Time("12:30", 1));
        mTimeList.add(new Time("13:00", 1));
        mTimeList.add(new Time("13:30", 1));
        mTimeList.add(new Time("14:00", 1));
        mTimeList.add(new Time("14:30", 1));
        mTimeList.add(new Time("15:00", 1));
        mTimeList.add(new Time("15:30", 1));
        mTimeList.add(new Time("16:30", 1));

        mTimeAdapter.swapData(mTimeList);
    }

    private void setPickedTime(int position){
        mTimeList.get(position).setPicked(0);
        mTimeInt = mTimeList.get(position).getTime();
        mTimeAdapter.swapData(mTimeList);
        Toast.makeText(this, mTimeInt, Toast.LENGTH_SHORT).show();
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

    @Override
    public void voidMethod(List<Time> list, int adapterPosition) {
        setPickedTime(adapterPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            Intent intent = new Intent(OrderActivity.this, QuestionActivity.class);
            intent.putExtra("doctor_name", mDoctorName);
            intent.putExtra("doctor_id", doctor_id);

            intent.putExtra("doctor_id", doctor_id);
            intent.putExtra("doctor_id", doctor_id);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}