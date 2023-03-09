package com.sample.clinic;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Common.Constants;
import com.sample.clinic.databinding.ActivitySetLocationBinding;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivitySetLocationBinding binding;
    GoogleMap gMap;
    LatLng currentLocation;
    Marker gmapMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String rawCurrentLocation = getIntent().getStringExtra("rawCurrentLocation");
        currentLocation = new Gson().fromJson(rawCurrentLocation, new TypeToken<LatLng>() {
        }.getType());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initListeners();


    }

    private void initListeners() {
        binding.btnSelectAsLocation.setOnClickListener(v -> {
            if (currentLocation != null) {
                Constants.selectedLocation = currentLocation;
                finish();
            }

        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (currentLocation != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }

        gMap.setOnMapClickListener(latLng -> {
            if (gmapMarker != null) gmapMarker.remove();
            gmapMarker = gMap.addMarker(new MarkerOptions().position(latLng).title("selected position"));
            currentLocation = latLng;
            binding.btnSelectAsLocation.setVisibility(View.VISIBLE);
        });
    }
}
