package com.upload.adeogo.dokita.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.ChatHead;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Adeogo on 11/16/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private List<ChatHead> mList = null;

    private final ListAdapterOnclickHandler mClickHandler;


    public interface ListAdapterOnclickHandler{
        void voidMethod(List<ChatHead> list, int adapterPosition);
    }

    public ListAdapter(Context context, ListAdapterOnclickHandler listAdapterOnclickHandler){
        mContext = context;
        mClickHandler = listAdapterOnclickHandler;
    }


    public class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mNameTextView;
        public final TextView mDateTextView;
        public final CircleImageView mImageView;

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.title_tv);
            mDateTextView = (TextView) itemView.findViewById(R.id.online_status_tv);
            mImageView = (CircleImageView) itemView.findViewById(R.id.profileImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.voidMethod(mList, adapterPosition);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view  = layoutInflater.inflate(R.layout.layout_list, parent, false);
        return new ListAdapterViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String Title = null;
        String PictureUrl = null;
        long unixTime = 0;


        if(mList != null){
            ChatHead chatHead = mList.get(position);
            Title = chatHead.getUserName();
            PictureUrl = chatHead.getPictureUrl();
            if (TextUtils.isEmpty(Title) || TextUtils.equals("", Title)){
                Title = "Anonymous";
            }

            if (PictureUrl!= null){
                Glide.with(mContext)
                        .load(PictureUrl)
                        .into(((ListAdapterViewHolder) holder).mImageView);
            }else {
                ((ListAdapterViewHolder) holder).mImageView.setImageDrawable(mContext.getDrawable(R.drawable.profile_black));
            }

            unixTime = chatHead.getUnixTime();
            Calendar c= Calendar.getInstance();
            c.getTimeInMillis();
            String cur_day=String.format("%te %B %tY",c,c,c); // This will give date like 22 February 2012

            c.setTimeInMillis(unixTime);//set your saved timestamp
            String that_day=String.format("%te %B %tY",c,c,c); //this will convert timestamp into format like 22 February 2012
            DateFormat df;

            if (TextUtils.equals(cur_day, that_day)){
               df = new SimpleDateFormat("HH:mm:ss");
            }else {
                df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            }

            java.util.Date time = new java.util.Date((long)unixTime);
            String shownDate = df.format(time);
            ((ListAdapterViewHolder) holder).mDateTextView.setText(shownDate);

            ((ListAdapterViewHolder) holder).mNameTextView.setText(Title);
        }


    }


    @Override
    public int getItemCount() {
        if(null == mList){
            return 0;
        }
        return mList.size();
    }


    public List<ChatHead> swapData(List<ChatHead> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mList) {
            return null; // bc nothing has changed
        }
        List<ChatHead> temp = mList;
        this.mList = list; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


}
