package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.ChatHead;

import java.util.List;

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

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.title_tv);

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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String Title = null;

        if(mList != null){
            ChatHead chatHead = mList.get(position);
            Title = chatHead.getUserName();
        }

        ((ListAdapterViewHolder) holder).mNameTextView.setText(Title);
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
