package com.andreimironov.criminalintent;

import android.content.Intent;

import java.util.List;
import java.util.UUID;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements
            OnViewClickedListener,
            OnFirstButtonClickedListener,
            OnLastButtonClickedListener,
            OnCrimeUpdatedListener
{
    private static final int REQUEST_CRIME = 1;

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onViewClicked(UUID id, int position) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, id, position);
            startActivityForResult(intent, REQUEST_CRIME);
        }
        else {
            Fragment newDetail = CrimeFragment.newInstance(id, position);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit()
            ;
        }
    }

    @Override
    public void onFirstButtonClicked() {
        List<Crime> crimes = CrimeLab.get(this).getCrimes();
        int position = 0;
        UUID id = crimes.get(position).getId();
        Fragment newDetail = CrimeFragment.newInstance(id, position);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        ;
    }

    @Override
    public void onLastButtonClicked() {
        List<Crime> crimes = CrimeLab.get(this).getCrimes();
        int position = crimes.size() - 1;
        UUID id = crimes.get(position).getId();
        Fragment newDetail = CrimeFragment.newInstance(id, position);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        ;
    }

    @Override
    public void onCrimeUpdated(UUID id, int position, boolean wasDeleted) {
        CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.updateUI();
    }
}
