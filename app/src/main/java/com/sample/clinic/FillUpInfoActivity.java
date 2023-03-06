package com.sample.clinic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.ActivityFillUpBinding;

public class FillUpInfoActivity extends AppCompatActivity {

    ActivityFillUpBinding binding;
    LocalFirestore2 fs;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFillUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setValues();
        setListeners();
    }

    private void setValues() {
        fs = new LocalFirestore2(FillUpInfoActivity.this);
        pd = new ProgressDialog(FillUpInfoActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(falses);
    }

    private void setListeners() {
        binding.btnSaveBook.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String address = binding.editAddress.getText().toString();
            String medicalHistory = binding.editMedicalHistory.getText().toString();
            if (name.equals("") || address.equals("") || medicalHistory.equals("")) {
                Toast.makeText(FillUpInfoActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                pd.show();
                Bookings bookings = new Bookings();
                bookings.setClientName(name);
                bookings.setBookDate("");
                bookings.setMedicalHistory(medicalHistory);
                bookings.setAddress(address);
                fs.addBooking(bookings, new FireStoreListener() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        FireStoreListener.super.onSuccess();
                    }

                    @Override
                    public void onError() {
                        pd.dismiss();
                        Toast.makeText(FillUpInfoActivity.this, "Failed to add booking", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
