package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sample.clinic.Adapters.DoctorAdapter;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.AdminListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.DialogAddDoctorBinding;
import com.sample.clinic.databinding.DialogEditDoctorBinding;
import com.sample.clinic.databinding.FragmentDoctorProfilesBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfilesFragment extends Fragment {

    Context mContext;

    FragmentDoctorProfilesBinding binding;
    AdminListener listener;
    AlertDialog alertAddDoctor, alertUpdateDoctor;

    DialogAddDoctorBinding addDoctorBinding;
    DialogEditDoctorBinding editDoctorBinding;

    ProgressDialog pd;
    LocalFirestore2 fs;
    List<Doctor> doctorList = new ArrayList<>();
    ;
    DoctorAdapter adapter;


    public DoctorProfilesFragment(Context mContext, AdminListener l) {
        this.mContext = mContext;
        this.listener = l;
    }

    public DoctorProfilesFragment(Context mContext, AdminListener l, List<Doctor> d) {
        this.mContext = mContext;
        this.listener = l;
        this.doctorList = d;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorProfilesBinding.inflate(LayoutInflater.from(mContext), container, false);
        setValues();
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnAddDoctor.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            addDoctorBinding = DialogAddDoctorBinding.inflate(getLayoutInflater(), null, false);
            setDialogListeners();
            mBuilder.setView(addDoctorBinding.getRoot());
            alertAddDoctor = mBuilder.create();
            alertAddDoctor.setCancelable(false);
            alertAddDoctor.show();
        });
    }

    private void setDialogListeners() {

        addDoctorBinding.btnAdd.setOnClickListener(v -> {
            String name = addDoctorBinding.editDoctorName.getText().toString();
            String specialize = addDoctorBinding.editSpecialization.getText().toString();
            String address = addDoctorBinding.editAddress.getText().toString();
            String contactNumber = addDoctorBinding.editContactNumber.getText().toString();
            String username = addDoctorBinding.editUsername.getText().toString();
            String password = addDoctorBinding.editPassword.getText().toString();
            String confirmPass = addDoctorBinding.editConfirmPassword.getText().toString();
            String secret = addDoctorBinding.editSecret.getText().toString();

            if (name.equals("") || specialize.equals("") || address.equals("") || contactNumber.equals("") || username.equals("") || password.equals("") || confirmPass.equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals(confirmPass)) {
                    pd.show();
                    Users users = new Users();
                    users.setEmail(username);
                    users.setPassword(password);
                    users.setUserType(3);
                    users.setSecret(secret);

                    fs.addDoctorUser(users, new FireStoreListener() {
                        @Override
                        public void onAddUserSuccess(Users users) {
                            Doctor doctor = new Doctor.DoctorBuilder()
                                    .setDocID(users.getDocID())
                                    .setDoctorName(name)
                                    .setSpecialize(specialize)
                                    .setAddress(address)
                                    .setContactNumber(contactNumber)
                                    .build();
                            fs.addDoctor(doctor, new FireStoreListener() {
                                @Override
                                public void onSuccess() {
                                    pd.dismiss();
                                    alertAddDoctor.dismiss();
                                    Toast.makeText(mContext, "Successfully added doctor", Toast.LENGTH_SHORT).show();
                                    binding.recycler.setAdapter(null);
                                    loadData();
                                }

                                @Override
                                public void onError() {
                                    pd.dismiss();
                                    Toast.makeText(mContext, "Failed to add doctor", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(mContext, "Failed to add doctor", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(mContext, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                }

            }

        });
        addDoctorBinding.btnCancel.setOnClickListener(v -> alertAddDoctor.dismiss());
    }

    private void loadData() {
        fs.getDoctors(new FireStoreListener() {
            @Override
            public void onSuccessDoctor(List<Doctor> doctorList) {
                adapter = new DoctorAdapter(mContext, new AdapterListener() {
                    @Override
                    public void onClick(Doctor doctor) {
                        Log.e("DOCTOR_SELECTED", doctor.toString());
                        AlertDialog.Builder sBuilder = new AlertDialog.Builder(mContext);
                        editDoctorBinding = DialogEditDoctorBinding.inflate(LayoutInflater.from(mContext), null, false);
                        setValuesInUpdateDialog(doctor);
                        setUpdateDialogListeners(doctor);
                        sBuilder.setView(editDoctorBinding.getRoot());
                        alertUpdateDoctor = sBuilder.create();
                        alertUpdateDoctor.show();
                    }
                }, doctorList);
                binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                binding.recycler.setAdapter(adapter);
            }

            @Override
            public void onError() {
                Toast.makeText(mContext, "There are no doctors available as of the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpdateDialogListeners(Doctor origDoctorData) {
        editDoctorBinding.btnUpdate.setOnClickListener(v -> {
            String name = editDoctorBinding.editDoctorName.getText().toString();
            String specialize = editDoctorBinding.editSpecialization.getText().toString();
            String address = editDoctorBinding.editAddress.getText().toString();
            String contactNumber = editDoctorBinding.editContactNumber.getText().toString();

            if (name.equals("") || specialize.equals("") || address.equals("") || contactNumber.equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                pd.show();
                Doctor newDoctor = new Doctor.DoctorBuilder()
                        .setDoctorName(name)
                        .setSpecialize(specialize)
                        .setAddress(address)
                        .setContactNumber(contactNumber)
                        .setDocID(origDoctorData.getDocID())
                        .build();

                fs.updateDoctor(newDoctor, new FireStoreListener() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        alertUpdateDoctor.dismiss();
                        Toast.makeText(mContext, "Successfully Updated Doctor's Profile", Toast.LENGTH_SHORT).show();
                        binding.recycler.setAdapter(null);
                        loadData();
                    }

                    @Override
                    public void onError() {
                        pd.dismiss();
                        Toast.makeText(mContext, "Failed to update Doctor's Profile", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        editDoctorBinding.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder dBuilder = new AlertDialog.Builder(mContext);
            DialogInterface.OnClickListener dListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        pd.show();
                        fs.deleteDoctor(origDoctorData.getDocID(), new FireStoreListener() {
                            @Override
                            public void onSuccess() {
                                pd.dismiss();
                                alertUpdateDoctor.dismiss();
                                Toast.makeText(mContext, "Successfully Deleted Doctor's Record", Toast.LENGTH_SHORT).show();
                                binding.recycler.setAdapter(null);
                                loadData();
                            }

                            @Override
                            public void onError() {
                                pd.dismiss();
                                Toast.makeText(mContext, "Failed to delete Doctor's profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            };
            dBuilder.setMessage("Are You Want To Delete This Doctor's Record?")
                    .setNegativeButton("Yes", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false)
                    .show();

        });
    }

    private void setValuesInUpdateDialog(Doctor doctor) {
        editDoctorBinding.editDoctorName.setText(doctor.getDoctorName());
        editDoctorBinding.editSpecialization.setText(doctor.getSpecialize());
        editDoctorBinding.editAddress.setText(doctor.getAddress());
        editDoctorBinding.editContactNumber.setText(doctor.getContactNumber());
    }

    private void setValues() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
        fs = new LocalFirestore2(mContext);
        if (doctorList.size() > 0) {
            adapter = new DoctorAdapter(mContext, new AdapterListener() {
                @Override
                public void onClick(Doctor doctor) {
                    AlertDialog.Builder sBuilder = new AlertDialog.Builder(mContext);
                    editDoctorBinding = DialogEditDoctorBinding.inflate(LayoutInflater.from(mContext), null, false);
                    setValuesInUpdateDialog(doctor);
                    setUpdateDialogListeners(doctor);
                    sBuilder.setView(editDoctorBinding.getRoot());
                    alertUpdateDoctor = sBuilder.create();
                    alertUpdateDoctor.show();
                }
            }, doctorList);
            binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
            binding.recycler.setAdapter(adapter);
        } else {
            doctorList = new ArrayList<>();
            loadData();
        }
    }


}
