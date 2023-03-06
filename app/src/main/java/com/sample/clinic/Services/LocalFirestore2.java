package com.sample.clinic.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Bookings;
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

    public void addBooking(Bookings bookings, FireStoreListener listener) {
        bookings.setStatus("Active");
        db.collection("bookings")
                .document()
                .set(null)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_ADD_BOOKING", e.getMessage());
                    listener.onError();
                });
    }
}
