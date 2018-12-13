package com.andreimironov.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment implements OnViewClickedListener {
    private static final int REQUEST_CRIME = 1;
    private static final String KEY_SUBTITLE_VISIBLE = "subtitle visible";
    private RecyclerView mCrimeRecyclerView;
    private CrimeListAdapter mCrimeAdapter;
    private boolean mSubtitleVisible;
    private TextView mNoCrimesLabel;
    private Button mAddCrimeButton;

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
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        mCrimeAdapter = new CrimeListAdapter(crimes, inflater, this);
        mCrimeRecyclerView.setAdapter(mCrimeAdapter);
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
        updateSubtitle();
        updateNoCrimeInfoViews();
        return view;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean wasChanged = data.getBooleanExtra(CrimeFragment.KEY_WAS_CHANGED, false);
            boolean wasDeleted = data.getBooleanExtra(CrimeFragment.KEY_WAS_DELETED, false);
            if (wasChanged) {
                int position = data.getIntExtra(CrimeFragment.KEY_POSITION, -1);
                mCrimeAdapter.notifyItemChanged(position);
            }
            if (wasDeleted) {
                int position = data.getIntExtra(CrimeFragment.KEY_POSITION, -1);
                mCrimeAdapter.notifyItemRemoved(position);
                updateSubtitle();
                updateNoCrimeInfoViews();
            }
        }
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

    private void addCrime() {
        Crime crime = new Crime();
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimeLab.addCrime(crime);
        mCrimeAdapter.notifyItemInserted(crimeLab.getCrimes().size() - 1);
        updateSubtitle();
        updateNoCrimeInfoViews();
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivityForResult(intent, REQUEST_CRIME);
    }

    @Override
    public void onViewClicked(UUID id) {
        Intent intent = CrimePagerActivity.newIntent(getActivity(), id);
        startActivityForResult(intent, REQUEST_CRIME);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, mSubtitleVisible);
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
}