package com.sample.clinic.Interfaces;

import com.google.firebase.firestore.DocumentReference;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Users;

import java.util.List;

public interface FireStoreListener {
    default void onAddUserSuccess(Users users) {

    }

    default void onAddUserError(Exception e) {

    }

    default void onSuccess() {

    }

    default void onSuccess(List<Bookings> bookingsList) {

    }

    default void onError() {

    }

}
