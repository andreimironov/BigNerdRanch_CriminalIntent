package com.andreimironov.criminalintent;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

public class CrimeListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected int position;
    protected Crime mCrime;
    protected TextView mTitleTextView;
    protected TextView mDateTextView;
    protected ImageView mSolvedImageView;
    private OnViewClickedListener mOnViewClickedListener;

    public CrimeListHolder(View view, OnViewClickedListener onViewClickedListener) {
        super(view);
        mOnViewClickedListener = onViewClickedListener;
        mTitleTextView = itemView.findViewById(R.id.crime_title);
        mDateTextView = itemView.findViewById(R.id.crime_date);
        itemView.setOnClickListener(this);
        mSolvedImageView = itemView.findViewById(R.id.crime_solved);
    }

    public void bind(Crime crime, int position) {
        this.position = position;
        mCrime = crime;
        mTitleTextView.setText(mCrime.getTitle());
        mDateTextView.setText(
                android.text.format.DateFormat.format("EEEE, dd MMMM yyyy", mCrime.getDate()).toString()
        );
        mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        mOnViewClickedListener.onViewClicked(mCrime.getId());
    }
}

interface OnViewClickedListener {
    void onViewClicked(UUID id);
}
