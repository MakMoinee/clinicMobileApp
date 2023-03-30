package com.sample.clinic.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationBarView;
import com.sample.clinic.Adapters.AppointmentAdapter;
import com.sample.clinic.Adapters.DoctorAdapter;
import com.sample.clinic.ClientDoctorProfileActivity;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.FragmentConsultBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class ConsultFragment extends Fragment {
    FragmentConsultBinding binding;
    Context mContext;
    MainButtonsListener listener;
    AppointmentAdapter adapter;
    LocalFirestore2 fs;

    public ConsultFragment(Context c, MainButtonsListener m) {
        this.mContext = c;
        this.listener = m;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConsultBinding.inflate(LayoutInflater.from(mContext), container, false);
        setValues();
        return binding.getRoot();
    }

    private void setValues() {
        fs = new LocalFirestore2(mContext);
        binding.btnRelative.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, ClientDoctorProfileActivity.class)));
        binding.navBottom.setSelectedItemId(R.id.action_appointment);
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    listener.onHomeClick();
                    break;
                case R.id.action_booking:
                    listener.onBookingClick();
                    break;
                case R.id.action_appointment:
                    return true;
                case R.id.action_chat:
                    listener.onChatClick();
                    break;
                case R.id.action_settings:
                    listener.onNavClick();
                    break;
            }
            return false;
        });

        loadData();
    }

    private void loadData() {
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getAppointments(users.getDocID(), new FireStoreListener() {
            @Override
            public void onSuccessAppointment(List<Appointment> appointmentList) {
                adapter = new AppointmentAdapter(mContext, appointmentList, new AdapterListener() {
                    @Override
                    public void onClick() {
                        AdapterListener.super.onClick();
                    }
                });
                binding.recycler2.setLayoutManager(new LinearLayoutManager(mContext));
                binding.txtNoAppointment.setVisibility(View.GONE);
                binding.recycler2.setAdapter(adapter);
            }

            @Override
            public void onError() {
                binding.txtNoAppointment.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "There are no active appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.recycler2.setAdapter(null);
        loadData();
    }
}
