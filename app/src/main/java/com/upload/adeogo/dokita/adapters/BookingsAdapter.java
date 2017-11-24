package com.upload.adeogo.dokita.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.Appointment;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Adeogo on 11/5/2017.
 */

public class BookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private List<Appointment> mList = null;

    private final BookingsAdapter.BookingsAdapterOnclickHandler mClickHandler;


    public interface BookingsAdapterOnclickHandler{
        void voidMethodBooking(List<Appointment> list, int adapterPosition);
        void cancelAppointment(List<Appointment> list, int adapterPosition);
    }

    public BookingsAdapter(Context context, BookingsAdapter.BookingsAdapterOnclickHandler listAdapterOnclickHandler){
        mContext = context;
        mClickHandler = listAdapterOnclickHandler;
    }

    public class BookingsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mNameTextView;
        public final TextView mDateTextView;
        public final TextView mLocationTextView;
        public final LinearLayout mCancelLinearLayout;

        public BookingsAdapterViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.card_DoctorName);
            mDateTextView = (TextView) itemView.findViewById(R.id.card_date_tv);
            mLocationTextView = (TextView) itemView.findViewById(R.id.card_DoctorPlace);
            mCancelLinearLayout = (LinearLayout) itemView.findViewById(R.id.cancel_ll);

            mCancelLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    mClickHandler.cancelAppointment(mList, adapterPosition);
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.voidMethodBooking(mList, adapterPosition);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view  = layoutInflater.inflate(R.layout.list_business_card, parent, false);
        return new BookingsAdapter.BookingsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String Name = null;
        String Location = null;
        String Date = null;

        if(mList != null){
            Appointment appointment = mList.get(position);

            Name = appointment.getDoctorName();
            Location = appointment.getLocation();

            Calendar calendar = Calendar.getInstance();

            int thisYear = calendar.get(Calendar.YEAR);

            int thisMonth = calendar.get(Calendar.MONTH);

            int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

            if (appointment.getDay() == thisDay && appointment.getMonth() == thisMonth && appointment.getYear() == thisYear){
                Date = "Today, " + appointment.getTime();
            }else if (appointment.getDay() == thisDay + 1 && appointment.getMonth() == thisMonth + 1 && appointment.getYear() == thisYear + 1){
                Date = "Tomorrow, " + appointment.getTime();
            }else
                Date = appointment.getTime() + " on " + appointment.getDay() + "/" + appointment.getMonth() + "/" + appointment.getYear();
        }

        ((BookingsAdapterViewHolder) holder).mNameTextView.setText(Name);
        ((BookingsAdapterViewHolder) holder).mLocationTextView.setText(Location);
        ((BookingsAdapterViewHolder) holder).mDateTextView.setText(Date);
    }


    @Override
    public int getItemCount() {
        if(null == mList){
            return 0;
        }
        return mList.size();
    }


    public List<Appointment> swapData(List<Appointment> list) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (list == mList) {
            return null; // bc nothing has changed
        }
        List<Appointment> temp = mList;
        this.mList = list; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (list != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


}
