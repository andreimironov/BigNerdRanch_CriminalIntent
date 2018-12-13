package com.andreimironov.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POSITION = "crime_position";
    public static final int REQUEST_DATE = 0;
    public static final String KEY_WAS_CHANGED = "was changed";
    public static final String KEY_POSITION = "position";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String EXTRA_DATE = "date";
    private int mPosition;
    private Crime mCrime;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mFirstButton;
    private Button mLastButton;
    private OnFirstButtonClickedListener mOnFirstButtonClickedListener;
    private OnLastButtonClickedListener mOnLastButtonClickedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        UUID crimeId = (UUID) args.getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPosition = args.getInt(ARG_CRIME_POSITION);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = v.findViewById(R.id.crime_title);
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
                returnResult(true);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDetailsField = v.findViewById(R.id.crime_details);
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
                returnResult(true);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
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
                            startActivityForResult(intent, CrimeFragment.REQUEST_DATE);
                        }
                    }
                }
        );
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mCrime.setSolved(isChecked);
                returnResult(true);
            }
        });
        mFirstButton = v.findViewById(R.id.first_button);
        mFirstButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnFirstButtonClickedListener.onFirstButtonClicked();
                    }
                }
        );
        mLastButton = v.findViewById(R.id.last_button);
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
        return v;
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
            returnResult(true);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void returnResult(boolean wasChanged) {
        Intent intent = new Intent();
        intent.putExtra(KEY_WAS_CHANGED, wasChanged);
        intent.putExtra(KEY_POSITION, mPosition);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    public static CrimeFragment newInstance(UUID crimeId, int position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        args.putInt(ARG_CRIME_POSITION, position);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

interface OnFirstButtonClickedListener {
    void onFirstButtonClicked();
}

interface OnLastButtonClickedListener {
    void onLastButtonClicked();
}