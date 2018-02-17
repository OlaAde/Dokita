package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.SearchAdapter;
import com.upload.adeogo.dokita.models.DoctorProfile;
import com.upload.adeogo.dokita.utils.NetworkUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchAdapterOnclickHandler {

    private static final String TAG = "SearchActivity";
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<DoctorProfile> mDoctorList = new ArrayList<>();

    private String urlFemale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_female.png?alt=media&token=eaecb6c4-5f7c-4bd6-ae8f-6a7a73e27a80";
    private String urlMale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_male.png?alt=media&token=e4530bf8-4f41-495d-885e-e2c73007b395";

    private String queryName, mSelfUrl, userId;
    private String queryCity;
    private String querySpeciality;

    private TextView mNoDataTextView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDoctorsReference, mSelfReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mListener;


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

        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Result");
        mRecyclerView = (RecyclerView) findViewById(R.id.search_rv);
        mNoDataTextView = (TextView) findViewById(R.id.no_result_search);

        boolean isConnect = NetworkUtils.isOnline(this);

        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        String userId = mFirebaseAuth.getCurrentUser().getUid();

        mSelfReference = mFirebaseDatabase.getReference().child("users").child(userId);
        mDoctorsReference = mFirebaseDatabase.getReference().child("new_doctors/all_profiles");


        mManager = new LinearLayoutManager(this);
        mAdapter = new SearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mManager);
        Intent intent = getIntent();
        queryName = intent.getStringExtra("doctor_name");
        queryCity = intent.getStringExtra("city");
        querySpeciality = intent.getStringExtra("speciality");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (TextUtils.equals(dataSnapshot.getKey(), "photoUrl")){
                    mSelfUrl = dataSnapshot.getValue().toString();
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


        mSelfReference.addChildEventListener(mChildEventListener);

        Query appointmentQuery = null;
        if (queryName!=null){
            appointmentQuery = mDoctorsReference.orderByChild("name");
        }

        if (queryCity!=null){
            appointmentQuery = mDoctorsReference.orderByChild("city").equalTo(queryCity);
        }

        if (querySpeciality!=null){
            appointmentQuery = mDoctorsReference.orderByChild("speciality").equalTo(querySpeciality);
        }


        appointmentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appointmentSnapshot: dataSnapshot.getChildren()) {

                    mNoDataTextView.setVisibility(View.GONE);

                    DoctorProfile doctor = appointmentSnapshot.getValue(DoctorProfile.class);
                    mDoctorList.add(doctor);
                    mAdapter.swapData(null);
                    mAdapter.swapData(mDoctorList);
                }
                mAdapter.swapData(mDoctorList);

                if (queryName!=null){
            iterator(queryName , 0);
                }

                if (queryCity!=null){
            iterator(queryCity, 1);
                }

                if (querySpeciality!=null){
                    iterator(querySpeciality, 2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });




    }

    private void iterator(CharSequence constraint, int which){
        final int count = 100;

        if(!TextUtils.isEmpty(constraint)){
            String constraintString = constraint.toString().toLowerCase(Locale.ROOT);
            Iterator<DoctorProfile> iter = mDoctorList.iterator();
            while(iter.hasNext() && mDoctorList.size()!= 0){
                if (which == 0){
                    if(!iter.next().getName().toLowerCase(Locale.ROOT).contains(constraintString))
                    {
                        iter.remove();
                    }
                }else if (which ==1){
                    if(!iter.next().getCity().toLowerCase(Locale.ROOT).contains(constraintString))
                    {
                        iter.remove();
                    }
                }else if (which ==2){
                    if(!iter.next().getSpeciality().toLowerCase(Locale.ROOT).contains(constraintString))
                    {
                        iter.remove();
                    }
                }
            }
        }
    }

    @Override
    public void voidMethod(List<DoctorProfile> list, int adapterPosition) {
        DoctorProfile doctor = list.get(adapterPosition);
        Intent intent = new Intent(SearchActivity.this, OrderActivity.class);
        intent.putExtra("name", doctor.getName());
        intent.putExtra("phone_number", doctor.getDoctorPhoneNumber());
        intent.putExtra("speciality", doctor.getSpeciality());
        intent.putExtra("picture_url", doctor.getPictureUrl());
        intent.putExtra("doctor_id", doctor.getDoctorId());
        intent.putExtra("start_time_hour", doctor.getStartHour());
        intent.putExtra("start_time_minute", doctor.getStartMinute());
        intent.putExtra("end_time_hour", doctor.getEndHour());
        intent.putExtra("end_time_minute", doctor.getEndMinute());
        intent.putExtra("sunday", doctor.getSunday());
        intent.putExtra("monday", doctor.getMonday());
        intent.putExtra("tuesday", doctor.getTuesday());
        intent.putExtra("wednesday", doctor.getWednesday());
        intent.putExtra("thursday", doctor.getThursday());
        intent.putExtra("friday", doctor.getFirday());
        intent.putExtra("saturday", doctor.getSaturday());
        intent.putExtra("pictureUrl", doctor.getPictureUrl());
        intent.putExtra("selfPictureUrl", mSelfUrl);

        startActivity(intent);
    }

}
