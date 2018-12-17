package com.andreimironov.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PhotoViewFragment extends DialogFragment {
    private static final String ARG_PHOTO_PATH = "photo path";
    private String mPhotoPath;
    private ImageView mPhotoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo, container, false);
        Bundle args = getArguments();
        mPhotoPath = args.getString(ARG_PHOTO_PATH);
        mPhotoView = view.findViewById(R.id.photo_view);
        mPhotoView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoPath, getActivity()));
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
