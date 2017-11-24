package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adeogo on 11/5/2017.
 */

public class TimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private List<Time> mList = new ArrayList<>();

    RecyclerView.ViewHolder viewHolder;

    private final TimeAdapter.TimeAdapterOnclickHandler mClickHandler;


    public interface TimeAdapterOnclickHandler{
        void voidMethod(List<Time> list, int adapterPosition);
    }

    public TimeAdapter(Context context, TimeAdapter.TimeAdapterOnclickHandler timeAdapterOnclickHandler){
        mContext = context;
        mClickHandler = timeAdapterOnclickHandler;
    }


    public class TimeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mTimeTextView;

        public TimeAdapterViewHolder(View itemView) {
            super(itemView);
            mTimeTextView = (TextView) itemView.findViewById(R.id.timeTV);

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
        View view  = layoutInflater.inflate(R.layout.list_time, parent, false);
        return new TimeAdapter.TimeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String Title = null;
        int Picked = 1;
        if(mList != null){
            Time time = mList.get(position);
            Title = time.getTime();
            Picked = time.getPicked();
        }

        viewHolder = holder;

        ((TimeAdapterViewHolder) holder).mTimeTextView.setText(Title);
        if (Picked == 0){
            ((TimeAdapterViewHolder) holder).mTimeTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        }else if (Picked == 1){
            ((TimeAdapterViewHolder) holder).mTimeTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        if(null == mList){
            return 0;
        }
        return mList.size();
    }


    public List<Time> swapData(List<Time> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mList) {
            return null; // bc nothing has changed
        }
        List<Time> temp = mList;
        this.mList = list; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}