package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.Doctor;
import com.squareup.picasso.Picasso;
import com.upload.adeogo.dokita.models.DoctorProfile;

import java.util.List;

/**
 * Created by Adeogo on 10/8/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private String urlFemale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_female.png?alt=media&token=eaecb6c4-5f7c-4bd6-ae8f-6a7a73e27a80";
    private String urlMale = "https://firebasestorage.googleapis.com/v0/b/dokita-c3d87.appspot.com/o/generic_photos%2Ficon_male.png?alt=media&token=e4530bf8-4f41-495d-885e-e2c73007b395";

    private List<DoctorProfile> mDoctorList = null;

    private final SearchAdapterOnclickHandler mClickHandler;


    public interface SearchAdapterOnclickHandler{
        void voidMethod(List<DoctorProfile> list, int adapterPosition);
    }

    public SearchAdapter(Context context, SearchAdapterOnclickHandler searchAdapterOnclickHandler){
        mContext = context;
        mClickHandler = searchAdapterOnclickHandler;
    }


    public class SearchAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mNameTextView;
        public final TextView mSpecialityTextView;
        public final ImageView mDoctorImageView;
        public final View mShowLineView;
        public final LinearLayout mWholeLinearLayout;


        public SearchAdapterViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.search_doctor_name);
            mSpecialityTextView = (TextView) itemView.findViewById(R.id.search_doctor_speciality);
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

            mClickHandler.voidMethod(mDoctorList, adapterPosition);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view  = layoutInflater.inflate(R.layout.list_item_search, parent, false);
        return new SearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String name = null;
        String imageUrl = null;
        String speciality = null;

        if(mDoctorList != null){
            DoctorProfile doctor = mDoctorList.get(position);
            name = doctor.getName();
            imageUrl = doctor.getPictureUrl();
            speciality = doctor.getSpeciality();
        }

        if (TextUtils.isEmpty(name)){
            ((SearchAdapterViewHolder) holder).mWholeLinearLayout.setVisibility(View.GONE);
        }

        ((SearchAdapterViewHolder) holder).mNameTextView.setText(name);
        ((SearchAdapterViewHolder) holder).mSpecialityTextView.setText(speciality);

        Picasso.with(mContext)
                .load(imageUrl)
                .resize(80, 80)
                .centerCrop()
                .into(((SearchAdapterViewHolder) holder).mDoctorImageView);
    }

    @Override
    public int getItemCount() {
        if(null == mDoctorList){
            return 0;
        }
        return mDoctorList.size();
    }

    public List<DoctorProfile> swapData(List<DoctorProfile> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mDoctorList) {
            return null; // bc nothing has changed
        }
        List<DoctorProfile> temp = mDoctorList;
        this.mDoctorList = list; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
