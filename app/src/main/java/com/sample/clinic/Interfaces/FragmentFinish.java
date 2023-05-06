package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.Buildings;

public interface FragmentFinish {
    void onFinishFirstFragment();
    void onFinishSecondFragment();
    void onLoginFinish();
    void openBuildingFragment(Buildings buildings);
    void openProfileFragment();

    void onAdminFragment();

    void onDoctorLogin();

    void finishMain();

    void reloadNearbyFragment();
}
