package com.andreimironov.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private int mSuspectId;
    private boolean mHasPhoneNumber;
    private String mPhoneNumber;


    public int getSuspectId() {
        return mSuspectId;
    }

    public void setSuspectId(int suspectId) {
        mSuspectId = suspectId;
    }

    public boolean isHasPhoneNumber() {
        return mHasPhoneNumber;
    }

    public void setHasPhoneNumber(boolean hasPhoneNumber) {
        mHasPhoneNumber = hasPhoneNumber;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
