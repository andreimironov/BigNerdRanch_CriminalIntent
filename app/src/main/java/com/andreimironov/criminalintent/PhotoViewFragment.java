package com.andreimironov.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class PhotoViewFragment extends DialogFragment {
    private static final String ARG_PHOTO_PATH = "photo path";
    private String mPhotoPath;
    private ImageView mPhotoView;
    private ConstraintLayout mParentLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo, container, false);
        Bundle args = getArguments();
        mParentLayout = view.findViewById(R.id.parent_layout);
        mPhotoPath = args.getString(ARG_PHOTO_PATH);
        mPhotoView = view.findViewById(R.id.photo_view);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoView.setImageBitmap(
                        PictureUtils.getScaledBitmap(mPhotoPath, mPhotoView.getWidth(), mPhotoView.getHeight())
                );
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return view;
    }

    public static PhotoViewFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_PATH, path);
        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
