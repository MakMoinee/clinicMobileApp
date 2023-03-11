package com.sample.clinic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sample.clinic.Fragments.AdminHomeFragment;
import com.sample.clinic.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;
    Fragment fragment;
    FragmentTransaction ft;
    FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.btnNav.setSelectedItemId(R.id.action_home);
        binding.btnNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragment = new AdminHomeFragment(AdminActivity.this);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragment, fragment, null);
                    ft.commit();
                    return true;
                case R.id.action_doctor_profiles:
                    break;
                case R.id.action_settings:
                    break;
            }
            return false;
        });
    }
}
