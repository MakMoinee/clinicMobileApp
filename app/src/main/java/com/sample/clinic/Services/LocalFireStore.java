package com.sample.clinic.Services;

import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Users;

public interface LocalFireStore {

    void insertUserRecord(Users users, FireStoreListener listener);

    void updateUserRecord(Users users, FireStoreListener listener);

    void getLogin(Users users, FireStoreListener listener);

    void storeBuildings(Buildings buildings, FireStoreListener listener);

    void forgotPassword(Users users,FireStoreListener listener);
}
