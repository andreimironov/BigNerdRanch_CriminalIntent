package com.andreimironov.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POSITION = "crime_position";
    public static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "Dialog date";
    public static final String EXTRA_DATE = "date";
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "Dialog time";
    private static final int REQUEST_CONTACT = 2;
    public static final int REQUEST_READ_CONTACTS_PERMISSION = 666;
    private static final int REQUEST_PHOTO= 3;
    private static final String DIALOG_PHOTO = "Dialog photo";
    private int mPosition;
    private Crime mCrime;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mFirstButton;
    private Button mLastButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mDialButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private PackageManager mPackageManager;
    private FragmentManager mFragmentManager;
    private OnFirstButtonClickedListener mOnFirstButtonClickedListener;
    private OnLastButtonClickedListener mOnLastButtonClickedListener;
    private OnCrimeUpdatedListener mOnCrimeUpdatedListener;
    private OnPhotoUpdatedListener mOnPhotoUpdatedListener;

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
        mOnCrimeUpdatedListener = (OnCrimeUpdatedListener) context;
        mOnPhotoUpdatedListener = (OnPhotoUpdatedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFirstButtonClickedListener = null;
        mOnLastButtonClickedListener = null;
        mOnCrimeUpdatedListener = null;
        mOnPhotoUpdatedListener = null;
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
                mOnCrimeUpdatedListener.onCrimeUpdated(mCrime.getId(), mPosition, true);
                return true;
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentManager = getFragmentManager();
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
                CrimeLab.get(getActivity()).updateCrime(mCrime);
                mOnCrimeUpdatedListener.onCrimeUpdated(mCrime.getId(), mPosition, false);
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
                CrimeLab.get(getActivity()).updateCrime(mCrime);
                mOnCrimeUpdatedListener.onCrimeUpdated(mCrime.getId(), mPosition, false);
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
                            DatePickerFragment dialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                            dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                            dialogFragment.show(mFragmentManager, DIALOG_DATE);
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
                            TimePickerFragment dialogFragment = TimePickerFragment.newInstance(mCrime.getDate());
                            dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                            dialogFragment.show(mFragmentManager, DIALOG_TIME);
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
        mPackageManager = getActivity().getPackageManager();
        if (mPackageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        mDialButton = view.findViewById(R.id.dial_suspect);
        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCrime.isHasPhoneNumber()) {
                    if (mCrime.getPhoneNumber() == null) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            readSuspectPhone(getActivity(), mCrime);
                            dialSuspect(getActivity(), mCrime);
                        }
                        else {
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{ Manifest.permission.READ_CONTACTS },
                                    REQUEST_READ_CONTACTS_PERMISSION
                            );
                        }
                    }
                    else {
                        dialSuspect(getActivity(), mCrime);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "This contact has no phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPhotoButton = view.findViewById(R.id.crime_camera);
        mPhotoView = view.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewFragment dialogFragment = PhotoViewFragment.newInstance(mPhotoFile.getPath());
                dialogFragment.show(mFragmentManager, DIALOG_PHOTO);
            }
        });
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(mPackageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile
                );
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = mPackageManager.queryIntentActivities(
                        captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY
                );
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();
        return view;
    }

    public static void dialSuspect(Activity activity, Crime crime) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(crime.getPhoneNumber()));
        activity.startActivity(intent);
    }

    public static void readSuspectPhone(Activity activity, Crime crime) {
        Cursor phones = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ crime.getSuspectId(),
                null,
                null
        );
        phones.moveToFirst();
        crime.setPhoneNumber(
            "tel:" + phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        );
    }

    private void updatePhotoView() {
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    mPhotoView.setImageDrawable(null);
                    mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
                } else {
                    Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
                    mPhotoView.setImageBitmap(bitmap);
                    mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            CrimeLab.get(getActivity()).updateCrime(mCrime);
            mOnCrimeUpdatedListener.onCrimeUpdated(mCrime.getId(), mPosition, false);
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
            CrimeLab.get(getActivity()).updateCrime(mCrime);
            mOnCrimeUpdatedListener.onCrimeUpdated(mCrime.getId(), mPosition, false);
            updateDate();
            return;
        }
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] projection = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };
            Cursor contentCursor = getActivity()
                    .getContentResolver()
                    .query(contactUri, projection, null, null, null)
            ;
            try {
                if (contentCursor.moveToFirst()){
                    String displayName = contentCursor.getString(
                            contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                    );
                    int id = contentCursor.getInt(
                            contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                    );
                    String hasPhoneNumber = contentCursor.getString(
                            contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    );
                    mCrime.setSuspect(displayName);
                    mCrime.setSuspectId(id);
                    mCrime.setHasPhoneNumber(hasPhoneNumber.equalsIgnoreCase("1"));
                    mSuspectButton.setText(displayName);
                }
            }
            finally {
                contentCursor.close();
                return;
            }
        }
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(
                    getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile
            );
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mOnPhotoUpdatedListener.onPhotoUpdated(mPhotoView);
            updatePhotoView();
        }

    }

    private void updateDate() {
        Date date = mCrime.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        String dateLocale = java.text.DateFormat
                .getDateTimeInstance(java.text.DateFormat.LONG, java.text.DateFormat.LONG)
                .format(date)
        ;
        mDateButton.setText(dateLocale);
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

interface OnPhotoUpdatedListener {
    void onPhotoUpdated(View view);
}