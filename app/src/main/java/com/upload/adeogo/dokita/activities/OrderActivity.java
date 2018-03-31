package com.upload.adeogo.dokita.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.SearchAdapter;
import com.upload.adeogo.dokita.models.Appointment;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upload.adeogo.dokita.models.Notification;
import com.upload.adeogo.dokita.services.NotifyService;
import com.upload.adeogo.dokita.utils.IdGenerator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView mNameTextView, mSpecialityTextView;
    private TextView mSundayDateTextView, mMondayDateTextView, mTuesdayDateTextView, mWednesdayDateTextView, mThursdayDateTextView, mFridayDateTextView, mSaturdayDateTextView;

    private ImageView mTimeLabelTextView;

    private TimePickerDialog tpd;
    private DatePickerDialog mDatePickerDialog;

    private CircleImageView mProfileImageView;
    private Button mBookAppointmentButton, mMessageButton;


    private int mDay = -1, mMonth, mYear, timeSetter, startHour, startMinute, endHour, endMinute, sunday = 0, monday = 0, tuesday = 0, wednesday = 0, thursday = 0, friday = 0, saturday = 0;

    private String mOrderTxt, mDescription, mClientName, mDoctorPhoneNumber, mClientPhoneNumber, mDoctorSpecialist, mDoctorName, mDateText, mTimeString = null,
             userId, doctor_id, mUsername, mMessage, mAppointmentKey, mPictureUrl, mSelfPhotoUrl;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mSelfDatabaseReference, mSelfProfileDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;

    private LinearLayout mTimeLayout;
    private TextView mTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Appointment");

        mSundayDateTextView = findViewById(R.id.sunday);
        mMondayDateTextView = findViewById(R.id.monday);
        mTuesdayDateTextView = findViewById(R.id.tuesday);
        mWednesdayDateTextView = findViewById(R.id.wednesday);
        mThursdayDateTextView = findViewById(R.id.thursday);
        mFridayDateTextView = findViewById(R.id.friday);
        mSaturdayDateTextView = findViewById(R.id.saturday);

        mMessageButton = findViewById(R.id.message);

        mBookAppointmentButton = (Button) findViewById(R.id.book_appointment);
        mNameTextView = (TextView) findViewById(R.id.profile_name);
        mSpecialityTextView = (TextView) findViewById(R.id.profile_speciality);
        mTimeLabelTextView = findViewById(R.id.pickTimeLabel);
        mTimeTextView = findViewById(R.id.timePickedTextVIew);

        mTimeLayout = findViewById(R.id.timeLayout);

        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);

        Typeface semiBoldTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_semibold.ttf");
        Typeface italicsTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_light_italic.ttf");
        mNameTextView.setTypeface(semiBoldTypeface);
        mSpecialityTextView.setTypeface(italicsTypeface);
        mBookAppointmentButton.setTypeface(semiBoldTypeface);

        Intent intent = getIntent();
        doctor_id = intent.getStringExtra("doctor_id");
        mDoctorName = intent.getStringExtra("name");
        mDoctorSpecialist =  intent.getStringExtra("speciality");
        mPictureUrl =  intent.getStringExtra("picture_url");
        mDoctorPhoneNumber = intent.getStringExtra("phone_number");
        startHour = intent.getIntExtra("start_time_hour", 0);
        startMinute = intent.getIntExtra("start_time_minute", 0);
        endHour = intent.getIntExtra("end_time_hour", 0);

        sunday = intent.getIntExtra("sunday", 0);
        monday = intent.getIntExtra("monday", 0);
        tuesday = intent.getIntExtra("tuesday", 0);
        wednesday = intent.getIntExtra("wednesday", 0);
        thursday = intent.getIntExtra("thursday", 0);
        friday = intent.getIntExtra("friday", 0);
        saturday = intent.getIntExtra("saturday", 0);

        mPictureUrl = intent.getStringExtra("pictureUrl");
        mSelfPhotoUrl = intent.getStringExtra("selfPictureUrl");


        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // PatientData is signed in
                    userId = user.getUid();
                    mUsername = user.getDisplayName();
                    mClientName = mUsername;
                    mSelfDatabaseReference = mFirebaseDatabase.getReference().child("users/" + userId + "/appointments");
                    mSelfProfileDatabase = mFirebaseDatabase.getReference().child("users/" + userId);
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // PatientData is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(OrderActivity.this, LoginActivity.class));
                }
            }
        };

        mSelfProfileDatabase = mFirebaseDatabase.getReference().child("users/" + userId);

        mDatabaseReference = mFirebaseDatabase.getReference().child("new_doctors/" + doctor_id + "/appointments/appointments");

        //Custom DatePicker
        final Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        final int thisMonth = calendar.get(Calendar.MONTH);

        final int thisDay = calendar.get(Calendar.DAY_OF_MONTH);


        updateDateColor();

        mBookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeString == null){
                    Toast.makeText(OrderActivity.this, "You forgot to pick a Time", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (mDateText == null || mDay == -1){
//                    Toast.makeText(OrderActivity.this, "You forgot to pick a Date", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                sendAppointment();
            }
        });


        mNameTextView.setText(mDoctorName);
        mSpecialityTextView.setText(mDoctorSpecialist);

        Glide.with(this)
                .load(mPictureUrl)
                .into(mProfileImageView);

        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (TextUtils.equals("phone", dataSnapshot.getKey())){
                    mClientPhoneNumber = dataSnapshot.getValue().toString();
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

        if (eventListener!= null){
            mSelfProfileDatabase.addChildEventListener(eventListener);
        }


        mTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();

                if (tpd == null) {
                    tpd = TimePickerDialog.newInstance(
                            OrderActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                } else {
                    tpd.initialize(
                            OrderActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND),
                            true
                    );
                }

               tpd.setMinTime(startHour, startMinute, 00);
                tpd.setMaxTime(endHour, endMinute, 00);

                tpd.show(getFragmentManager(), "Timepickerdialog");

            }
        });

        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, QuestionActivity.class);
                intent.putExtra("doctor_name", mDoctorName);
                intent.putExtra("doctor_id", doctor_id);
                intent.putExtra("pictureUrl", mPictureUrl);
                intent.putExtra("selfPhotoUrl", mSelfPhotoUrl);
                startActivity(intent);
            }
        });
    }


    private void sendAppointment(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message to doctor");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);



// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                mMessage = input.getText().toString();

                if (mMessage.length() > 25){

                    IdGenerator idGenerator = new IdGenerator(new Random());
                    mAppointmentKey = idGenerator.nextId();

                    Calendar calendar  = Calendar.getInstance();

                    mYear = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DATE);
                    mMonth = calendar.get(Calendar.MONTH);

                    mDatabaseReference.child(mAppointmentKey).setValue(new Appointment(userId, doctor_id, mTimeString, mYear, mMonth, mDay, mDoctorPhoneNumber, mClientPhoneNumber, mDoctorName, mClientName, "The General Hospital", 0, mMessage, mSelfPhotoUrl));
                    mSelfDatabaseReference.child(mAppointmentKey).setValue(new Appointment(userId,doctor_id, mTimeString, mYear, mMonth, mDay, mDoctorPhoneNumber,mClientPhoneNumber, mDoctorName, mClientName, "The General Hospital", 0, mMessage, mSelfPhotoUrl));

                    Notification notification = new Notification();
                    notification.setText(mUsername + " has booked a appointment with you");
                    notification.setTopic(doctor_id);
                    notification.setUid(userId);
                    notification.setUsername(mUsername);
                    notification.setType("1");

                    FirebaseDatabase.getInstance().getReference(doctor_id).push().setValue(notification);

                    FirebaseMessaging.getInstance().subscribeToTopic(userId);

                    Toasty.success(OrderActivity.this, "Appointment Request Sent!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(OrderActivity.this, "Please enter more details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });


        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 25) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);
                } else {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);                        }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        dialog.show();
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

    private void setAlarm(){

        Intent myIntent = new Intent(this , NotifyService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24 , pendingIntent);
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

    private void updateDateColor(){

        if (sunday == 1){
            mSundayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
            mSundayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }

        if (monday == 1){
            mMondayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (monday == 0){
            mMondayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }


        if (tuesday == 1){
            mTuesdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (tuesday == 0){
            mTuesdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }

        if (wednesday == 1){
            mWednesdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (wednesday == 0){
            mWednesdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }


        if (thursday == 1){
            mThursdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (thursday == 0){
            mThursdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }

        if (friday == 1){
            mFridayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (friday == 0){
            mFridayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }

        if (saturday == 1){
            mSaturdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background_clicked));
        }else if (saturday == 0){
            mSaturdayDateTextView.setBackground(OrderActivity.this.getResources().getDrawable(R.drawable.curved_button_background));
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mTimeString = hourOfDay + ":" + minute;

        if (minute < 10){
           mTimeString = hourOfDay + ":0" + minute;
        }

        mTimeTextView.setText(mTimeString);
    }
}