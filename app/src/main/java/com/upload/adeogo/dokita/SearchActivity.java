package com.upload.adeogo.dokita;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upload.adeogo.dokita.adapters.SearchAdapter;
import com.upload.adeogo.dokita.models.Doctor;
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

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchAdapterOnclickHandler {

    private static final String TAG = "SearchActivity";
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<DoctorProfile> mDoctorList = new ArrayList<>();

    private String urlFemale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_female.png?alt=media&token=eaecb6c4-5f7c-4bd6-ae8f-6a7a73e27a80";
    private String urlMale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_male.png?alt=media&token=e4530bf8-4f41-495d-885e-e2c73007b395";

    private String queryName;
    private String queryCity;
    private String querySpeciality;

    private TextView mNoInternetTextView;

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

//    private List<DoctorProfile> setFakeData() {
//        List<DoctorProfile> DoctorList = new ArrayList<>();
//        DoctorList.add(new DoctorProfile( "76","adeogo.oladipo@gmail.com","12345678", "Dr Dipo Kolawole", "+79179040998", urlMale, "Nigeria" , "Warri","Surgeon"));
//        DoctorList.add(new DoctorProfile( "2","adeogo.oladipo@gmail.com", "12345678","Dr Janet Dipson", "+79179040998",  urlFemale,"Nigeria","Abuja","Gynecologist"));
//        DoctorList.add(new DoctorProfile( "33", "adeogo.oladipo@gmail.com","12345678","Dr Demola John", "+79179040998",  urlMale,"Nigeria", "Lagos", "Surgeon" ));
//        DoctorList.add(new DoctorProfile( "5", "adeogo.oladipo@gmail.com","12345678","Dr Paul Kolade", "+79179040998",  urlMale,  "Russia", "Kazan","Surgeon"));
//        DoctorList.add(new DoctorProfile( "44", "adeogo.oladipo@gmail.com","12345678","Dr Joyce Dumelo", "+79179040998", urlFemale, "Nigeria", "Abuja", "Dentist"));
//        DoctorList.add(new DoctorProfile( "66", "adeogo.oladipo@gmail.com","12345678","Dr John Hames", "+79179040998",  urlFemale, "Uganda", "Mbarara", "Surgeon"));
//        DoctorList.add(new DoctorProfile( "45", "adeogo.oladipo@gmail.com","12345678","Dr Timothy Junior", "+79179040998",  urlMale, "Nigeria", "Ado Ekiti", "Cardiologist"));
//        DoctorList.add(new DoctorProfile( "505", "adeogo.oladipo@gmail.com","12345678","Dr Jola James", "+79179040998",  urlFemale, "Nigeria", "Ado Ekiti", "Digestive surgeon"));
//        DoctorList.add(new DoctorProfile( "506", "adeogo.oladipo@gmail.com","12345678","Dr Johnson Jamez", "+79179040998", urlMale, "Nigeria", "Ado Ekiti", "Hepatologist"));
//        DoctorList.add(new DoctorProfile( "496", "adeogo.oladipo@gmail.com","12345678","Dr Janet Demson", "+79179040998",  urlFemale, "Nigeria", "Ado Ekiti", "Oncologist"));
//        DoctorList.add(new DoctorProfile( "403", "adeogo.oladipo@gmail.com","12345678","Dr Cameron Junior", "+79179040998", urlMale, "Uganda", "Entebbe", "Plastic Surgeon"));
//        DoctorList.add(new DoctorProfile( "55", "adeogo.oladipo@gmail.com","12345678","Dr Timothy Jolade", "+79179040998", urlMale, "Nigeria", "Ado Ekiti", "General Surgeon"));
//        DoctorList.add(new DoctorProfile( "96", "adeogo.oladipo@gmail.com","12345678","Dr Isaac Moses", "+79179040998", urlMale, "Nigeria", "Ado Ekiti", "Cardiologist"));
//        DoctorList.add(new DoctorProfile( "99", "adeogo.oladipo@gmail.com","12345678","Dr Makata Junior", "+79179040998", urlMale, "Nigeria", "Ado Ekiti", "General Surgeon"));
//        DoctorList.add(new DoctorProfile( "77", "adeogo.oladipo@gmail.com","12345678","Dr Juliet Junior", "+79179040998", urlFemale, "Uganda", "Kampala", "Cardiologist"));
//        DoctorList.add(new DoctorProfile( "85", "adeogo.oladipo@gmail.com","12345678","Dr Dola Modupe James", "+79179040998", urlFemale, "Uganda", "Entebbe", "Ear nose throat Doctor"));
//        DoctorList.add(new DoctorProfile( "26", "adeogo.oladipo@gmail.com","12345678","Dr Katerina Petrova", "+79179040998", urlFemale, "Russia", "Kazan", "Cardiologist"));
//        DoctorList.add(new DoctorProfile( "88", "adeogo.oladipo@gmail.com","12345678","Dr Olade Olamide", "+79179040998", urlFemale, "Nigeria", "Ado Ekiti", "Diabetologist"));
//        DoctorList.add(new DoctorProfile( "78", "adeogo.oladipo@gmail.com","12345678","Dr Timothy jacket", "+79179040998",urlMale, "Uganda", "Gulu", "Cardiologist"));
//        DoctorList.add(new DoctorProfile( "65", "adeogo.oladipo@gmail.com","12345678","Dr Titus James", "+79179040998", urlMale, "Uganda", "Wakiso", "General Surgeon"));
//        DoctorList.add(new DoctorProfile( "56", "adeogo.oladipo@gmail.com","12345678","Dr Justina Japhet", "+79179040998", urlFemale, "Nigeria", "Ado Ekiti", "Plastic Surgeon"));
//        DoctorList.add(new DoctorProfile( "693", "adeogo.oladipo@gmail.com","12345678","Dr Sergei Kasporov", "+79179040998", urlMale, "Russia", "Moscow", "Cardiologist"));
//
//        return DoctorList;
//    }

    @Override
    public void voidMethod(List<DoctorProfile> list, int adapterPosition) {
        DoctorProfile doctor = list.get(adapterPosition);
        Intent intent = new Intent(SearchActivity.this, OrderActivity.class);
        intent.putExtra("name", doctor.getName());
        intent.putExtra("phone_number", doctor.getDoctorPhoneNumber());
        intent.putExtra("speciality", doctor.getSpeciality());
        intent.putExtra("picture_url", doctor.getPictureUrl());
        intent.putExtra("doctor_id", doctor.getDoctorId());
        startActivity(intent);
    }

//    public void setDataNow(View view) {
//        List<DoctorProfile> listed  =  setFakeData();
//        mDoctorsReference.push().setValue(listed);
//    }
}
