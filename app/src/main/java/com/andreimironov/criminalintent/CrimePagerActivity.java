package com.andreimironov.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity
        extends AppCompatActivity
        implements
            OnFirstButtonClickedListener,
            OnLastButtonClickedListener,
            OnCrimeUpdatedListener
{
    private static final String EXTRA_CRIME_ID = "crime_id";
    private static final String EXTRA_CRIME_POSITION = "crime_position";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mViewPager = findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId(), position);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = getIntent().getIntExtra(EXTRA_CRIME_POSITION, -1);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onFirstButtonClicked() {
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onLastButtonClicked() {
        mViewPager.setCurrentItem(mCrimes.size() - 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CrimeFragment.REQUEST_READ_CONTACTS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Crime crime = mCrimes.get(mViewPager.getCurrentItem());
                    CrimeFragment.readSuspectPhone(this, crime);
                    CrimeFragment.dialSuspect(this, crime);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default:
                return;
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeId, int position) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(EXTRA_CRIME_POSITION, position);
        return intent;
    }

    @Override
    public void onCrimeUpdated(UUID id, int position, boolean wasDeleted) {
        if (wasDeleted) {
            finish();
        }
        else {

        }
    }
}
