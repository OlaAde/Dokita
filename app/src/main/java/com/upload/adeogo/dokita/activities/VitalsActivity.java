package com.upload.adeogo.dokita.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.data.DokitariContract;
import com.upload.adeogo.dokita.models.Vitals;
import com.upload.adeogo.dokita.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    private static final int VITALS_LOADER_ID = 1;
    private String mUsername;
    private String userId;

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

        setContentView(R.layout.activity_vitals);

        mBloodPressureEditText = (EditText) findViewById(R.id.blood_pressure_et);
        mBodyTemperatureEditText = (EditText) findViewById(R.id.body_temperature_et);
        mHeartRateEditText = (EditText) findViewById(R.id.heart_rate_et);
        mRespiratoryRateEditText = (EditText) findViewById(R.id.respiratory_rate_et);
        mNoInternetTextView = (TextView) findViewById(R.id.no_internet_vitals);


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
                    startActivity(new Intent(VitalsActivity.this, LoginActivity.class));
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

        setLoader();
    }

    private void setLoader(){
        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(VITALS_LOADER_ID);
        if (loader == null)
            loaderManager.initLoader(VITALS_LOADER_ID, queryBundle, new VitalsActivity.CursorCallback());
        else
            loaderManager.restartLoader(VITALS_LOADER_ID, queryBundle, new VitalsActivity.CursorCallback());
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

                    ContentValues vitalsValues = new ContentValues();
                    vitalsValues.put(DokitariContract.VitalsEntry.COLUMN_USER_ID, vitals.getUserId());
                    vitalsValues.put(DokitariContract.VitalsEntry.COLUMN_BLOOD_PRESSURE, vitals.getBloodPressure());
                    vitalsValues.put(DokitariContract.VitalsEntry.COLUMN_BODY_TEMPERATURE, vitals.getBodyTemperature());
                    vitalsValues.put(DokitariContract.VitalsEntry.COLUMN_HEART_RATE, vitals.getHeartRate());
                    vitalsValues.put(DokitariContract.VitalsEntry.COLUMN_RESPIRATORY_RATE, vitals.getRespiratoryRate());

                    if(vitalsValues!=null){
                        ContentResolver dokitariContentResolver = getContentResolver();

                        dokitariContentResolver.delete(
                                DokitariContract.VitalsEntry.CONTENT_URI, null, null);

                        dokitariContentResolver.insert(
                                DokitariContract.VitalsEntry.CONTENT_URI,
                                vitalsValues);
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
                if ( NetworkUtils.isOnline(this)){
                    mDatabaseReference.push().setValue(vital);
                    Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(this, "No Internet To Update!", Toast.LENGTH_SHORT).show();
                }
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

    public class CursorCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            switch (VITALS_LOADER_ID) {

                case VITALS_LOADER_ID:
                /* URI for all rows of weather data in our weather table */
                    Uri vitalsUri = DokitariContract.VitalsEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */

                    return new CursorLoader(VitalsActivity.this,
                            vitalsUri,
                            null,
                            null,
                            null,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + VITALS_LOADER_ID);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToLast()){
                int bloodPressureIndex = data.getColumnIndex(DokitariContract.VitalsEntry.COLUMN_BLOOD_PRESSURE);
                int bodyTemperatureIndex = data.getColumnIndex(DokitariContract.VitalsEntry.COLUMN_BODY_TEMPERATURE);
                int heartRateIndex = data.getColumnIndex(DokitariContract.VitalsEntry.COLUMN_HEART_RATE);
                int respiratoryRateIndex = data.getColumnIndex(DokitariContract.VitalsEntry.COLUMN_RESPIRATORY_RATE);


                if (data.getInt(bloodPressureIndex) == -1){
                    mBloodPressureEditText.setText("");
                }else {
                    mBloodPressureEditText.setText(Integer.toString(data.getInt(bloodPressureIndex)));
                }

                if (data.getInt(bodyTemperatureIndex) == -1){
                    mBodyTemperatureEditText.setText("");
                }else {
                    mBodyTemperatureEditText.setText(Integer.toString(data.getInt(bodyTemperatureIndex)));
                }

                if (data.getInt(heartRateIndex) == -1){
                    mHeartRateEditText.setText("");
                }else {
                    mHeartRateEditText.setText(Integer.toString(data.getInt(heartRateIndex)));
                }

                if (data.getInt(respiratoryRateIndex) == -1){
                    mRespiratoryRateEditText.setText("");
                }else {
                    mRespiratoryRateEditText.setText(Integer.toString(data.getInt(respiratoryRateIndex)));
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

}
