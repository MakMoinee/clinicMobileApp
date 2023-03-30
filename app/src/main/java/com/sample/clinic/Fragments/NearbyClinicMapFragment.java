package com.sample.clinic.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sample.clinic.HospitalDetailActivity;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Interfaces.LocalRequestListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Models.FullNearPlacesResponse;
import com.sample.clinic.Models.NearPlacesRequest;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalRequest;
import com.sample.clinic.databinding.FragmentNearbyClinicMapBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NearbyClinicMapFragment extends Fragment {

    Context mContext;
    FragmentFinish fn;
    MainButtonsListener listener;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    FragmentNearbyClinicMapBinding binding;
    FullNearPlacesResponse fullNearPlacesResponse;

    GoogleMap gMap;

    LatLng currentLocation, originalLocation;
    Boolean locationPermissionGranted = false;
    LocalRequest localRequest;
    List<LatLng> listOfHospitalLocations = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<NearPlacesResponse> hospitalList = new ArrayList<>();
    NearPlacesResponse selectedHospital = new NearPlacesResponse();
    String selectedPlace = "";


    private FusedLocationProviderClient providerClient;
    Boolean runOnce = false;

    public NearbyClinicMapFragment(Context mContext, FragmentFinish fn, MainButtonsListener listener) {
        this.mContext = mContext;
        this.fn = fn;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNearbyClinicMapBinding.inflate(inflater, container, false);
        localRequest = new LocalRequest(mContext);
        providerClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLocationPermission();
        initMap(binding.getRoot());
        initValues();
        initListeners();
        return binding.getRoot();
    }

    private void initListeners() {
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    break;
                case R.id.action_booking:
                    listener.onBookingClick();
                    return true;
                case R.id.action_appointment:
                    listener.onConsultClick();
                    return true;
                case R.id.action_settings:
                    listener.onNavClick();
                    break;
                case R.id.action_chat:
                    listener.onChatClick();
                    break;
            }
            return false;
        });
        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hospitalList.size() > 0 && selectedPlace != "") {
                    for (NearPlacesResponse placesResponse : hospitalList) {
                        if (placesResponse.getName().equals(selectedPlace)) {
                            String rawHospital = new Gson().toJson(placesResponse);
                            Intent intent = new Intent(mContext, HospitalDetailActivity.class);
                            intent.putExtra("rawHospital", rawHospital);
                            mContext.startActivity(intent);
                            binding.relativePopup.setVisibility(View.GONE);
                            selectedPlace = "";
                        }
                    }
                }
            }
        });
        binding.btnCancel.setOnClickListener(v -> binding.relativePopup.setVisibility(View.GONE));
    }

    private void initMap(View mView) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> gMap = googleMap);
    }

    private void initValues() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentLocation == null) {
                    Toast.makeText(mContext, "Current Location couldn't detected. Please turn on location services or move to an open space", Toast.LENGTH_SHORT).show();

                }
            }
        }, 5000);
    }


    private void fetchLocation() {

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000,
                5000, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.e("LOCATION:", location.toString());
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {

                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }
                });
        boolean isavailable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isavailable) {

            Location loc = lm.getLastKnownLocation("gps");

            if (loc != null) {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                currentLocation = new LatLng(latitude, longitude);

                NearPlacesRequest req = new NearPlacesRequest();
                req.setLocation(String.format("%s,%s", currentLocation.latitude, currentLocation.longitude));
                localRequest.getNearbyHospitals(req, new LocalRequestListener() {
                    @Override
                    public void onSuccess(FullNearPlacesResponse f) {
                        fullNearPlacesResponse = f;
                        if (f != null) {
                            List<NearPlacesResponse> results = removeDuplicates(fullNearPlacesResponse.getResults());

                            for (NearPlacesResponse resp : results) {
                                LatLng latLng = new LatLng(resp.getGeometry().getLocation().getLat(), resp.getGeometry().getLocation().getLng());
                                Marker marker = gMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(resp.getName())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                markerList.add(marker);
                                addListenersToMarkers();
                                listOfHospitalLocations.add(latLng);
                            }
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Your Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext, "Failed to get nearby hospitals", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                initValues();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        } else {
            providerClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            NearPlacesRequest req = new NearPlacesRequest();
                            req.setLocation(String.format("%s,%s", currentLocation.latitude, currentLocation.longitude));
                            markerList = new ArrayList<>();
                            if (originalLocation == null) {
                                originalLocation = currentLocation;
                            }
                            localRequest.getNearbyHospitals(req, new LocalRequestListener() {
                                @Override
                                public void onSuccess(FullNearPlacesResponse f) {
                                    fullNearPlacesResponse = f;
                                    if (f != null) {
                                        List<NearPlacesResponse> results = removeDuplicates(fullNearPlacesResponse.getResults());
                                        hospitalList = results;
                                        for (NearPlacesResponse resp : results) {
                                            LatLng latLng = new LatLng(resp.getGeometry().getLocation().getLat(), resp.getGeometry().getLocation().getLng());
                                            Marker marker = gMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(resp.getName())
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            markerList.add(marker);
                                            listOfHospitalLocations.add(latLng);
                                        }
                                        gMap.addMarker(new MarkerOptions()
                                                .position(currentLocation)
                                                .title("Your Location")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                                        addListenersToMarkers();
                                        if (!runOnce) {
                                            runOnce = true;
                                            runUIThread();
                                        }
                                    }
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(mContext, "Failed to get nearby hospitals", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            initValues();
                        }
                    });
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void addListenersToMarkers() {
        gMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle().equalsIgnoreCase("Your Location")) {
                binding.relativePopup.setVisibility(View.GONE);
                selectedPlace = "";
            } else {
                binding.relativePopup.setVisibility(View.VISIBLE);
                binding.txtLocation.setText(marker.getTitle());
                selectedPlace = marker.getTitle();
            }

            return false;
        });
        gMap.setOnMapClickListener(latLng -> {
            binding.relativePopup.setVisibility(View.GONE);
            selectedPlace = "";
        });
    }

    private List<NearPlacesResponse> removeDuplicates(List<NearPlacesResponse> results) {
        List<NearPlacesResponse> uniqueList = new ArrayList<>();
        Map<String, NearPlacesResponse> uniqueMap = new HashMap<>();
        for (NearPlacesResponse res : results) {
            if (uniqueMap != null) {
                if (uniqueMap.containsKey(res.getName())) {
                    continue;
                }
            }
            uniqueMap.put(res.getName(), res);
        }
        for (Map.Entry<String, NearPlacesResponse> r : uniqueMap.entrySet())
            uniqueList.add(r.getValue());

        return uniqueList;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
//        fetchLocation();
        getLocation();
    }

    private void runUIThread() {
        Runnable runnable = () -> {
            if (currentLocation.latitude == originalLocation.latitude && currentLocation.longitude == originalLocation.longitude) {

            } else {
                getLocation();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
    }

}
