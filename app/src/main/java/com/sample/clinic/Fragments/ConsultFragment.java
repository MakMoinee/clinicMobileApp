package com.sample.clinic.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.R;
import com.sample.clinic.databinding.FragmentConsultBinding;

public class ConsultFragment extends Fragment {
    FragmentConsultBinding binding;
    Context mContext;
    MainButtonsListener listener;

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
//        binding.navBottom.setSelectedItemId(R.id.action_consult);
        binding.navBottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        listener.onHomeClick();
                        break;
                    case R.id.action_booking:
                        listener.onBookingClick();
                        break;
//                    case R.id.action_consult:
//                        return true;
                    case R.id.action_settings:
                        listener.onNavClick();
                        break;
                }
                return false;
            }
        });
    }
}
