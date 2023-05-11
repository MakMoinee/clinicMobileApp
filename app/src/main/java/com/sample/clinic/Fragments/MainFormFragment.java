package com.sample.clinic.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.sample.clinic.Adapters.BuildingAdapter;
import com.sample.clinic.Adapters.HospitalAdapter;
import com.sample.clinic.Common.Constants;
import com.sample.clinic.HospitalDetailActivity;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.BuildingListener;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Interfaces.LocalRequestListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Interfaces.StorageListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.FullNearPlacesResponse;
import com.sample.clinic.Models.NearPlacesRequest;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalRequest;
import com.sample.clinic.Services.Storage;
import com.sample.clinic.SetLocationActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainFormFragment extends Fragment implements StorageListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int M_MAX_ENTRIES = 5;
    private Context context;
    private RecyclerView recyclerView;
    private ImageButton btnSettings, btnSearch;
    private List<Buildings> buildingsList = new ArrayList<>();
    private List<Buildings> origList = new ArrayList<>();
    private BuildingAdapter adapter;
    private Storage storage;
    private AutoCompleteTextView txtSearch;
    private String[] buildingFilePaths;
    private int currentFileIndex = 0;
    private FragmentFinish fn;
    private String[] countries;
    private ArrayAdapter<String> adapterStr;
    private String lastSearchVal = "";
    private ImageButton btnProfile;
    private MainButtonsListener mainBtnListener;
    FullNearPlacesResponse fullNearPlacesResponse;
    Boolean locationPermissionGranted;

    HospitalAdapter hAdapter;
    LocalRequest localRequest;

    LatLng currentLocation;

    private NavigationBarView btnBottom;

    private BuildingListener bListener = new BuildingListener() {
        @Override
        public void OnClickListener(View mView, int position) {
            Buildings buildings = buildingsList.get(position);
            String buildName = buildings.getBuildingName().replaceAll(".jpg", "");
            buildings.setDescription(lastSearchVal);
            buildings.setBuildingName(buildName);
            fn.openBuildingFragment(buildings);

        }
    };


    public MainFormFragment(Context mContext, FragmentFinish listener, boolean refresh, MainButtonsListener btn) {
        this.context = mContext;
        this.fn = listener;
        this.mainBtnListener = btn;
        if (refresh) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    storage.getBuildingsPoster();
                }
            }, 50);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_main, container, false);
        initViews(mView);
        initListeners(mView);
        if (Constants.selectedLocation == null) {
            getLocationPermission();
        } else {
            loadLocation();
        }

        return mView;
    }

    private void loadLocation() {
        if (Constants.selectedLocation == null) return;
        currentLocation = Constants.selectedLocation;
        Constants.selectedLocation = null;
        recyclerView.setAdapter(null);
        NearPlacesRequest req = new NearPlacesRequest();
        req.setLocation(String.format("%s,%s", currentLocation.latitude, currentLocation.longitude));
        localRequest.getNearbyHospitals(req, "hospital", new LocalRequestListener() {
            @Override
            public void onSuccess(FullNearPlacesResponse f) {
                fullNearPlacesResponse = f;
                if (f != null) {
                    recyclerView.setAdapter(null);
                    List<NearPlacesResponse> results = removeDuplicates(fullNearPlacesResponse.getResults());
                    Constants.nearbyHospitals = results;
                    hAdapter = new HospitalAdapter(context, results, new AdapterListener() {
                        @Override
                        public void onClick(NearPlacesResponse nearPlacesResponse) {
                            String rawHospital = new Gson().toJson(nearPlacesResponse);
//                                    Log.e("rawHospital", rawHospital);
                            Intent intent = new Intent(context, HospitalDetailActivity.class);
                            intent.putExtra("rawHospital", rawHospital);
                            context.startActivity(intent);
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(hAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                Toast.makeText(context, "Failed to get nearby hospitals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListeners(View mView) {
        txtSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (currentLocation != null) {
                    Intent intent = new Intent(context, SetLocationActivity.class);
                    intent.putExtra("rawCurrentLocation", new Gson().toJson(currentLocation));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Please make sure you turn on your location services first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    break;
                case R.id.action_settings:
                    mainBtnListener.onNavClick();
                    break;
            }
            return false;
        });
    }

    private void loadChangeSearchValue() {
        String searchVal = txtSearch.getText().toString();
        if (searchVal.length() == 0) {
            lastSearchVal = "";
            adapter = new BuildingAdapter(context, origList, bListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }, 50);
            return;
        }
        if (adapterStr.getPosition(searchVal) < 0) {
            lastSearchVal = "";
            return;
        }
        lastSearchVal = searchVal;
        Map<String, String> map = Constants.getBuildingMaps();
        String storageName = map.get(searchVal);


        if (buildingsList.size() == 0) {
            StorageListener storageSearchListener = new StorageListener() {
                @Override
                public void onSuccess(String data) {

                }

                @Override
                public void onSuccessBuilding(List<Buildings> b) {
                    buildingsList = b;
                    adapter = new BuildingAdapter(context, buildingsList, bListener);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(adapter);
                    new Handler().postDelayed(() -> recyclerView.setVisibility(View.VISIBLE), 500);
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onSuccessRetrieveNavGuide(Buildings buildings) {

                }

                @Override
                public void onSuccessRetrieveVideoURL(Buildings buildings) {

                }
            };

            Storage searchStorage = new Storage(storageSearchListener);
            searchStorage.getBuildingsFromStorage(storageName);
        } else {
            Log.e("STORAGE_NAME", storageName.toString());
            List<Buildings> newBuilding = new ArrayList<>();
            for (Buildings b : buildingsList) {
                String buildName = b.getBuildingName().replaceAll(".jpg", "");
                Log.e("buildName", buildName);
                if (buildName.equals(storageName)) {
                    newBuilding.add(b);
                    break;
                }
            }
            buildingsList = newBuilding;

            adapter = new BuildingAdapter(context, newBuilding, bListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            new Handler().postDelayed(() -> recyclerView.setVisibility(View.VISIBLE), 50);
        }


    }

    private void initViews(View mView) {
        txtSearch = mView.findViewById(R.id.editSearch);
        recyclerView = mView.findViewById(R.id.recycler);
        recyclerView.setVisibility(View.INVISIBLE);
        btnSearch = mView.findViewById(R.id.imgSearch);
        storage = new Storage(this);
        buildingFilePaths = Constants.buildingFileFolder.split(",");
        currentFileIndex = 0;
        localRequest = new LocalRequest(context);

//        req.setLocation();
        //fullNearPlacesResponse = new LocalRequest(context).getNearbyHospitals();

//        storage.getBuildingsFromStorage(buildingFilePaths[0]);
//        storage.getBuildingsPoster();

        countries = getResources().getStringArray(R.array.search_array);
        // Create the adapter and set it to the AutoCompleteTextView
        adapterStr = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, countries);

        txtSearch.setAdapter(adapterStr);
        btnBottom = mView.findViewById(R.id.navBottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentLocation == null) {
                    Toast.makeText(context, "Current Location couldn't detected. Please turn on location services or move to an open space", Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSuccess(String data) {

    }

    @Override
    public void onSuccessBuilding(List<Buildings> b) {
        if (b != null) {

            if (buildingsList.size() > 0) {
                buildingsList.clear();
                origList.clear();
            }
            for (Buildings buildings : b) {
                buildingsList.add(buildings);
                origList.add(buildings);
            }

            adapter = new BuildingAdapter(context, buildingsList, bListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }, 50);

        }
    }

    @Override
    public void onError(Exception e) {
        Log.e("STORAGE_ERR", e.getMessage());
    }

    @Override
    public void onSuccessRetrieveNavGuide(Buildings buildings) {

    }

    @Override
    public void onSuccessRetrieveVideoURL(Buildings buildings) {

    }

    private void fetchLocation() {

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                localRequest.getNearbyHospitals(req, "hospital", new LocalRequestListener() {
                    @Override
                    public void onSuccess(FullNearPlacesResponse f) {
                        fullNearPlacesResponse = f;
                        if (f != null) {
                            recyclerView.setAdapter(null);
                            List<NearPlacesResponse> results = removeDuplicates(fullNearPlacesResponse.getResults());
                            Constants.nearbyHospitals = results;
                            hAdapter = new HospitalAdapter(context, results, new AdapterListener() {
                                @Override
                                public void onClick(NearPlacesResponse nearPlacesResponse) {
                                    String rawHospital = new Gson().toJson(nearPlacesResponse);
//                                    Log.e("rawHospital", rawHospital);
                                    Intent intent = new Intent(context, HospitalDetailActivity.class);
                                    intent.putExtra("rawHospital", rawHospital);
                                    context.startActivity(intent);
                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            recyclerView.setAdapter(hAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(context, "Failed to get nearby hospitals", Toast.LENGTH_SHORT).show();
                    }
                });
                // Toast.makeText(context, "Longitude is  " + longitude + "   Latitude is   " + latitude, Toast.LENGTH_LONG).show();

            } else {

            }
        }
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
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context.getApplicationContext(),
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
        fetchLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocation();
    }
}
