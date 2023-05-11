package com.sample.clinic.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.sample.clinic.Adapters.CategoryAdapter;
import com.sample.clinic.HospitalDetailActivity;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Interfaces.LocalRequestListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Models.Categories;
import com.sample.clinic.Models.FullNearPlacesResponse;
import com.sample.clinic.Models.NearPlacesRequest;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.LocalRequest;
import com.sample.clinic.databinding.DialogCategoryBinding;
import com.sample.clinic.databinding.FragmentNearbyClinicMapBinding;

import org.checkerframework.checker.units.qual.A;

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
    LocalFirestore2 fs;
    List<LatLng> listOfHospitalLocations = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<NearPlacesResponse> hospitalList = new ArrayList<>();
    NearPlacesResponse selectedHospital = new NearPlacesResponse();
    String selectedPlace = "";
    DialogCategoryBinding categoryBinding;
    String keyword = "hospital";
    List<Categories> categoriesList = new ArrayList<>();

    AlertDialog catDialog;

    private FusedLocationProviderClient providerClient;
    Boolean runOnce = false;
    ScheduledExecutorService executor;
    ProgressDialog pdLoad;

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
        fs = new LocalFirestore2(mContext);
        providerClient = LocationServices.getFusedLocationProviderClient(mContext);
        pdLoad = new ProgressDialog(mContext);
        pdLoad.setMessage("Loading Maps ...");
        pdLoad.setCancelable(false);
        getLocationPermission();
        initMap(binding.getRoot());
        loadCategories();


        return binding.getRoot();
    }

    private void loadCategories() {
        fs.getCategories(new FireStoreListener() {
            @Override
            public void onSuccessCategories(List<Categories> c) {
                categoriesList = c;
                chooseCategory();
            }

            @Override
            public void onError() {
                Toast.makeText(mContext, "There are no active categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseCategory() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        categoryBinding = DialogCategoryBinding.inflate(LayoutInflater.from(mContext), null, false);
        mBuilder.setView(categoryBinding.getRoot());

        categoryBinding.btnProceed.setEnabled(false);
        CategoryAdapter adapter = new CategoryAdapter(mContext, categoriesList, new AdapterListener() {
            @Override
            public void onClick(int position) {
                Categories c = categoriesList.get(position);
                if (c != null) {
                    keyword = c.getCategory().toLowerCase();
                    categoryBinding.btnProceed.setEnabled(true);
                    Toast.makeText(mContext, String.format("You have selected category: %s", keyword), Toast.LENGTH_SHORT).show();
                }
            }
        });

        categoryBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
        categoryBinding.recycler.setAdapter(adapter);

        /**
         * chooseCategory listeners
         */
        categoryBinding.btnProceed.setOnClickListener(v -> {
            pdLoad.show();
            if (executor != null) executor.shutdown();
            if (markerList.size() > 0) {
                for (Marker marker : markerList) markerList.remove(marker);
            }
            catDialog.dismiss();
            getLocation();
            initValues();
            initListeners();
        });
        catDialog = mBuilder.create();
        catDialog.setCancelable(false);
        catDialog.show();
    }

    private void initListeners() {
        binding.btnAccept.setOnClickListener(v -> {
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
        });
        binding.btnCancel.setOnClickListener(v -> binding.relativePopup.setVisibility(View.GONE));
        binding.btnChangeCategory.setOnClickListener(v -> {
            fn.reloadNearbyFragment();
        });
    }

    private void initMap(View mView) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> gMap = googleMap);
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    break;
                case R.id.action_settings:
                    listener.onNavClick();
                    return true;
            }
            return false;
        });
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
                localRequest.getNearbyHospitals(req, keyword, new LocalRequestListener() {
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
                            localRequest.getNearbyHospitals(req, keyword, new LocalRequestListener() {
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
                                        pdLoad.dismiss();
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
                            pdLoad.dismiss();
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
                binding.txtLatLng.setText(String.format("Latitude:%s , Longitude:%s ", marker.getPosition().latitude, marker.getPosition().longitude));
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
//        getLocation();
    }

    private void runUIThread() {
        Runnable runnable = () -> {
            if (currentLocation.latitude == originalLocation.latitude && currentLocation.longitude == originalLocation.longitude) {

            } else {
                getLocation();
            }
        };
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
    }

}
