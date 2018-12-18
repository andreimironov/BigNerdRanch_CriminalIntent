package com.andreimironov.criminalintent;

import java.util.UUID;

interface OnCrimeUpdatedListener {
    void onCrimeUpdated(UUID id, int position, boolean wasDeleted);
}