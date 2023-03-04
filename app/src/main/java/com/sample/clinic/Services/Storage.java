package com.sample.clinic.Services;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sample.clinic.Common.Constants;
import com.sample.clinic.Interfaces.StorageListener;
import com.sample.clinic.Models.Buildings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Storage {

    private FirebaseStorage fs;
    private StorageListener listener;

    public Storage(StorageListener listener1) {
        fs = FirebaseStorage.getInstance(Constants.storageBucket);
        listener = listener1;
    }

    public void getBuildingsPosterByPath(String key) {
        StorageReference listSF = fs.getReference().child(Constants.postersPath + key);
        List<Buildings> buildingsList = new ArrayList<>();
        Map<Integer, Buildings> newSet = new HashMap<>();
        listSF.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            Log.e("ITEM_NAME", item.getName().toString());
                            Buildings buildings = new Buildings();
                            item.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (!uri.toString().equals("")) {

                                                if (key.equals(item.getName())) {
                                                    buildings.setBuildingName(item.getName());
                                                    buildings.setPicturePath(uri.toString());
                                                    buildingsList.add(buildings);
                                                }

                                                if (buildingsList.size() == listResult.getItems().size()) {
                                                    listener.onSuccessBuilding(buildingsList);
                                                }
                                            } else {
                                                listener.onError(new Exception("Empty"));
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e);
                    }
                });
    }

    public void getBuildingsPoster() {
        StorageReference listSF = fs.getReference().child(Constants.postersPath);
        List<Buildings> buildingsList = new ArrayList<>();
        Map<Integer, Buildings> newSet = new HashMap<>();
        listSF.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            Log.e("ITEM_NAME", item.getName().toString());
                            Buildings buildings = new Buildings();
                            item.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (!uri.toString().equals("")) {
                                                buildings.setBuildingName(item.getName());
                                                buildings.setPicturePath(uri.toString());
                                                buildingsList.add(buildings);

                                                if (buildingsList.size() == listResult.getItems().size()) {
                                                    listener.onSuccessBuilding(buildingsList);
                                                }
                                            } else {
                                                listener.onError(new Exception("Empty"));
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e);
                    }
                });
    }

    public void getBuildingsFromStorage(String filePath) {

        StorageReference listSF = fs.getReference().child(Constants.buildingPath + filePath + "/");
        List<Buildings> buildingsList = new ArrayList<>();
        Map<Integer, Buildings> newSet = new HashMap<>();
        listSF.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            Buildings buildings = new Buildings();
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Do whatever you need here.
                                    if (!uri.toString().equals("")) {
                                        Log.w("URL", "downloadUrl:" + uri.toString());
                                        if (uri.toString().contains("1st") && uri.toString().contains("Floor") || uri.toString().contains("Ground") && uri.toString().contains("Floor")) {
                                            buildings.setBuildingName(filePath);
                                            buildings.setPicturePath(uri.toString());
                                            newSet.put(0, buildings);
                                        }
                                        if (uri.toString().contains("2nd") && uri.toString().contains("Floor")) {
                                            buildings.setBuildingName(filePath);
                                            buildings.setPicturePath(uri.toString());
                                            newSet.put(1, buildings);
                                        }
                                        if (uri.toString().contains("3rd") && uri.toString().contains("Floor")) {
                                            buildings.setBuildingName(filePath);
                                            buildings.setPicturePath(uri.toString());
                                            newSet.put(2, buildings);
                                        }
                                        if (uri.toString().contains("4th") && uri.toString().contains("Floor")) {
                                            buildings.setBuildingName(filePath);
                                            buildings.setPicturePath(uri.toString());
                                            newSet.put(3, buildings);
                                        }
                                        if (uri.toString().contains("5th") && uri.toString().contains("Floor")) {
                                            buildings.setBuildingName(filePath);
                                            buildings.setPicturePath(uri.toString());
                                            newSet.put(4, buildings);
                                        }

                                        // setup the sequence properly
                                        if (newSet.size() == listResult.getItems().size()) {
                                            int currentIndex = 1;
                                            while (currentIndex <= newSet.size()) {
                                                buildingsList.add(newSet.get(currentIndex - 1));
                                                currentIndex++;
                                            }
                                        }

                                        if (listResult.getItems().size() != 0 && buildingsList.size() == listResult.getItems().size()) {
                                            listener.onSuccessBuilding(buildingsList);
                                        }
                                    } else {
                                        listener.onError(new Exception("There are no buildings saved in database"));
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    listener.onError(exception);
                                }
                            });
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e);
                    }
                });

    }

    public void getBuildingNavGuideByPath(String fullPath) {
        Log.e("FULLPATH", Constants.buildingPath + fullPath);
        StorageReference sf = fs.getReference().child(Constants.buildingPath + fullPath);
        sf.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!uri.toString().equals("")) {
                            Buildings b = new Buildings();
                            b.setBuildingName(sf.getName());
                            b.setPicturePath(uri.toString());
                            listener.onSuccessRetrieveNavGuide(b);

                        } else {
                            listener.onError(new Exception("Empty URI"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void getBuildingVideoPath(String fullPath) {
        Log.e("FULLPATH_VIDEO", Constants.virtualGuidePath + fullPath);
        StorageReference sf = fs.getReference().child(Constants.virtualGuidePath + fullPath);
        sf.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!uri.toString().equals("")) {
                            Buildings b = new Buildings();
                            b.setBuildingName(sf.getName());
                            b.setVideoPath(uri.toString());
                            listener.onSuccessRetrieveVideoURL(b);

                        } else {
                            listener.onError(new Exception("Empty URI"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e);
                    }
                });
    }
}
