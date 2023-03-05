package com.sample.clinic;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.databinding.ActivityHospitalDetailBinding;

public class HospitalDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityHospitalDetailBinding binding;
    Location hospitalLocation;
    GoogleMap mMap;
    Marker hospitalMarker;
    String hospitalName = "";

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
                Boolean isOpen = nearPlacesResponse.getOpening_hours().getOpen_now();
                binding.txtStatus.setText(String.format("Status: %s", isOpen));
                binding.btnBook.setEnabled(isOpen);

                hospitalLocation.setLatitude(nearPlacesResponse.getGeometry().getLocation().getLat());
                hospitalLocation.setLongitude(nearPlacesResponse.getGeometry().getLocation().getLng());
            } else {
                finish();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        hospitalMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(hospitalLocation.getLatitude(), hospitalLocation.getLongitude()))
                .title(hospitalName.toUpperCase()));

    }
}
