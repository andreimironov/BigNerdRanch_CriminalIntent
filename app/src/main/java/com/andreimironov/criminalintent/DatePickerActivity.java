package com.andreimironov.criminalintent;

import android.content.Intent;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Intent data = getIntent();
        Date date = (Date) data.getSerializableExtra(CrimeFragment.EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }
}
