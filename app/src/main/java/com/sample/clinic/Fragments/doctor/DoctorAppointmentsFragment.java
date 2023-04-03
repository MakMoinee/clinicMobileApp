package com.sample.clinic.Fragments.doctor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


    public DoctorAppointmentsFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorAppointmentsBinding.inflate(LayoutInflater.from(mContext), container, false);
        loadAppointments();
        return binding.getRoot();
    }

    private void loadAppointments() {
        fs = new LocalFirestore2(mContext);
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getDoctorAppointments(users.getDocID(), new FireStoreListener() {
            @Override
            public void onSuccessAppointment(List<Appointment> appointmentList) {
                FireStoreListener.super.onSuccessAppointment(appointmentList);
            }

            @Override
            public void onError() {
                FireStoreListener.super.onError();
            }
        });
    }
}
