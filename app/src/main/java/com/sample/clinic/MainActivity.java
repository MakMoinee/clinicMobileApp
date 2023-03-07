package com.sample.clinic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sample.clinic.Common.Constants;
import com.sample.clinic.Fragments.BookingFragment;
import com.sample.clinic.Fragments.BuildingFragment;
import com.sample.clinic.Fragments.ConsultFragment;
import com.sample.clinic.Fragments.CreateAccountFragment;
import com.sample.clinic.Fragments.FirstFragment;
import com.sample.clinic.Fragments.MainFormFragment;
import com.sample.clinic.Fragments.ProfileFragment;
import com.sample.clinic.Fragments.SecondFragment;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Interfaces.ProfileListener;
import com.sample.clinic.Interfaces.StorageListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.Storage;

import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentFinish {

    private Fragment fragment;
    private BuildingFragment buildFragment;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private int fragmentIndex = 0;
    private ProgressDialog ps;

    private List<Buildings> buildings;

    Menu mainActivityMenu;

    private MainButtonsListener btnListener = new MainButtonsListener() {
        @Override
        public void onNavClick() {
            AlertDialog.Builder nBuilder = new AlertDialog.Builder(MainActivity.this);
            DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            new MyUserPreferrence(MainActivity.this).saveUser(new Users());
                            startFragment();
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            nBuilder.setMessage("Do you want to proceed signinig out?")
                    .setNegativeButton("Yes", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false);
            nBuilder.show();
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
            fragment = new MainFormFragment(MainActivity.this, MainActivity.this, false, btnListener);
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
    };

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
            onLoginFinish();
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
        fragment = new MainFormFragment(MainActivity.this, MainActivity.this, false, btnListener);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, null);
        ft.commit();
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
                fragment = new MainFormFragment(MainActivity.this, MainActivity.this, true, btnListener);
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.frame, fragment, null);
                ft.commit();
                break;

        }
    }

}