package com.sample.clinic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.text.TimeZoneFormat;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.timepicker.TimeFormat;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Fragments.DatePickerFragment;
import com.sample.clinic.Fragments.TimePickerFragment;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.databinding.ActivityHospitalDetailBinding;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class HospitalDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityHospitalDetailBinding binding;
    LatLng hospitalLocation;
    GoogleMap mMap;
    Marker hospitalMarker;
    String hospitalName = "";

    DatePickerFragment dpDate;
    String selectedDate = "", selectedTime = "";
    TimePickerFragment tpTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String rawHospital = getIntent().getStringExtra("rawHospital");
        if (rawHospital == "") {
            finish();
        } else {
            NearPlacesResponse nearPlacesResponse = new Gson().fromJson(rawHospital, new TypeToken<NearPlacesResponse>() {
            }.getType());

            if (nearPlacesResponse != null) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                binding.txtHospitalName.setText(String.format("Hospital Name: %s", nearPlacesResponse.getName().toUpperCase()));
                hospitalName = nearPlacesResponse.getName();
                Boolean isOpen = false;
                if (nearPlacesResponse.getOpening_hours() != null) {
                    isOpen = nearPlacesResponse.getOpening_hours().getOpen_now();
                    binding.btnBook.setEnabled(isOpen);
                    if (isOpen) {
                        binding.txtStatus.setText("Status: Open");
                    } else {
                        binding.txtStatus.setText("Status: Close");
                    }
                } else {
                    binding.btnBook.setEnabled(true);
                    binding.txtStatus.setVisibility(View.GONE);
                }


                hospitalLocation = new LatLng(nearPlacesResponse.getGeometry().getLocation().getLat(), nearPlacesResponse.getGeometry().getLocation().getLng());

            } else {
                finish();
            }
            setListeners();
        }
    }

    private void setListeners() {
        binding.btnBook.setOnClickListener(v -> {
            dpDate = new DatePickerFragment((view, year, month, dayOfMonth) -> {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());

                if (selectedDate != "") {
                    tpTime = new TimePickerFragment((view1, hourOfDay, minute) -> {
                        Calendar mCalendar1 = Calendar.getInstance();
                        mCalendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar1.set(Calendar.MINUTE, minute);
                        if (hourOfDay > 12) {
                            selectedTime = String.format("%s:%s pm", (hourOfDay - 12), minute);
                        } else {
                            selectedTime = String.format("%s:%s am", hourOfDay, minute);
                        }

                        
                    }, HospitalDetailActivity.this);
                    tpTime.show(getSupportFragmentManager(), "TIME_PICk");
                }
            });
            dpDate.show(getSupportFragmentManager(), "DATE PICK");
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        hospitalMarker = mMap.addMarker(new MarkerOptions()
                .position(hospitalLocation)
                .title(hospitalName.toUpperCase())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15));

    }

}
