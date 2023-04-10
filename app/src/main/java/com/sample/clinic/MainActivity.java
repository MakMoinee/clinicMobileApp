package com.sample.clinic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sample.clinic.Common.Constants;
import com.sample.clinic.Fragments.BookingFragment;
import com.sample.clinic.Fragments.BuildingFragment;
import com.sample.clinic.Fragments.ChatListFragment;
import com.sample.clinic.Fragments.ConsultFragment;
import com.sample.clinic.Fragments.CreateAccountFragment;
import com.sample.clinic.Fragments.FirstFragment;
import com.sample.clinic.Fragments.NearbyClinicMapFragment;
import com.sample.clinic.Fragments.ProfileFragment;
import com.sample.clinic.Fragments.SecondFragment;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Interfaces.ProfileListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.databinding.DialogSettingsBinding;
import com.sample.clinic.doctor.DoctorActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentFinish {

    private Fragment fragment;
    private BuildingFragment buildFragment;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private int fragmentIndex = 0;
    private ProgressDialog ps;

    private List<Buildings> buildings;
    DialogSettingsBinding settingsBinding;
    AlertDialog settingsDialog, dialog;

    Menu mainActivityMenu;

    private WebView wv;
    private WebSettings ws;

    private MainButtonsListener btnListener = new MainButtonsListener() {
        @Override
        public void onNavClick() {
            AlertDialog.Builder nBuilder = new AlertDialog.Builder(MainActivity.this);
            settingsBinding = DialogSettingsBinding.inflate(getLayoutInflater(), null, false);
            nBuilder.setView(settingsBinding.getRoot());
            setSettingsDialogListeners();
            settingsDialog = nBuilder.create();
            settingsDialog.show();

        }

        @Override
        public void onProfileClick() {
            openProfileFragment();
        }

        @Override
        public void onBookingClick() {
            fragmentIndex = 3;
            fragment = new BookingFragment(MainActivity.this, btnListener);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();
        }

        @Override
        public void onHomeClick() {
            fragmentIndex = 3;
            fragment = new NearbyClinicMapFragment(MainActivity.this, MainActivity.this, btnListener);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();
        }

        @Override
        public void onConsultClick() {
            fragmentIndex = 3;
            fragment = new ConsultFragment(MainActivity.this, btnListener);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();
        }

        @Override
        public void onLongItemClickBooking() {
        }

        @Override
        public void onItemClickBooking() {
        }

        @Override
        public void onChatClick() {
            fragmentIndex = 6;
            fragment = new ChatListFragment(MainActivity.this, btnListener);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();
        }
    };

    private void setSettingsDialogListeners() {
        settingsBinding.relLogout.setOnClickListener(v -> {
            AlertDialog.Builder nBuilder = new AlertDialog.Builder(MainActivity.this);
            DialogInterface.OnClickListener dListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        new MyUserPreferrence(MainActivity.this).saveUser(new Users());
                        settingsDialog.dismiss();
                        startFragment();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.cancel();
                        break;
                }
            };
            nBuilder.setMessage("Do you want to proceed signinig out?")
                    .setNegativeButton("Yes", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false);
            nBuilder.show();
        });
        settingsBinding.relTC.setOnClickListener(v -> {
            settingsDialog.dismiss();
            View sView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_terms, null, false);
            initializeTermDiag(sView);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setView(sView);
            dialog = mBuilder.create();
            dialog.show();
        });
    }

    private void initializeTermDiag(View sView) {
        wv = sView.findViewById(R.id.myweb);
        ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        wv.loadUrl("https://www.termsandconditionsgenerator.com/live.php?token=u1AIL7yApHsxy5rrqyfgNqJ4ijs6yEj2");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        initViews();
        ps = new ProgressDialog(MainActivity.this);
    }

    private void initViews() {
//        storage = new Storage(MainActivity.this);
        String[] buildingFilePaths = Constants.buildingFileFolder.split(",");

//        storage.getBuildingsFromStorage(buildingFilePaths[0]);

        startFragment();

    }


    private void startFragment() {
        fragmentIndex = 0;
        fragment = new FirstFragment(MainActivity.this, MainActivity.this);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, null);
        ft.commit();
    }

    @Override
    public void onFinishFirstFragment() {
        Users users = new MyUserPreferrence(MainActivity.this).getUsers();
        if (users.getDocID() != "") {

            if (users.getDocID().equals(BuildConfig.ADMIN_UUID)) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (users.getUserType() == 3) {
                    onDoctorLogin();
                } else {
                    onLoginFinish();
                }

            }


        } else {
            fragmentIndex = 1;
            fragment = new SecondFragment(MainActivity.this, MainActivity.this);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();


        }

    }

    @Override
    public void onFinishSecondFragment() {

        fragmentIndex = 2;
        fragment = new CreateAccountFragment(MainActivity.this, MainActivity.this, ps);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, null);
        ft.commit();
    }

    @Override
    public void onLoginFinish() {
        fragmentIndex = 3;

        Boolean isNotif = getIntent().getBooleanExtra("isNotif", false);
        if (isNotif) {
            btnListener.onBookingClick();
        } else {
            fragment = new NearbyClinicMapFragment(MainActivity.this, MainActivity.this, btnListener);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame, fragment, null);
            ft.commit();
        }

    }

    @Override
    public void openBuildingFragment(Buildings buildings) {
        fragmentIndex = 4;
        buildFragment = new BuildingFragment(MainActivity.this, buildings);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, buildFragment, null);
        ft.addToBackStack("building");
        ft.commit();
    }

    @Override
    public void openProfileFragment() {
        fragmentIndex = 5;
        fragment = new ProfileFragment(MainActivity.this, new ProfileListener() {
            @Override
            public void onBackPressed() {
                onLoginFinish();
            }
        });
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, null);
        ft.commit();
    }

    @Override
    public void onAdminFragment() {
        finish();
    }

    @Override
    public void onBackPressed() {
        switch (fragmentIndex) {
            case 1:
            case 3:
                super.onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 2:
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                onFinishFirstFragment();
                break;
            case 4:
                buildFragment.releasePlayer();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                fragmentIndex = 3;
                fragment = new NearbyClinicMapFragment(MainActivity.this, MainActivity.this, btnListener);
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.frame, fragment, null);
                ft.commit();
                break;
            case 6:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                onLoginFinish();
                break;
        }
    }

    @Override
    public void onDoctorLogin() {
        Intent intent = new Intent(MainActivity.this, DoctorActivity.class);
        startActivity(intent);
        finish();
    }
}