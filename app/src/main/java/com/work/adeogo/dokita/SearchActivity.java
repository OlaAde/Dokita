package com.work.adeogo.dokita;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.work.adeogo.dokita.adapters.SearchAdapter;
import com.work.adeogo.dokita.models.Doctor;
import com.work.adeogo.dokita.utils.NetworkUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchAdapterOnclickHandler {

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<Doctor> mDoctorList;

    private String urlFemale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_female.png?alt=media&token=eaecb6c4-5f7c-4bd6-ae8f-6a7a73e27a80";
    private String urlMale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_male.png?alt=media&token=e4530bf8-4f41-495d-885e-e2c73007b395";

    private String queryName;
    private String queryCity;
    private String querySpeciality;

    private TextView mNoInternetTextView;

    private List<Doctor> finale = setFakeData();

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDoctorsReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Result");
        mRecyclerView = (RecyclerView) findViewById(R.id.search_rv);
        mNoInternetTextView = (TextView) findViewById(R.id.no_internet_search);

        boolean isConnect = NetworkUtils.isOnline(this);
        if (!isConnect)
            mNoInternetTextView.setVisibility(View.VISIBLE);
        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDoctorsReference = mFirebaseDatabase.getReference().child("doctors");

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
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                mDoctorList.add(doctor);
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

        mDoctorsReference.addChildEventListener(mChildEventListener);
        setFakeData();

        if (queryName!=null){
            iterator(queryName , 0);
        }

        if (queryCity!=null){
            iterator(queryCity, 1);
        }

        if (querySpeciality!=null){
            iterator(querySpeciality, 2);
        }

        mAdapter.swapData(mDoctorList);
    }

    private List<Doctor> setFakeData() {
        mDoctorList = new ArrayList<>();
        mDoctorList.add(new Doctor(0, "0", "Dr Dipo Kolawole", null, null, urlMale , "Surgeon", "+79179040998", "Warri", "Nigeria"));
        mDoctorList.add(new Doctor(1, "2", "Dr Janet Dipson", null, null, urlFemale, "Gynecologist", "+79179040998", "Abuja", "Nigeria"));
        mDoctorList.add(new Doctor(0, "33", "Dr Demola John", null, null, urlMale, "Surgeon", "+79179040998", "Lagos", "Nigeria"));
        mDoctorList.add(new Doctor(0, "5", "Dr Paul Kolade", null, null, urlMale, "Surgeon", "+79179040998", "Kazan", "Russia"));
        mDoctorList.add(new Doctor(1, "44", "Dr Joyce Dumelo", null, null, urlFemale, "Dentist", "+79179040998", "Abuja", "Nigeria"));
        mDoctorList.add(new Doctor(1, "66", "Dr John Hames", null, null, urlFemale, "Surgeon", "+79179040998", "Mbarara", "Uganda"));
        mDoctorList.add(new Doctor(0, "45", "Dr Timothy Junior", null, null, urlMale, "Cardiologist", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(1, "505", "Dr Jola James", null, null, urlFemale, "Digestive surgeon", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "506", "Dr Johnson Jamez", null, null, urlMale, "Hepatologist", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(1, "496", "Dr Janet Demson", null, null, urlFemale, "Oncologist", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "403", "Dr Cameron Junior", null, null, urlMale, "Plastic Surgeon", "+79179040998", "Entebbe", "Uganda"));
        mDoctorList.add(new Doctor(0, "55", "Dr Timothy Jolade", null, null, urlMale, "General Surgeon", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "96", "Dr Isaac Moses", null, null, urlMale, "Cardiologist", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "99", "Dr Makata Junior", null, null, urlMale, "General Surgeon", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(1, "77", "Dr Juliet Junior", null, null, urlFemale, "Cardiologist", "+79179040998", "Kampala", "Uganda"));
        mDoctorList.add(new Doctor(1, "85", "Dr Dola Modupe James", null, null, urlFemale, "Ear nose throat Doctor", "+79179040998", "Entebbe", "Uganda"));
        mDoctorList.add(new Doctor(1, "26", "Dr Katerina Petrova", null, null, urlFemale, "Cardiologist", "+79179040998", "Kazan", "Russia"));
        mDoctorList.add(new Doctor(1, "88", "Dr Olade Olamide", null, null, urlFemale, "Diabetologist", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "78", "Dr Timothy jacket", null, null, urlMale, "Cardiologist", "+79179040998", "Gulu", "Uganda"));
        mDoctorList.add(new Doctor(0, "65", "Dr Titus James", null, null, urlMale, "General Surgeon", "+79179040998", "Wakiso", "Uganda"));
        mDoctorList.add(new Doctor(1, "56", "Dr Justina Japhet", null, null, urlFemale, "Plastic Surgeon", "+79179040998", "Ado Ekiti", "Nigeria"));
        mDoctorList.add(new Doctor(0, "693", "Dr Sergei Kasporov", null, null, urlMale, "Cardiologist", "+79179040998", "Moscow", "Russia"));

        return mDoctorList;
    }
    private void iterator(CharSequence constraint, int which){
        final int count = 100;

        if(!TextUtils.isEmpty(constraint)){
            String constraintString = constraint.toString().toLowerCase(Locale.ROOT);
            Iterator<Doctor> iter = mDoctorList.iterator();
            while(iter.hasNext()){
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
    public void voidMethod(List<Doctor> list, int adapterPosition) {
        Doctor doctor = list.get(adapterPosition);
        Intent intent = new Intent(SearchActivity.this, OrderActivity.class);
        intent.putExtra("name", doctor.getName());
        intent.putExtra("phone_number", doctor.getPhoneNumber());
        intent.putExtra("speciality", doctor.getSpeciality());
        intent.putExtra("picture_url", doctor.getImageUrl());
        intent.putExtra("doctor_id", doctor.getId());
        startActivity(intent);
    }
}
