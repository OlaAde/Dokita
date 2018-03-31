package com.upload.adeogo.dokita.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.NewSearchAdapter;
import com.upload.adeogo.dokita.adapters.SearchAdapter;
import com.upload.adeogo.dokita.models.DoctorProfile;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements  NewSearchAdapter.NewSearchAdapterOnclickHandler {

    private final String TAG = "Collaborate Fragment";

    private LinearLayoutManager mLayoutManager;
    private NewSearchAdapter mSearchAdapter;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private TextView mNoDoctor;
    private List<String> mDoctorNameList = new ArrayList<>();
    private List<DoctorProfile> mDoctorProfileList = new ArrayList<>();
    private List<DoctorProfile> mDoctorSearchProfileList = new ArrayList<>();

    private ValueEventListener mValueEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private Query mDatabaseReference, mSelfReference;
    private ChildEventListener mChildEventListener;

    private String mUserId, mSearchQuery = "", mSelfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        mSearchQuery = intent.getStringExtra("query");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mSearchView = (SearchView) findViewById(R.id.searchview);
        mSearchAdapter = new NewSearchAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);
        mProgressBar = findViewById(R.id.progressBar);
        mNoDoctor = findViewById(R.id.noDoctor);


        mRecyclerView.setAdapter(mSearchAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        String userId = mFirebaseAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("new_doctors").child("all_profiles");
        mSelfReference = mFirebaseDatabase.getReference().child("users").child(userId);
        setData();

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mSearchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mSearchAdapter.getFilter().filter(query);
                return false;
            }

        });


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
    }

    private void setData() {

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean available = dataSnapshot.getValue(Boolean.class);
                if (available) {
                    mProgressBar.setVisibility(View.GONE);
                    mNoDoctor.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mNoDoctor.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DoctorProfile doctorProfile = dataSnapshot.getValue(DoctorProfile.class);
                if (!TextUtils.equals(doctorProfile.getDoctorId(), mUserId)){
                    mProgressBar.setVisibility(View.GONE);
                    mDoctorProfileList.add(doctorProfile);
                    mSearchAdapter.swapData(null);
                    mSearchAdapter.swapData(mDoctorProfileList);
                    mSearchView.setQuery(mSearchQuery,true);
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
        mDatabaseReference.addChildEventListener(mChildEventListener);
        mSearchAdapter.swapData(mDoctorProfileList);

    }



    @Override
    public void voidMethod(List<DoctorProfile> list, int adapterPosition) {
        DoctorProfile doctor = list.get(adapterPosition);
        Intent intent = new Intent(SearchResultActivity.this, OrderActivity.class);
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
