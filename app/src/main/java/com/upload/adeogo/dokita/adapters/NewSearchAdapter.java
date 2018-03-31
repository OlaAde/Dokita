package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.DoctorProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adeogo on 11/16/2017.
 */

public class NewSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    Context mContext;
    private List<DoctorProfile> mList = null;
    private List<DoctorProfile> mFilteredList = null;

    private final NewSearchAdapterOnclickHandler mClickHandler;




    public interface NewSearchAdapterOnclickHandler{
        void voidMethod(List<DoctorProfile> list, int adapterPosition);
    }

    public NewSearchAdapter(Context context, NewSearchAdapterOnclickHandler newSearchAdapterOnclickHandler){
        mContext = context;
        mClickHandler = newSearchAdapterOnclickHandler;
    }


    public class NewSearchAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mNameTextView;
        public final TextView mSpecialityTextView, mHomeTextView, mOnlineTextView, mOfficeTextView, mClinicTextView;
        public final ImageView mDoctorImageView;
        public final View mShowLineView;
        public final LinearLayout mWholeLinearLayout;

        public NewSearchAdapterViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.search_doctor_name);
            mSpecialityTextView = (TextView) itemView.findViewById(R.id.search_doctor_speciality);

            mHomeTextView = (TextView) itemView.findViewById(R.id.home_tv);
            mOnlineTextView = (TextView) itemView.findViewById(R.id.online_tv);
            mOfficeTextView = (TextView) itemView.findViewById(R.id.office_tv);
            mClinicTextView = (TextView) itemView.findViewById(R.id.clinic_tv);

            mDoctorImageView = (ImageView) itemView.findViewById(R.id.search_doctor_image);
            mWholeLinearLayout = itemView.findViewById(R.id.whole_search_ll);



            Typeface nameTypeface = Typeface.createFromAsset(mContext.getAssets(), "font/open_sans_semibold.ttf");
            Typeface specialityTypeface = Typeface.createFromAsset(mContext.getAssets(), "font/open_sans_light_italic.ttf");
            mNameTextView.setTypeface(nameTypeface);
            mSpecialityTextView.setTypeface(specialityTypeface);
            mShowLineView =  itemView.findViewById(R.id.show_line);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.voidMethod(mFilteredList, adapterPosition);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view  = layoutInflater.inflate(R.layout.list_item_search, parent, false);
        return new NewSearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String name = null;
        String imageUrl = null;
        String speciality = null;
        int online = 0, home = 0, office = 0, clinic = 0;

        if(mFilteredList != null){
            DoctorProfile doctor = mFilteredList.get(position);
            name = doctor.getName();
            imageUrl = doctor.getPictureUrl();
            speciality = doctor.getSpeciality();
            online = doctor.getOnlineConsult();
            office = doctor.getOfficeVisit();
            home = doctor.getHomeVisit();
            clinic = doctor.getClinic();
        }

        if (TextUtils.isEmpty(name)){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mWholeLinearLayout.setVisibility(View.GONE);
        }

        ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mNameTextView.setText(name);
        ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mSpecialityTextView.setText(speciality);

        Glide.with(mContext)
                .load(imageUrl)
                .into(((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mDoctorImageView);


        if (online == 0){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mOnlineTextView.setVisibility(View.GONE);
        }else if (online == 1){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mOnlineTextView.setVisibility(View.VISIBLE);
        }

        if (home == 0){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mHomeTextView.setVisibility(View.GONE);
        }else if (home == 1){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mHomeTextView.setVisibility(View.VISIBLE);
        }

        if (office == 0){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mOfficeTextView.setVisibility(View.GONE);
        }else if (office == 1){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mOfficeTextView.setVisibility(View.VISIBLE);
        }

        if (clinic == 0){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mClinicTextView.setVisibility(View.GONE);
        }else if (clinic == 1){
            ((NewSearchAdapter.NewSearchAdapterViewHolder) holder).mClinicTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mList;
                } else {
                    List<DoctorProfile> filteredList = new ArrayList<>();
                    for (DoctorProfile row : mList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getSpeciality().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<DoctorProfile>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        if(null == mFilteredList){
            return 0;
        }
        return mFilteredList.size();
    }

    public List<DoctorProfile> swapData(List<DoctorProfile> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mList) {
            return null; // bc nothing has changed
        }
        List<DoctorProfile> temp = mList;
        this.mList = list; // new cursor value assigned

        mFilteredList = mList;

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
