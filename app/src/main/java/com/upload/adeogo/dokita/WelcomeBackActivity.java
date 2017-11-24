package com.upload.adeogo.dokita;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upload.adeogo.dokita.models.DoctorProfile;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeBackActivity extends AppCompatActivity {

    private TextView mWelcomeTextView;
    private TextView mNameWelcomeTextView;
    private ImageView mWelcomeImageView;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChildEventListener;
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);
        mWelcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        mNameWelcomeTextView = (TextView) findViewById(R.id.welcomeNameTextView);
        mWelcomeImageView = (ImageView) findViewById(R.id.background_image_welcome);

        Stetho.initializeWithDefaults(this);

        Typeface greetingsTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_light_italic.ttf");
        Typeface nameTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_light.ttf");
        mWelcomeTextView.setTypeface(greetingsTypeface);
        mNameWelcomeTextView.setTypeface(nameTypeface);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();

                    mReference = mDatabase.getReference().child("new_doctors").child("all_profiles").child(userId).child("name");

                    mUsername = user.getDisplayName();
                    setGreetingText();
                    onSignedInInitialize(user.getDisplayName());

                    new Timer().schedule(new TimerTask(){
                        public void run() {
                            Intent intent = new Intent(WelcomeBackActivity.this, OpeningAcvtivity.class);
                            startActivity(intent);
                        }
                    }, 2000);
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

    }

    private void setGreetingText(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            mWelcomeTextView.setText( this.getString(R.string.good_morning_text));
            mNameWelcomeTextView.setText(mUsername);
            mWelcomeImageView.setImageResource(R.drawable.morning);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            mWelcomeTextView.setText(this.getString(R.string.good_afternoon_text));
            mNameWelcomeTextView.setText(mUsername);
            mWelcomeImageView.setImageResource(R.drawable.afternoon);
        }else{
            mWelcomeTextView.setText(this.getString(R.string.good_evening_text));
            mNameWelcomeTextView.setText(mUsername);
            mWelcomeImageView.setImageResource(R.drawable.evening);
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
                    String doctorName =  dataSnapshot.getValue(String.class);
                    if (doctorName != null){
                       mUsername = doctorName;

                        Toast.makeText(WelcomeBackActivity.this, doctorName, Toast.LENGTH_SHORT).show();
                    }
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


}
