package com.andreimironov.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment implements OnViewClickedListener, OnCrimeUpdatedListener {
    private static final String KEY_SUBTITLE_VISIBLE = "subtitle visible";
    private RecyclerView mCrimeRecyclerView;
    private CrimeListAdapter mCrimeAdapter;
    private boolean mSubtitleVisible;
    private TextView mNoCrimesLabel;
    private Button mAddCrimeButton;
    private OnViewClickedListener mOnViewClickedListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mOnViewClickedListener = (OnViewClickedListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }
        mNoCrimesLabel = view.findViewById(R.id.no_crimes_label);
        mAddCrimeButton = view.findViewById(R.id.add_crime_button);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCrime();
            }
        });
        updateUI();
        ItemTouchHelper.Callback callback = new CrimeItemTouchHelperCallback(mCrimeAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mCrimeRecyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                addCrime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnViewClickedListener = null;
    }

    @Override
    public void onViewClicked(UUID id, int position) {
        mOnViewClickedListener.onViewClicked(id, position);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeListAdapter(crimes, getContext(), this, this);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.setCrimes(crimes);
            mCrimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
        updateNoCrimeInfoViews();
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateNoCrimeInfoViews() {
        int count = CrimeLab.get(getActivity()).getCrimes().size();
        if (count == 0) {
            mNoCrimesLabel.setVisibility(View.VISIBLE);
            mAddCrimeButton.setVisibility(View.VISIBLE);
        }
        else {
            mNoCrimesLabel.setVisibility(View.INVISIBLE);
            mAddCrimeButton.setVisibility(View.INVISIBLE);
        }
    }

    private void addCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        updateUI();
        mOnViewClickedListener.onViewClicked(crime.getId(), mCrimeAdapter.getItemCount() - 1);
    }

    @Override
    public void onCrimeUpdated(UUID id, int position, boolean wasDeleted) {
        if (wasDeleted) {
            CrimeLab.get(getActivity()).deleteCrime(id);
        }
    }
}