package com.andreimironov.criminalintent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.UUID;

public class CrimeListAdapter extends RecyclerView.Adapter<CrimeListHolder> implements OnViewClickedListener {
    private List<Crime> mCrimes;
    private LayoutInflater mLayoutInflater;
    private OnViewClickedListener mOnViewClickedListener;

    public CrimeListAdapter(List<Crime> crimes, LayoutInflater inflater, OnViewClickedListener onViewClickedListener) {
        mCrimes = crimes;
        mLayoutInflater = inflater;
        mOnViewClickedListener = onViewClickedListener;
    }

    @NonNull
    @Override
    public CrimeListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.list_item_crime, viewGroup, false);
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
}
