package com.sample.clinic.Fragments.doctor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.auth.User;
import com.sample.clinic.Adapters.doctor.AppointmentDoctorAdapter;
import com.sample.clinic.FillUpInfoActivity;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.ReminderNotif;
import com.sample.clinic.databinding.DialogAppointmentDetailsBinding;
import com.sample.clinic.databinding.DialogAppointmentsDoctorBinding;
import com.sample.clinic.databinding.FragmentDoctorAppointmentsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment {

    FragmentDoctorAppointmentsBinding binding;
    Context mContext;
    List<Users> usersList = new ArrayList<>();
    List<Appointment> appointmentList = new ArrayList<>();
    LocalFirestore2 fs;

    AppointmentDoctorAdapter adapter;

    DialogAppointmentsDoctorBinding dialogAppointmentsDoctorBinding;
    AlertDialog appointmentDialog;
    DialogAppointmentDetailsBinding detailsBinding;

    ProgressDialog pd;


    public DoctorAppointmentsFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorAppointmentsBinding.inflate(LayoutInflater.from(mContext), container, false);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
        loadAppointments();
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.layoutSearch.setEndIconOnClickListener(v -> {
            String searchName = binding.editPatientName.getText().toString();
            if (searchName.equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                binding.recycler.setAdapter(null);
                loadAppointments();
            } else {
                loadSearchAppointment(searchName);
            }
        });
    }

    private void loadSearchAppointment(String searchName) {
        fs = new LocalFirestore2(mContext);
        fs.getUserByLastName(searchName, new FireStoreListener() {
            @Override
            public void onSuccessUsers(List<Users> u) {
                usersList = u;
                Users users = new MyUserPreferrence(mContext).getUsers();
                fs.getDoctorAppointments(users.getDocID(), new FireStoreListener() {
                    @Override
                    public void onSuccessAppointment(List<Appointment> a) {
                        appointmentList = a;
                        adapter = new AppointmentDoctorAdapter(mContext, appointmentList, usersList, new AdapterListener() {
                            @Override
                            public void onLongClick(int position) {
                                Appointment a = appointmentList.get(position);
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                                DialogInterface.OnClickListener dListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            pd.show();
                                            a.setStatus("approved");
                                            updateAppointment(a, 1);
                                            break;
                                        case DialogInterface.BUTTON_POSITIVE:
                                            pd.show();
                                            a.setStatus("rejected");
                                            updateAppointment(a, 2);
                                            break;
                                        default:
                                            dialog.dismiss();
                                            break;
                                    }
                                };
                                mBuilder.setMessage("Click the following options to proceed")
                                        .setNegativeButton("Accept", dListener)
                                        .setPositiveButton("Decline", dListener)
                                        .setNeutralButton("Cancel", dListener)
                                        .setCancelable(false)
                                        .show();
                            }

                            @Override
                            public void onChatClick(int position) {
                                AlertDialog.Builder tBuilder = new AlertDialog.Builder(mContext);
                                detailsBinding = DialogAppointmentDetailsBinding.inflate(LayoutInflater.from(mContext), null, false);
                                tBuilder.setView(detailsBinding.getRoot());
                            }
                        });
                        binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.recycler.setAdapter(adapter);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext, String.format("There are no match with the last name: %s", searchName), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                Toast.makeText(mContext, String.format("There are no match with the last name: %s", searchName), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointments() {
        fs = new LocalFirestore2(mContext);
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getDoctorAppointments(users.getDocID(), new FireStoreListener() {
            @Override
            public void onSuccessAppointment(List<Appointment> a) {
                appointmentList = a;
                if (appointmentList.size() > 0) {
                    fs.getAllUsers(new FireStoreListener() {
                        @Override
                        public void onSuccessUsers(List<Users> u) {
                            usersList = u;
                            adapter = new AppointmentDoctorAdapter(mContext, appointmentList, usersList, new AdapterListener() {
                                @Override
                                public void onLongClick(int position) {
                                    Appointment a = appointmentList.get(position);
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                                    DialogInterface.OnClickListener dListener = (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                pd.show();
                                                a.setStatus("approved");
                                                updateAppointment(a, 1);
                                                break;
                                            case DialogInterface.BUTTON_POSITIVE:
                                                pd.show();
                                                a.setStatus("rejected");
                                                updateAppointment(a, 2);
                                                break;
                                            default:
                                                dialog.dismiss();
                                                break;
                                        }
                                    };
                                    mBuilder.setMessage("Click the following options to proceed")
                                            .setNegativeButton("Accept", dListener)
                                            .setPositiveButton("Decline", dListener)
                                            .setNeutralButton("Cancel", dListener)
                                            .setCancelable(false)
                                            .show();
                                }

                                @Override
                                public void onChatClick(int position) {
                                    Appointment a = appointmentList.get(position);
                                    AlertDialog.Builder tBuilder = new AlertDialog.Builder(mContext);
                                    DialogInterface.OnClickListener dListener = (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_NEGATIVE:

                                                a.setStatus("approved");
                                                updateAppointment(a, 1);
                                                break;
                                            case DialogInterface.BUTTON_POSITIVE:
                                                a.setStatus("rejected");
                                                updateAppointment(a, 2);
                                                break;
                                            default:
                                                dialog.dismiss();
                                                break;
                                        }
                                    };
                                    detailsBinding = DialogAppointmentDetailsBinding.inflate(LayoutInflater.from(mContext), null, false);
                                    setDetailsAppointment(a);
                                    tBuilder.setView(detailsBinding.getRoot());
                                    if (a.getStatus().equals("approved")) {
                                        tBuilder.setNeutralButton("Close", dListener)
                                                .setCancelable(false)
                                                .show();
                                    } else {
                                        tBuilder.setNegativeButton("Accept", dListener)
                                                .setPositiveButton("Decline", dListener)
                                                .setNeutralButton("Cancel", dListener)
                                                .setCancelable(false)
                                                .show();
                                    }

                                }
                            });

                            binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.recycler.setAdapter(adapter);


                        }

                        @Override
                        public void onError() {
                            Toast.makeText(mContext, "There are no appointments as of the moment", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "There are no appointments as of the moment", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError() {
                Toast.makeText(mContext, "There are no appointments as of the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetailsAppointment(Appointment a) {
        detailsBinding.txtBookDate.setText(String.format("Book Date and Time: %s", a.getBookDateTime()));
        Users mUser = new Users();
        for (Users u : usersList) {
            if (a.getUserID().equals(u.getDocID())) {
                mUser = u;
                break;
            }
        }
        String fullName = String.format("%s, %s %s", mUser.getLastName(), mUser.getFirstName(), mUser.getMiddleName());
        detailsBinding.txtPatientName.setText(String.format("Patient Name: %s", fullName));
        detailsBinding.txtPhoneNumber.setText(String.format("Patient Contact Number: %s", a.getClientContactNumber()));
    }

    private void updateAppointment(Appointment a, int flag) {
        fs.updateAppointment(a, new FireStoreListener() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                Toast.makeText(mContext, "Successfully updated appointment", Toast.LENGTH_SHORT).show();
                if (flag == 1) {
                    Intent intent = new Intent("com.sample.clinic.REMINDER");
                    intent.putExtra("notification_id", (a.getNotifID() * -1));
                    mContext.sendBroadcast(intent);

                    new Handler().postDelayed(() -> {
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        Date detTime = null;
                        try {
                            detTime = format.parse(a.getBookDateTime());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        if (detTime != null) {
                            long timeInMilli = detTime.getTime();
                            Users mUser = new Users();
                            for (Users u : usersList) {
                                if (a.getUserID().equals(u.getDocID())) {
                                    mUser = u;
                                }
                            }
                            String fullName = String.format("%s, %s %s", mUser.getLastName(), mUser.getFirstName(), mUser.getMiddleName());
                            showNotifs(a.getNotifID(), timeInMilli, String.format("Appointment with %s at %s", fullName, a.getBookDateTime()));
                        }

                    }, 1000);
                }
                binding.recycler.setAdapter(null);
                loadAppointments();
            }

            @Override
            public void onError() {
                pd.dismiss();
                Toast.makeText(mContext, "Failed to update appointment", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("NewApi")
    private void showNotifs(int notifID, long notificationTime, String notificationText) {
        Intent notificationIntent = new Intent(mContext, ReminderNotif.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_ID, notifID);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_TEXT, notificationText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notifID, notificationIntent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }
}
