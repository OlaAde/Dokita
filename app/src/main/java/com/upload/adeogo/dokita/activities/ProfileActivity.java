package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements BookingsAdapter.BookingsAdapterOnclickHandler, FavoriteAdapter.FavoriteAdapterOnclickHandler {
    private static final String TAG = "ProfileActivity";
    final boolean[] check = {false, false};

    private ScrollView mProfileScrollView;
    private TextView mCheckDataTextView, mCheckFavoriteDataTextView;

    private RecyclerView mBookingsRecyclerView, mFavoriteRecyclerView;

    private FavoriteAdapter mFavoriteAdapter;
    private BookingsAdapter mBookingsAdapter;

    private LinearLayoutManager mFavoriteManager, mBookingsManager;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDeleteDatabaseReference, mFavoriteDatabaseReference, mDatabaseReference, mFormerDatabaseReference;
    private ChildEventListener mChildEventListener, mFavoriteChildEventListener, mFormerChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Appointment> mAppointmentList = new ArrayList<>();
    private List<Favorite> mFavoriteList = new ArrayList<>();

    public static final String ANONYMOUS = "anonymous";

    private Appointment mPickedAppointment;

    public static final int RC_SIGN_IN = 1;
    private String mUsername, userId, mEmail, mPassword;

    private LinearLayout mBodyMeasurementsLinearLayout, mResultsLinearLayout, mVitalsLinearLayout, mHealthRecordsLinearLayout;

    private TextView mBodyTextView, mResultsTextView, mVitalsTextView, mHealthRecordsTextView;

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

        mProfileScrollView = findViewById(R.id.scrollViewProfile);
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
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" + userId + "/appointments");
                    mFavoriteDatabaseReference = mFirebaseDatabase.getReference().child("users/" + userId + "/favorites");

                    mDeleteDatabaseReference = mFirebaseDatabase.getReference().child("deleted").child("users").child(userId);

                    mFormerDatabaseReference = mFirebaseDatabase.getReference().child("users").child(userId);

                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }

                if (check[0] == false) {
                    mCheckDataTextView.setVisibility(View.VISIBLE);
                } else mCheckDataTextView.setVisibility(View.GONE);
                if (check[1] == false) {
                    mCheckFavoriteDataTextView.setVisibility(View.VISIBLE);
                } else mCheckFavoriteDataTextView.setVisibility(View.GONE);
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

                    if (check[0] == false) {
                        mCheckDataTextView.setVisibility(View.VISIBLE);
                    } else mCheckDataTextView.setVisibility(View.GONE);

                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    mAppointmentList.add(appointment);
                    mBookingsAdapter.swapData(mAppointmentList);
                    mBookingsManager.scrollToPosition(mAppointmentList.size() - 1);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mAppointmentList.remove(dataSnapshot.getValue(Appointment.class));
                    mBookingsAdapter.swapData(mAppointmentList);
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };

            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

        if (mFavoriteChildEventListener == null) {
            mFavoriteChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    check[1] = true;

                    if (check[1] == false) {
                        mCheckFavoriteDataTextView.setVisibility(View.VISIBLE);
                    } else mCheckFavoriteDataTextView.setVisibility(View.GONE);

                    Favorite favorite = dataSnapshot.getValue(Favorite.class);
                    mFavoriteList.add(favorite);
                    mFavoriteAdapter.swapData(mFavoriteList);
                    mFavoriteManager.scrollToPosition(mFavoriteList.size() - 1);

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };

            mFavoriteDatabaseReference.addChildEventListener(mFavoriteChildEventListener);
        }

        if (mFormerChildEventListener == null) {
            mFormerChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (TextUtils.equals(dataSnapshot.getKey(), "email")) {
                        mEmail = dataSnapshot.getValue(String.class);
                    }
                    if (TextUtils.equals(dataSnapshot.getKey(), "password")) {
                        mPassword = dataSnapshot.getValue().toString();
                    }
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mFormerDatabaseReference.addChildEventListener(mFormerChildEventListener);
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

    private void setDialog() {
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
                                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                                    appointmentSnapshot.getRef().removeValue();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_support){
            Intent intent = new Intent(ProfileActivity.this, SupportActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_logout) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
            alertDialog.setTitle("LOGOUT");
            alertDialog.setMessage("Want to Logout?");
            alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                alertDialog.setTitle("DELETE ACCOUNT");
                alertDialog.setMessage("Every reference to this account will be deleted!");

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final android.app.AlertDialog waitingDialog = new SpotsDialog(ProfileActivity.this);
                        waitingDialog.show();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("users").child(userId).removeValue();

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(mEmail, mPassword);

                        mDeleteDatabaseReference.push().setValue(userId);
                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                ref.child("new_doctors").child(userId).removeValue();

                                                if (task.isSuccessful()) {
                                                    waitingDialog.dismiss();
                                                    Snackbar.make(mProfileScrollView, "Account deleted!", Snackbar.LENGTH_LONG);
                                                    mFirebaseAuth.signOut();
                                                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                                }
                            });
                    }
                });
                alertDialog.show();
                }
            });

            alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alertDialog.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

}
