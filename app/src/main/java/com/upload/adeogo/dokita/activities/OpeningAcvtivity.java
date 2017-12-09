package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upload.adeogo.dokita.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpeningAcvtivity extends AppCompatActivity {

    private static final String TAG = "OpeningAcvtivity";
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDoctorsReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String mUsername;

    private RelativeLayout mNameRelativeLayout;
    private RelativeLayout mSpecialityRelativeLayout;
    private RelativeLayout mCityRelativeLayout;

    private EditText mNameEditText;
    private TextView mSpecialityTextView;
    private TextView mCityTextView;

    private Button mSearchButton;
    private ImageView mSpecialityImageView;
    private ImageView mCityImageView;
    private ImageView mNameImageView;
    private ImageView mSignInTextView;
    private ImageView mProfileImageView;
    private ImageView mQuestionImageView;

    private String mChosenDoctorName = null;
    private String mChosenSpeciality = null;
    private String mChosenCity = null;

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

        setContentView(R.layout.activity_opening_acvtivity);

        final Intent intent = getIntent();
        mChosenDoctorName = intent.getStringExtra("doctor_name");
        mChosenSpeciality = intent.getStringExtra("speciality");
        mChosenCity = intent.getStringExtra("city");

        mSearchButton = (Button) findViewById(R.id.search_button);
        mSpecialityImageView = (ImageView) findViewById(R.id.speciality_iv);
        mNameImageView = (ImageView) findViewById(R.id.name_iv);
        mCityImageView = (ImageView) findViewById(R.id.city_iv);
        mProfileImageView = (ImageView) findViewById(R.id.profile);
        mQuestionImageView = (ImageView) findViewById(R.id.question_icon);
        mSignInTextView = (ImageView) findViewById(R.id.sign_in_button);
        mNameRelativeLayout = (RelativeLayout) findViewById(R.id.name_rl);
        mSpecialityRelativeLayout = (RelativeLayout) findViewById(R.id.speciality_rl);
        mCityRelativeLayout = (RelativeLayout) findViewById(R.id.city_rl);

        mNameEditText = (EditText) findViewById(R.id.name_tv);
        mSpecialityTextView = (TextView) findViewById(R.id.speciality_tv);
        mCityTextView = (TextView) findViewById(R.id.city_tv);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDoctorsReference = mFirebaseDatabase.getReference().child("new_doctors");

        Typeface nameTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_semibold.ttf");
        mNameEditText.setTypeface(nameTypeface);
        mSpecialityTextView.setTypeface(nameTypeface);
        mCityTextView.setTypeface(nameTypeface);
        mSearchButton.setTypeface(nameTypeface);

        mSpecialityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningAcvtivity.this, GetDataActivity.class);
                intent.putExtra("int_setter",0);
                intent.putExtra("city", mChosenCity);
                intent.putExtra("doctor_name", mChosenDoctorName);
                startActivity(intent);
            }
        });

        mSpecialityRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningAcvtivity.this, GetDataActivity.class);
                intent.putExtra("int_setter",0);
                intent.putExtra("city", mChosenCity);
                intent.putExtra("doctor_name", mChosenDoctorName);
                startActivity(intent);
            }
        });

        mCityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                Intent intent = new Intent(OpeningAcvtivity.this, GetDataActivity.class);
                intent.putExtra("int_setter",1);
                intent.putExtra("doctor_name", mChosenDoctorName);
                intent.putExtra("speciality", mChosenSpeciality);
                startActivity(intent);
            }
        });

        mCityRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningAcvtivity.this, GetDataActivity.class);
                intent.putExtra("int_setter",1);
                intent.putExtra("doctor_name", mChosenDoctorName);
                intent.putExtra("speciality", mChosenSpeciality);
                startActivity(intent);
            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OpeningAcvtivity.this, ProfileActivity.class);
                startActivity(intent1);
            }
        });

        mQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OpeningAcvtivity.this, QuestionListActivity.class);
                intent1.putExtra("doctor_id", "general");
                startActivity(intent1);
            }
        });


        if (mChosenCity!=null){
            mCityTextView.setText(mChosenCity);
        }
        if (mChosenSpeciality != null){
            mSpecialityTextView.setText(mChosenSpeciality);
        }
        if (mChosenDoctorName != null){
            mNameEditText.setText(mChosenDoctorName);
        }
        mSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dial = "tel:" + "+79179040998";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OpeningAcvtivity.this, SearchActivity.class);
                mChosenDoctorName = mNameEditText.getText().toString();
                intent1.putExtra("city", mChosenCity);
                intent1.putExtra("doctor_name", mChosenDoctorName);
                intent1.putExtra("speciality", mChosenSpeciality);

                if (mChosenCity == null && TextUtils.isEmpty(mChosenDoctorName) && mChosenSpeciality == null){
                    Toast.makeText(OpeningAcvtivity.this, "Select a Search option", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(intent1);
                }

            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(OpeningAcvtivity.this, LoginActivity.class));
                }
            }
        };

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
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
        detachDatabaseReadListener();
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDoctorsReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDoctorsReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
