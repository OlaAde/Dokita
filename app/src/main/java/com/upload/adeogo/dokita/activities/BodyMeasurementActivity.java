package com.upload.adeogo.dokita.activities;

import android.app.Dialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.data.DokitariContract;
import com.upload.adeogo.dokita.models.BodyMeasurement;
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

public class BodyMeasurementActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private int mBodyFatPercentage = -1;
    private int mBodyMassIndex = -1;
    private int mHeight = -1;
    private int mLeanBodyMass = -1;
    private int mWaistCircumference = -1;
    private int mWeight = -1;

    private TextView mNoInternetTextView;

    private TextView mBodyFatTextView;
    private ImageView mBodyFatImageView;

    private EditText mBodyFatEditText;
    private EditText mBodyMassIndexEditText;
    private EditText mHeightEditText;
    private EditText mLeanBodyMassEditText;
    private EditText mWaistCircumferenceEditText;
    private EditText mWeightEditText;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private static final int BODY_MEASUREMENT_LOADER_ID = 1;
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

        setContentView(R.layout.activity_body_measurement);

        mBodyFatEditText = (EditText) findViewById(R.id.body_fat_et);
        mBodyMassIndexEditText = (EditText) findViewById(R.id.body_mass_index_et);
        mLeanBodyMassEditText = (EditText) findViewById(R.id.lean_body_mass_et);
        mHeightEditText = (EditText) findViewById(R.id.height_et);
        mWaistCircumferenceEditText = (EditText) findViewById(R.id.waist_circumference_et);
        mWeightEditText = (EditText) findViewById(R.id.weight_et);
        mNoInternetTextView = (TextView) findViewById(R.id.no_internet_body);
        boolean isConnect = NetworkUtils.isOnline(this);
        if (!isConnect)
            mNoInternetTextView.setVisibility(View.VISIBLE);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // PatientData is signed in
                    userId = user.getUid();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" +userId + "/body_measurements");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // PatientData is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(BodyMeasurementActivity.this, LoginActivity.class));
                }
            }
        };

        mHeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHeightEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });

        setLoader();

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Body Measurements");
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void setLoader(){
        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(BODY_MEASUREMENT_LOADER_ID);
        if (loader == null)
            loaderManager.initLoader(BODY_MEASUREMENT_LOADER_ID, queryBundle, new CursorCallback());
        else
            loaderManager.restartLoader(BODY_MEASUREMENT_LOADER_ID, queryBundle, new CursorCallback());
    }

    public void show()
    {

        final Dialog d = new Dialog(BodyMeasurementActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
        ImageView b1 = (ImageView) d.findViewById(R.id.button1);
        ImageView b2 = (ImageView) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mBodyFatTextView.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mBodyFatTextView.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        d.show();

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is",""+newVal);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private BodyMeasurement setData() {
        if (!TextUtils.isEmpty(mHeightEditText.getText().toString())){
            mHeight = Integer.parseInt(mHeightEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mBodyFatEditText.getText().toString())){
            mBodyFatPercentage = Integer.parseInt(mBodyFatEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mLeanBodyMassEditText.getText().toString())){
            mLeanBodyMass = Integer.parseInt(mLeanBodyMassEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mWaistCircumferenceEditText.getText().toString())){
            mWaistCircumference = Integer.parseInt(mWaistCircumferenceEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mBodyMassIndexEditText.getText().toString())){
            mBodyMassIndex = Integer.parseInt(mBodyMassIndexEditText.getText().toString());
        }

        if (!TextUtils.isEmpty(mWeightEditText.getText().toString())){
            mWeight = Integer.parseInt(mWeightEditText.getText().toString());
        }

        if (mHeight == 0 || mBodyFatPercentage == 0 || mLeanBodyMass == 0 || mWaistCircumference == 0 || mBodyMassIndex == 0 || mWeight == 0){
            if(mHeight == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }else if (mBodyFatPercentage == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }else if (mLeanBodyMass == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }else if (mWaistCircumference == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }else if (mBodyMassIndex == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }else if (mWeight == 0){
                Toast.makeText(this, "Make sure You've filled all fields", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        BodyMeasurement bodyMeasurement = new BodyMeasurement(userId, mBodyFatPercentage, mBodyMassIndex,
                mHeight, mLeanBodyMass, mWaistCircumference, mWeight);
        return bodyMeasurement;
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
            BodyMeasurement bodyMeasurement = setData();
            if (bodyMeasurement!= null){
                mDatabaseReference.push().setValue(bodyMeasurement);
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    BodyMeasurement bodyMeasurement = dataSnapshot.getValue(BodyMeasurement.class);

                    ContentValues bodyMeasurementValues = new ContentValues();
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_USER_ID, bodyMeasurement.getUserId());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_BODY_FAT_PERCENTAGE, bodyMeasurement.getBodyFatPercentage());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_BODY_MASS_INDEX, bodyMeasurement.getBodyMassIndex());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_HEIGHT, bodyMeasurement.getHeight());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_LEAN_BODY_MASS, bodyMeasurement.getLeanBodyMass());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_WAIST_CIRCUMFERENCE, bodyMeasurement.getWaistCircumference());
                    bodyMeasurementValues.put(DokitariContract.BodyMeasurementEntry.COLUMN_WEIGHT, bodyMeasurement.getWeight());

                    if(bodyMeasurementValues!=null){
                        ContentResolver dokitariContentResolver = getContentResolver();

                        dokitariContentResolver.delete(
                                DokitariContract.BodyMeasurementEntry.CONTENT_URI, null, null);

                        dokitariContentResolver.insert(
                                DokitariContract.BodyMeasurementEntry.CONTENT_URI,
                                bodyMeasurementValues);
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

            switch (BODY_MEASUREMENT_LOADER_ID) {

                case BODY_MEASUREMENT_LOADER_ID:
                /* URI for all rows of weather data in our weather table */
                    Uri bodyMeasurementUri = DokitariContract.BodyMeasurementEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */

                    return new CursorLoader(BodyMeasurementActivity.this,
                            bodyMeasurementUri,
                            null,
                            null,
                            null,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + BODY_MEASUREMENT_LOADER_ID);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToLast()){
                int heightIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_HEIGHT);
                int weightIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_WEIGHT);
                int bodyFatIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_BODY_FAT_PERCENTAGE);
                int waistCircumferenceIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_WAIST_CIRCUMFERENCE);
                int bodyMassIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_BODY_MASS_INDEX);
                int leanBodyMassIndex = data.getColumnIndex(DokitariContract.BodyMeasurementEntry.COLUMN_LEAN_BODY_MASS);

                

                if (data.getInt(heightIndex) == -1){
                    mHeightEditText.setText("");
                }else {
                    mHeightEditText.setText(Integer.toString(data.getInt(heightIndex)));
                }

                if (data.getInt(weightIndex) == -1){
                    mWeightEditText.setText("");
                }else {
                    mWeightEditText.setText(Integer.toString(data.getInt(weightIndex)));
                }

                if (data.getInt(bodyFatIndex) == -1){
                    mBodyFatEditText.setText("");
                }else {
                    mBodyFatEditText.setText(Integer.toString(data.getInt(bodyFatIndex)));
                }

                if (data.getInt(waistCircumferenceIndex) == -1){
                    mWaistCircumferenceEditText.setText("");
                }else {
                    mWaistCircumferenceEditText.setText(Integer.toString(data.getInt(waistCircumferenceIndex)));
                }


                if (data.getInt(bodyMassIndex) == -1){
                    mBodyMassIndexEditText.setText("");
                }else {
                    mBodyMassIndexEditText.setText(Integer.toString(data.getInt(bodyMassIndex)));
                }


                if (data.getInt(leanBodyMassIndex) == -1){
                    mLeanBodyMassEditText.setText("");
                }else {
                    mLeanBodyMassEditText.setText(Integer.toString(data.getInt(leanBodyMassIndex)));
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

}
