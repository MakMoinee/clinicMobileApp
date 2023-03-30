package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.NearPlacesResponse;

public interface AdapterListener {
    default void onClick(NearPlacesResponse nearPlacesResponse) {

    }

    default void onLongPress(Bookings bookings) {

    }

    default void onClick() {

    }

    default void onClick(Doctor doctor) {

    }

    default void onChatClick(int position){

    }
}
