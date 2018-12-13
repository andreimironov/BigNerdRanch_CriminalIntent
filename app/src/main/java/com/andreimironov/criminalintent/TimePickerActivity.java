package com.andreimironov.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Intent data = getIntent();
        Date date = (Date) data.getSerializableExtra(CrimeFragment.EXTRA_DATE);
        return TimePickerFragment.newInstance(date);
    }
}
