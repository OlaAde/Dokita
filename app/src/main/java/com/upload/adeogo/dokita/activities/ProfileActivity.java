package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.BookingsAdapter;
import com.upload.adeogo.dokita.adapters.FavoriteAdapter;
import com.upload.adeogo.dokita.models.Appointment;
import com.upload.adeogo.dokita.models.Favorite;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements BookingsAdapter.BookingsAdapterOnclickHandler, FavoriteAdapter.FavoriteAdapterOnclickHandler {
    private static final String TAG = "ProfileActivity" ;
    final boolean[] check = {false, false};

    private TextView mCheckDataTextView;
    private TextView mCheckFavoriteDataTextView;

    private RecyclerView mBookingsRecyclerView;
    private RecyclerView mFavoriteRecyclerView;

    private FavoriteAdapter mFavoriteAdapter;
    private BookingsAdapter mBookingsAdapter;

    private LinearLayoutManager mFavoriteManager;
    private LinearLayoutManager mBookingsManager;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFavoriteDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mFavoriteChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Appointment> mAppointmentList = new ArrayList<>();
    private List<Favorite> mFavoriteList = new ArrayList<>();

    public static final String ANONYMOUS = "anonymous";

    private Appointment mPickedAppointment;

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;

    private LinearLayout mBodyMeasurementsLinearLayout;
    private LinearLayout mResultsLinearLayout;
    private LinearLayout mVitalsLinearLayout;
    private LinearLayout mHealthRecordsLinearLayout;

    private TextView mBodyTextView;
    private TextView mResultsTextView;
    private TextView mVitalsTextView;
    private TextView mHealthRecordsTextView;

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

        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBookingsRecyclerView = findViewById(R.id.bookings_rv);
        mFavoriteRecyclerView = findViewById(R.id.favourites_rv);
        mCheckDataTextView = findViewById(R.id.no_appointment_tv);
        mCheckFavoriteDataTextView = findViewById(R.id.no_favorite_tv);

        mBodyMeasurementsLinearLayout = (LinearLayout) findViewById(R.id.body_measurements_ll);
        mVitalsLinearLayout = (LinearLayout) findViewById(R.id.vitals_ll);
        mResultsLinearLayout = (LinearLayout) findViewById(R.id.results_ll);
        mHealthRecordsLinearLayout = (LinearLayout) findViewById(R.id.health_records_ll);
        mBodyTextView = findViewById(R.id.body_tv);
        mHealthRecordsTextView = findViewById(R.id.health_records_tv);
        mResultsTextView = findViewById(R.id.results_tv);
        mVitalsTextView = findViewById(R.id.vitals_tv);

        Typeface semiBoldTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_semibold.ttf");
        Typeface italicsTypeface = Typeface.createFromAsset(getAssets(), "font/open_sans_light_italic.ttf");
        mBodyTextView.setTypeface(semiBoldTypeface);
        mHealthRecordsTextView.setTypeface(semiBoldTypeface);
        mResultsTextView.setTypeface(semiBoldTypeface);
        mVitalsTextView.setTypeface(semiBoldTypeface);

        mBodyMeasurementsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, BodyMeasurementActivity.class);
                startActivity(intent);
            }
        });


        mVitalsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, VitalsActivity.class);
                startActivity(intent);
            }
        });

        mResultsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

        mHealthRecordsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HealthRecordsActivity.class);
                startActivity(intent);
            }
        });


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
                    mFavoriteDatabaseReference = mFirebaseDatabase.getReference().child("users/" +userId + "/favorites");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }

                if (check[0] == false){
                    mCheckDataTextView.setVisibility(View.VISIBLE);
                }else mCheckDataTextView.setVisibility(View.GONE);
                if (check[1] == false){
                    mCheckFavoriteDataTextView.setVisibility(View.VISIBLE);
                }else mCheckFavoriteDataTextView.setVisibility(View.GONE);
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
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mAppointmentList.remove(dataSnapshot.getValue(Appointment.class));
                    mBookingsAdapter.swapData(mAppointmentList);
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };

            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

        if (mFavoriteChildEventListener == null) {
            mFavoriteChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    check[1] = true;

                    if (check[1] == false){
                        mCheckFavoriteDataTextView.setVisibility(View.VISIBLE);
                    }else mCheckFavoriteDataTextView.setVisibility(View.GONE);

                    Favorite favorite = dataSnapshot.getValue(Favorite.class);
                    mFavoriteList.add(favorite);
                    mFavoriteAdapter.swapData(mFavoriteList);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };

            mFavoriteDatabaseReference.addChildEventListener(mFavoriteChildEventListener);
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
    public void voidMethod(List<Favorite> list, int adapterPosition) {

    }

    @Override
    public void voidMethodBooking(List<Appointment> list, int adapterPosition) {

    }

    @Override
    public void cancelAppointment(List<Appointment> list, int adapterPosition) {
        mPickedAppointment = list.get(adapterPosition);
        setDialog();
    }

    private void setDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Query appointmentQuery = mDatabaseReference.orderByChild("doctorId").equalTo(mPickedAppointment.getDoctorId());
                        appointmentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appointmentSnapshot: dataSnapshot.getChildren()) {
                                    appointmentSnapshot.getRef().removeValue();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });}
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
