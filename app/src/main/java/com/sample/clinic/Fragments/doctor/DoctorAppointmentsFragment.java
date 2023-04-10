package com.sample.clinic.Fragments.doctor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sample.clinic.Adapters.doctor.AppointmentDoctorAdapter;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.FragmentDoctorAppointmentsBinding;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment {

    FragmentDoctorAppointmentsBinding binding;
    Context mContext;
    List<Users> usersList = new ArrayList<>();
    List<Appointment> appointmentList = new ArrayList<>();
    LocalFirestore2 fs;

    AppointmentDoctorAdapter adapter;


    public DoctorAppointmentsFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorAppointmentsBinding.inflate(LayoutInflater.from(mContext), container, false);
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
            public void onSuccessUsers(List<Users> usersList) {
                Users users = new MyUserPreferrence(mContext).getUsers();
                fs.getDoctorAppointments(users.getDocID(), new FireStoreListener() {
                    @Override
                    public void onSuccessAppointment(List<Appointment> a) {
                        appointmentList = a;
                        adapter = new AppointmentDoctorAdapter(mContext, appointmentList, usersList, new AdapterListener() {
                            @Override
                            public void onClick() {
                                AdapterListener.super.onClick();
                            }

                            @Override
                            public void onChatClick(int position) {
                                AdapterListener.super.onChatClick(position);
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
                        public void onSuccessUsers(List<Users> usersList) {
                            adapter = new AppointmentDoctorAdapter(mContext, appointmentList, usersList, new AdapterListener() {
                                @Override
                                public void onClick() {
                                    AdapterListener.super.onClick();
                                }

                                @Override
                                public void onChatClick(int position) {
                                    AdapterListener.super.onChatClick(position);
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
}
