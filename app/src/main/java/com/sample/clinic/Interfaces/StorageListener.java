package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.Buildings;

import java.util.List;

public interface StorageListener {
    void onSuccess(String data);
    void onSuccessBuilding(List<Buildings> buildingsList);
    void onError(Exception e);
    void onSuccessRetrieveNavGuide(Buildings buildings);
    void onSuccessRetrieveVideoURL(Buildings buildings);
}
