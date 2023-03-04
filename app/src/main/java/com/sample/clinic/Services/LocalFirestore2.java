package com.sample.clinic.Services;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.NearPlacesRequest;

public class LocalFirestore2 {

    Context mContext;
    FirebaseFirestore db;

    public LocalFirestore2(Context mContext) {
        this.mContext = mContext;
        db = FirebaseFirestore.getInstance();
    }


    public void getNearbyHospitals(NearPlacesRequest req, FireStoreListener listener) {

    }
}
