package com.andreimironov.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POSITION = "crime_position";
    public static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";
    public static final String EXTRA_DATE = "date";
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_CONTACT = 2;
    private int mPosition;
    private Crime mCrime;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mFirstButton;
    private Button mLastButton;
    private OnFirstButtonClickedListener mOnFirstButtonClickedListener;
    private OnLastButtonClickedListener mOnLastButtonClickedListener;
    private Button mReportButton;
    private Button mSuspectButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        UUID crimeId = (UUID) args.getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPosition = args.getInt(ARG_CRIME_POSITION);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnFirstButtonClickedListener = (OnFirstButtonClickedListener) context;
        mOnLastButtonClickedListener = (OnLastButtonClickedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFirstButtonClickedListener = null;
        mOnLastButtonClickedListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {
                // Здесь намеренно оставлено пустое место
            }
            @Override
            public void onTextChanged(
                CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDetailsField = view.findViewById(R.id.crime_details);
        mDetailsField.setText(mCrime.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // Здесь намеренно оставлено пустое место
            }
            @Override
            public void onTextChanged(
                CharSequence s, int start, int before, int count) {
                mCrime.setDetails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDateButton = view.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                        if (isTablet) {
                            FragmentManager manager = getFragmentManager();
                            DatePickerFragment dialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                            dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                            dialogFragment.show(manager, DIALOG_DATE);
                        }
                        else {
                            Intent intent = new Intent(getActivity(), DatePickerActivity.class);
                            intent.putExtra(EXTRA_DATE, mCrime.getDate());
                            startActivityForResult(intent, REQUEST_DATE);
                        }
                    }
                }
        );
        mTimeButton = view.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                        if (isTablet) {
                            FragmentManager manager = getFragmentManager();
                            TimePickerFragment dialogFragment = TimePickerFragment.newInstance(mCrime.getDate());
                            dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                            dialogFragment.show(manager, DIALOG_TIME);
                        }
                        else {
                            Intent intent = new Intent(getActivity(), TimePickerActivity.class);
                            intent.putExtra(EXTRA_DATE, mCrime.getDate());
                            startActivityForResult(intent, REQUEST_TIME);
                        }
                    }
                }
        );
        updateDate();
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mFirstButton = view.findViewById(R.id.first_button);
        mFirstButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnFirstButtonClickedListener.onFirstButtonClicked();
                    }
                }
        );
        mLastButton = view.findViewById(R.id.last_button);
        mLastButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnLastButtonClickedListener.onLastButtonClicked();
                    }
                }
        );
        if (mPosition == 0) {
            mFirstButton.setEnabled(false);
        }
        if (mPosition == CrimeLab.get(getActivity()).getCrimes().size() - 1) {
            mLastButton.setEnabled(false);
        }
        mReportButton = view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ShareCompat
                        .IntentBuilder
                        .from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent()
                ;
                startActivity(intent);
            }
        });
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
            return;
        }
        if (requestCode == REQUEST_TIME) {
            int hour = data.getIntExtra(TimePickerFragment.EXTRA_HOUR, -1);
            int minute = data.getIntExtra(TimePickerFragment.EXTRA_MINUTE, -1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCrime.getDate());
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);
            mCrime.setDate(calendar.getTime());
            updateDate();
            return;
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity()
                    .getContentResolver()
                    .query(contactUri, queryFields, null, null, null)
            ;
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data -
                // that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        Date date = mCrime.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        mDateButton.setText(date.toString());
        mTimeButton.setText(hour + ":" + minute);
    }

    public static CrimeFragment newInstance(UUID crimeId, int position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        args.putInt(ARG_CRIME_POSITION, position);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getCrimeReport() {
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mCrime.getDate()).toString();
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}

interface OnFirstButtonClickedListener {
    void onFirstButtonClicked();
}

interface OnLastButtonClickedListener {
    void onLastButtonClicked();
}