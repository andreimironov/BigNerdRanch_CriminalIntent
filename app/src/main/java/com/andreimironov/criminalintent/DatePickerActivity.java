package com.andreimironov.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Intent data = getIntent();
        Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }
}