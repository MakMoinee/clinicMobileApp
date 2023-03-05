package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.NearPlacesResponse;

public interface AdapterListener {
    default void onClick(NearPlacesResponse nearPlacesResponse) {

    }
}
