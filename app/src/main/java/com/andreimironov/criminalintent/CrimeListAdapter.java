package com.andreimironov.criminalintent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.UUID;

public class CrimeListAdapter extends RecyclerView.Adapter<CrimeListHolder> implements OnViewClickedListener {
    private List<Crime> mCrimes;
    private Context mContext;
    private OnViewClickedListener mOnViewClickedListener;

    public CrimeListAdapter(List<Crime> crimes, Context context, OnViewClickedListener onViewClickedListener) {
        mCrimes = crimes;
        mContext = context;
        mOnViewClickedListener = onViewClickedListener;
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
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    @Override
    public void onViewClicked(UUID id) {
        mOnViewClickedListener.onViewClicked(id);
    }

    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

}
