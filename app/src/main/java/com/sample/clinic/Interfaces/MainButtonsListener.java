package com.sample.clinic.Interfaces;

public interface MainButtonsListener {
    void onProfileClick();
    void onNavClick();

    void onBookingClick();

    void onHomeClick();

    void onConsultClick();

    default void onLongItemClickBooking(){

    }

    default void onItemClickBooking(){

    }
}
