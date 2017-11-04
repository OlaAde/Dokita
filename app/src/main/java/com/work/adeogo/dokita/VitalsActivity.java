package com.work.adeogo.dokita;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.work.adeogo.dokita.models.Vitals;
import com.work.adeogo.dokita.utils.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class VitalsActivity extends AppCompatActivity {
    private int mBloodPressure  = -1;
    private int mBodyTemperature = -1;
    private int mHeartRate = -1;
    private int mRespiratoryRate = -1;

    private TextView mNoInternetTextView;
    private EditText mBloodPressureEditText;
    private EditText mBodyTemperatureEditText;
    private EditText mHeartRateEditText;
    private EditText mRespiratoryRateEditText;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        mBloodPressureEditText = (EditText) findViewById(R.id.blood_pressure_et);
        mBodyTemperatureEditText = (EditText) findViewById(R.id.body_temperature_et);
        mHeartRateEditText = (EditText) findViewById(R.id.heart_rate_et);
        mRespiratoryRateEditText = (EditText) findViewById(R.id.respiratory_rate_et);
        mNoInternetTextView = (TextView) findViewById(R.id.no_internet_vitals);

        boolean isConnect = NetworkUtils.isOnline(this);
        if (!isConnect)
            mNoInternetTextView.setVisibility(View.VISIBLE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vitals");
        actionBar.setDisplayHomeAsUpEnabled(true);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" + userId + "/vitals");
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

        mBloodPressureEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBloodPressureEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private Vitals setData() {
        if (!TextUtils.isEmpty(mBloodPressureEditText.getText().toString())){
            mBloodPressure = Integer.parseInt(mBloodPressureEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mBodyTemperatureEditText.getText().toString())){
            mBodyTemperature = Integer.parseInt(mBodyTemperatureEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mHeartRateEditText.getText().toString())){
            mHeartRate = Integer.parseInt(mHeartRateEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mRespiratoryRateEditText.getText().toString())){
            mRespiratoryRate = Integer.parseInt(mRespiratoryRateEditText.getText().toString());
        }

        Vitals vitals = new Vitals(userId, mBloodPressure, mBodyTemperature, mHeartRate, mRespiratoryRate);

        return vitals;
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
                    Vitals vitals = dataSnapshot.getValue(Vitals.class);

                    if (vitals.getBloodPressure() == -1){
                        mBloodPressureEditText.setText("");
                    }else {
                        mBloodPressureEditText.setText(Integer.toString(vitals.getBloodPressure()));
                    }

                    if (vitals.getBodyTemperature() == -1){
                        mBodyTemperatureEditText.setText("");
                    }else {
                        mBodyTemperatureEditText.setText(Integer.toString(vitals.getBodyTemperature()));
                    }

                    if (vitals.getHeartRate() == -1){
                        mHeartRateEditText.setText("");
                    }else {
                        mHeartRateEditText.setText(Integer.toString(vitals.getHeartRate()));
                    }

                    if (vitals.getRespiratoryRate() == -1){
                        mRespiratoryRateEditText.setText("");
                    }else {
                        mRespiratoryRateEditText.setText(Integer.toString(vitals.getRespiratoryRate()));
                    }

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
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
            Vitals vital = setData();
            if (vital!= null){
                mDatabaseReference.push().setValue(vital);
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
