package com.andreimironov.criminalintent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.List;
import java.util.UUID;

public class CrimeListAdapter extends RecyclerView.Adapter<CrimeListHolder>
        implements OnViewClickedListener, ItemTouchHelperAdapter {
    private List<Crime> mCrimes;
    private Context mContext;
    private OnViewClickedListener mOnViewClickedListener;
    private OnCrimeUpdatedListener mOnCrimeUpdatedListener;

    public CrimeListAdapter(
            List<Crime> crimes,
            Context context,
            OnViewClickedListener onViewClickedListener,
            OnCrimeUpdatedListener onCrimeUpdatedListener
    ) {
        mCrimes = crimes;
        mContext = context;
        mOnViewClickedListener = onViewClickedListener;
        mOnCrimeUpdatedListener = onCrimeUpdatedListener;
    }

    @NonNull
    @Override
    public CrimeListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_crime, viewGroup, false);
        return new CrimeListHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeListHolder holder, int position) {
        Crime crime = mCrimes.get(position);
        holder.bind(crime, position);
        holder.itemView.setContentDescription(
                "The crime " + crime.getTitle() + " dated " + java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(crime.getDate()) + " has status " + (crime.isSolved() ? "solved" : "not solved")
        );
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    @Override
    public void onViewClicked(UUID id, int position) {
        mOnViewClickedListener.onViewClicked(id, position);
    }

    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

    @Override
    public void onItemDismiss(int position) {
        Crime crime = mCrimes.get(position);
        mOnCrimeUpdatedListener.onCrimeUpdated(crime.getId(), position, true);
        mCrimes.remove(position);
        notifyItemRemoved(position);
    }
}
