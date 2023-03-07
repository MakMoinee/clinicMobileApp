package com.sample.clinic.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sample.clinic.Common.Common;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.NearPlacesRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> fMap = Common.getBookMap(bookings);
        db.collection("bookings")
                .document()
                .set(fMap)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_ADD_BOOKING", e.getMessage());
                    listener.onError();
                });
    }

    public void getAllBookings(String userID, FireStoreListener listener) {
        List<Bookings> bookingsList = new ArrayList<>();
        db.collection("bookings")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Bookings bookings = documentSnapshot.toObject(Bookings.class);
                                bookings.setDocID(documentSnapshot.getId());
                                bookingsList.add(bookings);
                            }
                        }
                        listener.onSuccess(bookingsList);
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void deleteBooking(String docID, FireStoreListener listener){
        db.collection("bookings")
                .document(docID)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }
}
