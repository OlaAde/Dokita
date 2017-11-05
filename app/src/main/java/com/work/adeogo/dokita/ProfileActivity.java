package com.work.adeogo.dokita;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.work.adeogo.dokita.adapters.BookingsAdapter;
import com.work.adeogo.dokita.adapters.FavoriteAdapter;
import com.work.adeogo.dokita.models.Appointment;
import com.work.adeogo.dokita.models.BodyMeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements BookingsAdapter.BookingsAdapterOnclickHandler, FavoriteAdapter.FavoriteAdapterOnclickHandler {
    final boolean[] check = {false};

    private TextView mCheckDataTextView;

    private RecyclerView mBookingsRecyclerView;
    private RecyclerView mFavoriteRecyclerView;

    private FavoriteAdapter mFavoriteAdapter;
    private BookingsAdapter mBookingsAdapter;

    private LinearLayoutManager mFavoriteManager;
    private LinearLayoutManager mBookingsManager;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Appointment> mAppointmentList = new ArrayList<>();

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mBookingsRecyclerView = findViewById(R.id.bookings_rv);
        mFavoriteRecyclerView = findViewById(R.id.favourites_rv);
        mCheckDataTextView = findViewById(R.id.no_appointment_tv);

        mBookingsAdapter = new BookingsAdapter(this, this);
        mFavoriteAdapter = new FavoriteAdapter(this, this);

        mBookingsManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mFavoriteManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mFavoriteRecyclerView.setLayoutManager(mFavoriteManager);
        mBookingsRecyclerView.setLayoutManager(mBookingsManager);

        mFavoriteRecyclerView.setAdapter(mFavoriteAdapter);
        mBookingsRecyclerView.setAdapter(mBookingsAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" +userId + "/appointments");
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

                if (check[0] == false){
                    mCheckDataTextView.setVisibility(View.VISIBLE);
                }else mCheckDataTextView.setVisibility(View.GONE);
            }
        };

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                    check[0] = true;

                    if (check[0] == false){
                        mCheckDataTextView.setVisibility(View.VISIBLE);
                    }else mCheckDataTextView.setVisibility(View.GONE);

                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    mAppointmentList.add(appointment);
                    mBookingsAdapter.swapData(mAppointmentList);
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


    @Override
    public void voidMethod(List<String> list, int adapterPosition) {

    }

    @Override
    public void voidMethodBooking(List<Appointment> list, int adapterPosition) {

    }
}
