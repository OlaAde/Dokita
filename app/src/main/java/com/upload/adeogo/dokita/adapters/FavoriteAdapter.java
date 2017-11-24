package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.Favorite;

import java.util.List;

/**
 * Created by Adeogo on 11/5/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private List<Favorite> mList = null;
    private List<String> mTesterList = null;

    private final FavoriteAdapter.FavoriteAdapterOnclickHandler mClickHandler;


    public interface FavoriteAdapterOnclickHandler{
        void voidMethod(List<Favorite> list, int adapterPosition);
    }

    public FavoriteAdapter(Context context, FavoriteAdapter.FavoriteAdapterOnclickHandler listAdapterOnclickHandler){
        mContext = context;
        mClickHandler = listAdapterOnclickHandler;
    }


    public class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        public final TextView mNameTextView;

        public FavoriteAdapterViewHolder(View itemView) {
            super(itemView);
//            mNameTextView = (TextView) itemView.findViewById(R.id.title_tv);

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
        View view  = layoutInflater.inflate(R.layout.list_item_favourite, parent, false);
        return new FavoriteAdapter.FavoriteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String Title = null;
    }


    @Override
    public int getItemCount() {
        if(null == mList){
            return 0;
        }
        return mList.size();
    }


    public List<Favorite> swapData(List<Favorite> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mList) {
            return null; // bc nothing has changed
        }
        List<Favorite> temp = mList;
        this.mList = list; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


}
