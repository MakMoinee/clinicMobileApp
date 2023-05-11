package com.sample.clinic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sample.clinic.Fragments.AdminHomeFragment;
import com.sample.clinic.Fragments.CategoriesFragment;
import com.sample.clinic.Fragments.DoctorProfilesFragment;
import com.sample.clinic.Interfaces.AdminListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.ActivityAdminBinding;
import com.sample.clinic.databinding.LayoutSearchBinding;

import java.util.List;

public class AdminActivity extends AppCompatActivity implements AdminListener {

    ActivityAdminBinding binding;

    LayoutSearchBinding layoutSearchBinding;
    Fragment fragment;
    FragmentTransaction ft;
    FragmentManager fm;

    Menu menu;
    int fragmentIndex = 0;

    AlertDialog alertSearch;
    ProgressDialog pd;
    LocalFirestore2 fs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        fs = new LocalFirestore2(AdminActivity.this);
        pd = new ProgressDialog(AdminActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
        fragment = new AdminHomeFragment(AdminActivity.this);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, null);
        ft.commit();
        binding.btnNav.setSelectedItemId(R.id.action_home);
        binding.btnNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragmentIndex = 0;
                    setTitle("Admin Dashboard");
                    fragment = new AdminHomeFragment(AdminActivity.this);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragment, fragment, null);
                    ft.commit();
                    return true;
                case R.id.action_categories:
                    setTitle("Categories");
                    fragmentIndex = 1;
                    fragment = new CategoriesFragment(AdminActivity.this, AdminActivity.this);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragment, fragment, null);
                    ft.commit();
                    return true;
                case R.id.action_settings:
                    fragmentIndex = 2;
                    onCreateOptionsMenu(menu);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminActivity.this);
                    DialogInterface.OnClickListener dListener = (dialog, which) -> {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                new MyUserPreferrence(AdminActivity.this).saveUser(new Users());
                                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    };
                    mBuilder.setMessage("Are You Want To Logout ?")
                            .setNegativeButton("Yes", dListener)
                            .setPositiveButton("No", dListener)
                            .setCancelable(false)
                            .show();
                    break;
            }
            return false;
        });
    }


    @Override
    public void onBackPressed(int index) {

    }


    private void setDialogSearchValues() {
        layoutSearchBinding.btnSearch.setOnClickListener(v -> {
            String doctorName = layoutSearchBinding.editSearchDoctorName.getText().toString();
            if (doctorName.equals("")) {
                Toast.makeText(this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {

                if (doctorName.equals("all")) {
                    setTitle("Doctor Profiles");
                    alertSearch.dismiss();
                    fragmentIndex = 1;
                    fragment = new DoctorProfilesFragment(AdminActivity.this, AdminActivity.this);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragment, fragment, null);
                    ft.commit();
                } else {
                    pd.show();
                    fs.searchDoctor(doctorName, new FireStoreListener() {
                        @Override
                        public void onSuccessDoctor(List<Doctor> doctorList) {
                            pd.dismiss();
                            setTitle("Doctor Profiles");
                            alertSearch.dismiss();
                            fragmentIndex = 1;
                            fragment = new DoctorProfilesFragment(AdminActivity.this, AdminActivity.this, doctorList);
                            fm = getSupportFragmentManager();
                            ft = fm.beginTransaction();
                            ft.replace(R.id.fragment, fragment, null);
                            ft.commit();
                        }

                        @Override
                        public void onError() {
                            pd.dismiss();
                            Toast.makeText(AdminActivity.this, "There are no such doctor name, Please Try Another Name", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
