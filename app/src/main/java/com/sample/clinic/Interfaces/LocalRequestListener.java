package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.FullNearPlacesResponse;

public interface LocalRequestListener {
    default void onSuccess() {

    }

    default void onSuccess(FullNearPlacesResponse fullNearPlacesResponse) {

    }

    void onError();
}
