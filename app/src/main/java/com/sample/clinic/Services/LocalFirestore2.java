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
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Categories;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.NearPlacesRequest;
import com.sample.clinic.Models.Users;
import com.sample.clinic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalFirestore2 {

    Context mContext;
    FirebaseFirestore db;

    public LocalFirestore2(Context mContext) {
        this.mContext = mContext;
        db = FirebaseFirestore.getInstance();
    }


    public void getNearbyHospitals(NearPlacesRequest req, FireStoreListener listener) {

    }

    public void addBooking(Bookings bookings, FireStoreListener listener) {
        bookings.setStatus("Active");
        Map<String, Object> fMap = Common.getBookMap(bookings);
        db.collection("bookings")
                .document()
                .set(fMap)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_ADD_BOOKING", e.getMessage());
                    listener.onError();
                });
    }

    public void updateBooking(Bookings bookings, FireStoreListener listener) {
        bookings.setStatus("Active");
        Map<String, Object> fMap = Common.getBookMap(bookings);
        db.collection("bookings")
                .document(bookings.getDocID())
                .set(fMap, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_UPDATE_BOOKING", e.getMessage());
                    listener.onError();
                });
    }

    public void getAllBookings(String userID, FireStoreListener listener) {
        List<Bookings> bookingsList = new ArrayList<>();
        db.collection("bookings")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Bookings bookings = documentSnapshot.toObject(Bookings.class);
                                bookings.setDocID(documentSnapshot.getId());
                                bookingsList.add(bookings);
                            }
                        }
                        listener.onSuccess(bookingsList);
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void deleteBooking(String docID, FireStoreListener listener) {
        db.collection("bookings")
                .document(docID)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void addDoctor(Doctor doctor, FireStoreListener listener) {
        Map<String, Object> finalMap = Common.getDoctorMap(doctor);
        db.collection("doctor")
                .document(doctor.getDocID())
                .set(finalMap)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void addDoctorUser(Users users, FireStoreListener listener) {
        LocalHash hash = new LocalHash();
        String hashPass = hash.makeHashPassword(users.getPassword());
        users.setPassword(hashPass);
        Map<String, Object> finalMap = Common.toLoginMaps(users);
        db.collection("user")
                .document()
                .set(finalMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("user")
                                .whereEqualTo("email", users.getEmail())
                                .whereEqualTo("userType", users.getUserType())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        listener.onError();
                                    } else {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            if (documentSnapshot.exists()) {
                                                Users users1 = documentSnapshot.toObject(Users.class);
                                                if (users1 != null) {
                                                    users1.setDocID(documentSnapshot.getId());
                                                    listener.onAddUserSuccess(users1);
                                                    break;
                                                } else {
                                                    listener.onError();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FAILED_TO_GETUSER_DR", e.getMessage());
                                    listener.onError();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FAILED_TO_ADDUSER_DR", e.getMessage());
                    listener.onError();
                });
    }

    public void updateDoctor(Doctor doctor, FireStoreListener listener) {
        Map<String, Object> finalMap = Common.getDoctorMap(doctor);
        db.collection("doctor")
                .document(doctor.getDocID())
                .set(finalMap, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void deleteDoctor(String docID, FireStoreListener listener) {
        db.collection("doctor")
                .document(docID)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void searchDoctor(String searchKey, FireStoreListener listener) {
        db.collection("doctor")
                .whereEqualTo("doctorName", searchKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Doctor> doctorList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Doctor doctor = documentSnapshot.toObject(Doctor.class);
                                doctor.setDocID(documentSnapshot.getId());
                                doctorList.add(doctor);
                            }
                        }
                        if (doctorList.size() > 0) {
                            listener.onSuccessDoctor(doctorList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }


    public void getDoctors(FireStoreListener listener) {
        List<Doctor> doctorList = new ArrayList<>();
        db.collection("doctor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Doctor doctor = documentSnapshot.toObject(Doctor.class);
                                doctor.setDocID(documentSnapshot.getId());
                                doctorList.add(doctor);
                            } else {
                                continue;
                            }
                        }
                        if (doctorList.size() > 0) {
                            listener.onSuccessDoctor(doctorList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }


    public void addAppointment(Appointment appointment, FireStoreListener listener) {
        appointment.setStatus("pending approval");
        Map<String, Object> map = Common.getAppointmentMap(appointment);
        db.collection("appointments")
                .document()
                .set(map)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void updateAppointment(Appointment appointment, FireStoreListener listener) {
        Map<String, Object> map = Common.getAppointmentMap(appointment);
        db.collection("appointments")
                .document(appointment.getDocID())
                .set(map, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }

    public void getAppointments(String userID, FireStoreListener listener) {
        db.collection("appointments")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Appointment> appointmentList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                appointment.setDocID(documentSnapshot.getId());
                                appointmentList.add(appointment);
                            }
                        }

                        if (appointmentList.size() > 0) {
                            listener.onSuccessAppointment(appointmentList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void getDoctorAppointments(String docID, FireStoreListener listener) {
        db.collection("appointments")
                .whereEqualTo("doctorID", docID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Appointment> appointmentList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                appointment.setDocID(documentSnapshot.getId());
                                appointmentList.add(appointment);
                            }
                        }

                        if (appointmentList.size() > 0) {
                            listener.onSuccessAppointment(appointmentList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void getDoctorAppointmentsByName(String docID, String patientUserID, FireStoreListener listener) {
        db.collection("appointments")
                .whereEqualTo("doctorID", docID)
                .whereEqualTo("userID", patientUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Appointment> appointmentList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                appointment.setDocID(documentSnapshot.getId());
                                appointmentList.add(appointment);
                            }
                        }

                        if (appointmentList.size() > 0) {
                            listener.onSuccessAppointment(appointmentList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }


    public void addMessage(Message message, String name, FireStoreListener listener) {
        Map<String, Object> map = Common.getMessageMap(message, name);
        db.collection("messages")
                .document()
                .set(map)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_ADDING_MESSAGE", e.getMessage());
                    listener.onError();
                });

    }

    public void getMessages(String userID, FireStoreListener listener) {
        db.collection("messages")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Message2> messageList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Message2 message = documentSnapshot.toObject(Message2.class);
                            message.setDocID(documentSnapshot.getId());
                            messageList.add(message);
                        }

                        if (messageList.size() > 0) {
                            listener.onSuccessMessage(messageList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR_GET_MESSAGE", e.getMessage());
                    listener.onError();
                });
    }

    public void getDoctorMessages(String docID, FireStoreListener listener) {
        db.collection("messages")
                .whereEqualTo("doctorID", docID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Message2> messageList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Message2 message = documentSnapshot.toObject(Message2.class);
                            message.setDocID(documentSnapshot.getId());
                            messageList.add(message);
                        }

                        if (messageList.size() > 0) {
                            listener.onSuccessMessage(messageList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR_GET_MESSAGE", e.getMessage());
                    listener.onError();
                });
    }

    public void deleteMessage(String docID, FireStoreListener listener) {
        db.collection("messages")
                .document(docID)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_DELETE_MSG", e.getMessage());
                    listener.onError();
                });
    }

    public void updateMessage(Message message, String docName, String docID, FireStoreListener listener) {
        Map<String, Object> map = Common.getMessageMap(message, docName);
        Log.e("MAP", map.entrySet().toString());
        db.collection("messages")
                .document(docID)
                .set(map, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ERROR_UPDATE_FS_MSG", e.getMessage());
                    listener.onError();
                });
    }

    public void getAllUsers(FireStoreListener listener) {
        db.collection("user")
                .whereEqualTo("userType", 2)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            listener.onError();
                        } else {
                            List<Users> usersList = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Users users = documentSnapshot.toObject(Users.class);
                                    if (users != null) {
                                        users.setDocID(documentSnapshot.getId());
                                        usersList.add(users);
                                    }
                                }
                            }

                            if (usersList.size() > 0) {
                                listener.onSuccessUsers(usersList);
                            } else {
                                listener.onError();
                            }
                        }

                    }
                })
                .addOnFailureListener(e -> Log.e("ERROR_GET_USERS", e.getMessage()));
    }

    public void getUserByLastName(String lastName, FireStoreListener listener) {
        db.collection("user")
                .whereEqualTo("userType", 2)
                .whereEqualTo("lastName", lastName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Users> usersList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Users users = documentSnapshot.toObject(Users.class);
                                if (users != null) {
                                    users.setDocID(documentSnapshot.getId());
                                    usersList.add(users);
                                }
                            }
                        }

                        if (usersList.size() > 0) {
                            listener.onSuccessUsers(usersList);
                        } else {
                            listener.onError();
                        }
                    }

                })
                .addOnFailureListener(e -> Log.e("ERROR_GET_USERS", e.getMessage()));
    }

    public void getSpecificUser(String docID, FireStoreListener listener) {
        db.collection("user")
                .document(docID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Users users = documentSnapshot.toObject(Users.class);
                        if (users != null) {
                            users.setDocID(documentSnapshot.getId());
                            listener.onAddUserSuccess(users);
                        } else {
                            listener.onError();
                        }
                    } else {
                        listener.onError();
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void getCategories(FireStoreListener listener) {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onError();
                    } else {
                        List<Categories> categoriesList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Categories categories = documentSnapshot.toObject(Categories.class);
                                if (categories != null) {
                                    categories.setDocID(documentSnapshot.getId());
                                    categories.setCategoryPhotoId(R.drawable.ic_category);
                                    categoriesList.add(categories);
                                }
                            }
                        }
                        if (categoriesList.size() > 0) {
                            listener.onSuccessCategories(categoriesList);
                        } else {
                            listener.onError();
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }

    public void addCategory(Categories category, FireStoreListener listener) {
        Map<String, Object> fMap = new HashMap<>();
        fMap.put("category", category.getCategory());
        String docID = db.collection("categories")
                .document().getId();

        db.collection("categories")
                .document(docID)
                .set(fMap, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    listener.onError();
                });

    }

    public void deleteCategory(Categories category, FireStoreListener listener){
        db.collection("categories")
                .document(category.getDocID())
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError());
    }
}
