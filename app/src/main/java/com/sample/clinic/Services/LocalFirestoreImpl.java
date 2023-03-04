package com.sample.clinic.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sample.clinic.Common.Common;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Users;

import java.util.Map;

public class LocalFirestoreImpl implements LocalFireStore {

    private Context mContext;
    private FirebaseFirestore db;

    public LocalFirestoreImpl(Context context) {
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void updateUserRecord(Users users, FireStoreListener listener) {
        Map<String, Object> mapToInsert = Common.toLoginMaps(users);
        db.collection("user")
                .document(users.getDocID())
                .set(mapToInsert, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onAddUserSuccess(users);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("UPDATE_USER_RECORD", e.getMessage());
                    }
                });
    }

    @Override
    public void insertUserRecord(Users users, FireStoreListener listener) {
        LocalHash hash = new LocalHash();
        String hashPass = hash.makeHashPassword(users.getPassword());
        users.setPassword(hashPass);
        Map<String, Object> mapToInsert = Common.toLoginMaps(users);
        db.collection("user")
                .document()
                .set(mapToInsert)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onAddUserSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("INSERT_USER_RECORD", e.getMessage());
                    }
                });
    }

    @Override
    public void getLogin(Users users, FireStoreListener listener) {
        LocalHash hash = new LocalHash();
        db.collection("user")
                .whereEqualTo("email", users.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            listener.onAddUserError(new Exception("Wrong Username or Password"));
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Log.e("SUCCESS", "onSuccess: DOCUMENT" + documentSnapshot.getId() + " ; " + documentSnapshot.getData());

                                    Users users1 = documentSnapshot.toObject(Users.class);
                                    if (hash.verifyPassword(users.getPassword(), users1.getPassword())) {
                                        users1.setDocID(documentSnapshot.getId());
                                        listener.onAddUserSuccess(users1);
                                    }

                                } else {
                                    listener.onAddUserError(new Exception("Wrong Username or Password"));
                                }
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onAddUserError(e);
                    }
                });

    }

    @Override
    public void storeBuildings(Buildings buildings, FireStoreListener listener) {
        Map<String, Object> mapToInsert = Common.toBuildingMaps(buildings);

        db.collection("buildings")
                .document()
                .set(mapToInsert)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onAddUserError(e);
                    }
                });
    }

    @Override
    public void forgotPassword(Users users, FireStoreListener listener) {
        LocalHash hash = new LocalHash();
        String hashPass = hash.makeHashPassword(users.getPassword());
        users.setPassword(hashPass);
        db.collection("user")
                .whereEqualTo("email", users.getEmail())
                .whereEqualTo("secret", users.getSecret())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            listener.onAddUserError(new Exception("Wrong Username or Password"));
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Log.e("SUCCESS", "onSuccess: DOCUMENT" + documentSnapshot.getId() + " ; " + documentSnapshot.getData());

                                    Users users1 = documentSnapshot.toObject(Users.class);
                                    users1.setPassword(hashPass);
                                    users1.setDocID(documentSnapshot.getId());
                                    updateUserRecord(users1, listener);

                                } else {
                                    listener.onAddUserError(new Exception("Wrong Username or Password"));
                                }
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onAddUserError(e);
                    }
                });
    }
}
