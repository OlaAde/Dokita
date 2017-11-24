package com.upload.adeogo.dokita;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private LinearLayout mBodyMeasurementsLinearLayout;
    private LinearLayout mResultsLinearLayout;
    private LinearLayout mHeartLinearLayout;
    private LinearLayout mReproductiveHealthLinearLayout;
    private LinearLayout mVitalsLinearLayout;
    private LinearLayout mHealthRecordsLinearLayout;

//    private SearchView mSearchView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
//        mSearchView = (SearchView) rootView.findViewById(R.id.signed_search_view);

        mBodyMeasurementsLinearLayout = (LinearLayout) rootView.findViewById(R.id.body_measurements_ll);
        mVitalsLinearLayout = (LinearLayout) rootView.findViewById(R.id.vitals_ll);
        mResultsLinearLayout = (LinearLayout) rootView.findViewById(R.id.results_ll);

        mBodyMeasurementsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BodyMeasurementActivity.class);
                startActivity(intent);
            }
        });


        mVitalsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VitalsActivity.class);
                startActivity(intent);
            }
        });

        mResultsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResultsActivity.class);
                startActivity(intent);
            }
        });

//        mSearchView.setIconified(false);
//
//        mSearchView.clearFocus();
        return rootView;
    }

}
