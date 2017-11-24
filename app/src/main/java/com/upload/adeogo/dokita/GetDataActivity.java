package com.upload.adeogo.dokita;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.widget.Toast;

import com.upload.adeogo.dokita.adapters.ListaAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GetDataActivity extends AppCompatActivity implements ListaAdapter.ListAdapterOnclickHandler {

    private List<String> mDoctorsList;
    private List<String> mSpecitlityList;
    private List<String> mCityList;

    private Context mContext;

    private RecyclerView mRecyclerView;
    private ListaAdapter mAdapter;
    private LinearLayoutManager mManager;
    private SearchView mSearchView;

    private List<String> mList;
    private int mSetterInt;

    private String mChosenDoctorName = null;
    private String mChosenSpeciality = null;
    private String mChosenCity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = GetDataActivity.this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mSearchView = (SearchView) findViewById(R.id.searchview);
        mAdapter = new ListaAdapter(this, this);
        mManager = new LinearLayoutManager(this);


        Intent intent = getIntent();
        mSetterInt = intent.getIntExtra("int_setter", 0);

        mSearchView.setQueryHint("Search Doctors");
        setfakeData();
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
        if (mSetterInt == 0 ){
            mAdapter.swapData(mSpecitlityList);
            mList = mSpecitlityList;
            mChosenCity = intent.getStringExtra("city");
            mChosenDoctorName = intent.getStringExtra("doctor_name");
        }
        else if (mSetterInt == 1){
            mAdapter.swapData(mCityList);
            mList = mCityList;
            mChosenSpeciality = intent.getStringExtra("speciality");
            mChosenDoctorName = intent.getStringExtra("doctor_name");
        }
        else if(mSetterInt == 2){
            mAdapter.swapData(mDoctorsList);
            mList = mDoctorsList;
            mChosenCity = intent.getStringExtra("city");
            mChosenSpeciality = intent.getStringExtra("speciality");
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(mContext,"query" + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                iterator(newText);
                return true;
            }
        });
    }

    private void setfakeData() {

        if (mSetterInt == 0 ){
            mSpecitlityList = new ArrayList<>();
            mSpecitlityList.add("Paediatrician");
            mSpecitlityList.add("Dentist");
            mSpecitlityList.add("Ophthalmologist");
            mSpecitlityList.add("Dermatologist");
            mSpecitlityList.add("Gynecologist");
            mSpecitlityList.add("Digestive surgeon");
            mSpecitlityList.add("Hepatologist");
            mSpecitlityList.add("Cardiologist");
            mSpecitlityList.add("Diabetologist");
            mSpecitlityList.add("Ear nose throat Doctor");
            mSpecitlityList.add("General Surgeon");
            mSpecitlityList.add("Geneticist");
            mSpecitlityList.add( "gynecologist");
            mSpecitlityList.add( "General Physiciain");
            mSpecitlityList.add( "Oncologist");
            mSpecitlityList.add( "Plastic Surgeon");

            mList = mSpecitlityList;
        }

        else if (mSetterInt == 1){

            mCityList = new ArrayList<>();
            mCityList.add("Lagos");
            mCityList.add("Abuja");
            mCityList.add("Port Harcourt");
            mCityList.add("Ibadan");
            mCityList.add("Ado Ekiti");
            mCityList.add("Warri");
            mCityList.add("Kampala");
            mCityList.add("Entebbe");
            mCityList.add("Jinja");
            mCityList.add("Mbarara");
            mCityList.add("Gulu");
            mCityList.add("Wakiso");
            mCityList.add("Kasese");
            mCityList.add("Kazan");
            mCityList.add("Moscow");

            mList = mCityList;
        }

        else if(mSetterInt == 2){
            mDoctorsList = new ArrayList<>();
            mDoctorsList.add("Dipo Kolawole");
            mDoctorsList.add("Johnson");
            mDoctorsList.add("Daniel");
            mDoctorsList.add("Tolu");
            mDoctorsList.add("Janet");
            mDoctorsList.add("Tolulope");

            mList = mDoctorsList;
        }


        sortList(mList);

    }

    private void sortList(List<String> list){
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }
    @Override
    public void voidMethod(List<String> list, int adapterPosition) {
        Intent intent = new Intent(GetDataActivity.this, OpeningAcvtivity.class);
        if (mSetterInt == 0 ){
            mList = mSpecitlityList;
            mChosenSpeciality = mList.get(adapterPosition);
        }

        else if (mSetterInt == 1){
            mList = mCityList;
            mChosenCity = mList.get(adapterPosition);
        }

        else if(mSetterInt == 2){
            mList = mDoctorsList;
            mChosenDoctorName = mList.get(adapterPosition);
        }

        intent.putExtra("doctor_name",mChosenDoctorName);
        intent.putExtra("speciality",mChosenSpeciality);
        intent.putExtra("city",mChosenCity);
        startActivity(intent);
    }

    private void iterator(CharSequence constraint){
        final int count = 100;
        setfakeData();

        if(!TextUtils.isEmpty(constraint)){
            String constraintString = constraint.toString().toLowerCase(Locale.ROOT);

            Iterator<String> iter = mList.iterator();
            while(iter.hasNext()){
                if(!iter.next().toLowerCase(Locale.ROOT).contains(constraintString))
                {
                    iter.remove();
                }
            }
        }
        sortList(mList);
        mAdapter.swapData(mList);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}