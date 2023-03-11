package com.sample.clinic.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sample.clinic.databinding.FragmentAdminHomeBinding;

public class AdminHomeFragment extends Fragment {

    FragmentAdminHomeBinding binding;
    Context mContext;

    public AdminHomeFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(LayoutInflater.from(mContext), container, false);
        return binding.getRoot();
    }
}
