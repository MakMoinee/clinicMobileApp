package com.sample.clinic.doctor;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sample.clinic.Fragments.doctor.DoctorAppointmentsFragment;
import com.sample.clinic.R;
import com.sample.clinic.databinding.ActivityDoctorBinding;

public class DoctorActivity extends AppCompatActivity {

    ActivityDoctorBinding binding;
    Fragment fragment;
    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setValues();
        setListeners();
    }

    private void setValues() {
        fragment = new DoctorAppointmentsFragment(DoctorActivity.this);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, null);
        ft.commit();
    }

    private void setListeners() {
        binding.btnNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragment = new DoctorAppointmentsFragment(DoctorActivity.this);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragment, null);
                    ft.commit();
                    return true;
                case R.id.action_appointment:
                default:
                    return false;
            }
        });
    }
}
