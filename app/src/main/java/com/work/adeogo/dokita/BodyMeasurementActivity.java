package com.work.adeogo.dokita;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.work.adeogo.dokita.models.BodyMeasurement;
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
    private String mUsername;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    // User is signed in
                    userId = user.getUid();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" +userId + "/body_measurements");
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

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Body Measurements");
        actionBar.setDisplayHomeAsUpEnabled(true);
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

                    if (bodyMeasurement.getHeight() == -1){
                        mHeightEditText.setText("");
                    }else {
                        mHeightEditText.setText(Integer.toString(bodyMeasurement.getHeight()));
                    }

                    if (bodyMeasurement.getWeight() == -1){
                        mWeightEditText.setText("");
                    }else {
                        mWeightEditText.setText(Integer.toString(bodyMeasurement.getWeight()));
                    }

                    if (bodyMeasurement.getBodyFatPercentage() == -1){
                        mBodyFatEditText.setText("");
                    }else {
                        mBodyFatEditText.setText(Integer.toString(bodyMeasurement.getBodyFatPercentage()));
                    }

                    if (bodyMeasurement.getWaistCircumference() == -1){
                        mWaistCircumferenceEditText.setText("");
                    }else {
                        mWaistCircumferenceEditText.setText(Integer.toString(bodyMeasurement.getWaistCircumference()));
                    }


                    if (bodyMeasurement.getBodyMassIndex() == -1){
                        mBodyMassIndexEditText.setText("");
                    }else {
                        mBodyMassIndexEditText.setText(Integer.toString(bodyMeasurement.getBodyMassIndex()));
                    }


                    if (bodyMeasurement.getLeanBodyMass() == -1){
                        mLeanBodyMassEditText.setText("");
                    }else {
                        mLeanBodyMassEditText.setText(Integer.toString(bodyMeasurement.getLeanBodyMass()));
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

}
