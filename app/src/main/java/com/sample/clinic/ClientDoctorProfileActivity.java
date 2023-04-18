package com.sample.clinic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.sample.clinic.Adapters.ClientDoctorAdapter;
import com.sample.clinic.Adapters.DoctorAdapter;
import com.sample.clinic.Fragments.DatePickerFragment;
import com.sample.clinic.Fragments.TimePickerFragment;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.ActivityClientDoctorProfilesBinding;
import com.sample.clinic.databinding.DialogShowDoctorBinding;
import com.sample.clinic.databinding.LayoutSearchBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ClientDoctorProfileActivity extends AppCompatActivity {

    ActivityClientDoctorProfilesBinding binding;
    LayoutSearchBinding layoutSearchBinding;
    LocalFirestore2 fs;
    AlertDialog alertSearch;
    ProgressDialog pd;
    ClientDoctorAdapter adapter;

    DialogShowDoctorBinding showDoctorBinding;

    AlertDialog alertShowDoctorDetails;
    Doctor selectedDoctor;

    DatePickerFragment dpDate;
    TimePickerFragment tpTime;
    List<Doctor> doctorList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientDoctorProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Doctor Profiles");
        setValues();
    }

    private void setValues() {
        fs = new LocalFirestore2(ClientDoctorProfileActivity.this);
        pd = new ProgressDialog(ClientDoctorProfileActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);

        loadData();
    }

    private void loadData() {
        fs.getDoctors(new FireStoreListener() {
            @Override
            public void onSuccessDoctor(List<Doctor> d) {
                doctorList = d;
                adapter = new ClientDoctorAdapter(ClientDoctorProfileActivity.this, new AdapterListener() {
                    @Override
                    public void onClick(Doctor doctor) {
                        Log.e("DOCTOR_SELECTED", doctor.toString());
                        selectedDoctor = doctor;
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClientDoctorProfileActivity.this);
                        showDoctorBinding = DialogShowDoctorBinding.inflate(getLayoutInflater(), null, false);
                        setDialogShowValues();
                        mBuilder.setView(showDoctorBinding.getRoot());
                        alertShowDoctorDetails = mBuilder.create();
                        alertShowDoctorDetails.show();
                    }

                    @Override
                    public void onChatClick(int position) {
                        Doctor doctor = doctorList.get(position);
                        String rawDoctor = new Gson().toJson(doctor);
                        Intent intent = new Intent(ClientDoctorProfileActivity.this, ChatActivity.class);
                        intent.putExtra("rawDoctor", rawDoctor);
                        startActivity(intent);
                    }
                }, doctorList);
                binding.recycler.setLayoutManager(new LinearLayoutManager(ClientDoctorProfileActivity.this));
                binding.recycler.setAdapter(adapter);
            }

            @Override
            public void onError() {
                Toast.makeText(ClientDoctorProfileActivity.this, "There are no active doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDialogShowValues() {
        showDoctorBinding.editDoctorName.setText(selectedDoctor.getDoctorName());
        showDoctorBinding.editAddress.setText(selectedDoctor.getAddress());
        showDoctorBinding.editSpecialization.setText(selectedDoctor.getSpecialize());
        showDoctorBinding.editContactNumber.setText(selectedDoctor.getContactNumber());

        showDoctorBinding.btnCreateAppointment.setOnClickListener(v -> {
            String clientContactNumber = showDoctorBinding.editClientContact.getText().toString();
            if (clientContactNumber.equals("")) {
                Toast.makeText(ClientDoctorProfileActivity.this, "Please Fill The Client Contact Number", Toast.LENGTH_SHORT).show();
            } else {
                dpDate = new DatePickerFragment((view, year, month, dayOfMonth) -> {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.YEAR, year);
                    mCalendar.set(Calendar.MONTH, month);
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    tpTime = new TimePickerFragment((view1, hourOfDay, minute) -> {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat dfFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        try {
                            String bookDateStr = dfFormat.format(mCalendar.getTime());
                            Users users = new MyUserPreferrence(ClientDoctorProfileActivity.this).getUsers();
                            Random randI = new Random();
                            int myRandInt = randI.nextInt(100);
                            int randomInt = myRandInt + 1;
                            Appointment ap = new Appointment.AppointmentBuilder()
                                    .setDoctorName(selectedDoctor.getDoctorName())
                                    .setUserID(users.getDocID())
                                    .setBookDateTime(bookDateStr)
                                    .setNotifID(randomInt)
                                    .setClientContactNumber(clientContactNumber)
                                    .setDoctorID(selectedDoctor.getDocID())
                                    .build();

                            fs.addAppointment(ap, new FireStoreListener() {
                                @Override
                                public void onSuccess() {
                                    alertShowDoctorDetails.dismiss();
                                    Toast.makeText(ClientDoctorProfileActivity.this, "Successfully Created Appointment", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(ClientDoctorProfileActivity.this, "Failed to create appointment", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Log.e("ERROR_PARSE_DATE", e.getMessage());
                        }
                    }, ClientDoctorProfileActivity.this);
                    tpTime.show(getSupportFragmentManager(), "TIME_PICK");
                });

                dpDate.show(getSupportFragmentManager(), "DATE_PICK");
            }


        });
        showDoctorBinding.btnCancel.setOnClickListener(v -> alertShowDoctorDetails.dismiss());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClientDoctorProfileActivity.this);
                layoutSearchBinding = LayoutSearchBinding.inflate(getLayoutInflater(), null, false);
                setDialogSearchValues();
                mBuilder.setView(layoutSearchBinding.getRoot());
                alertSearch = mBuilder.create();
                alertSearch.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
                    binding.recycler.setAdapter(null);
                    loadData();
                } else {
                    pd.show();
                    binding.recycler.setAdapter(null);
                    fs.searchDoctor(doctorName, new FireStoreListener() {
                        @Override
                        public void onSuccessDoctor(List<Doctor> d) {
                            doctorList = d;
                            adapter = new ClientDoctorAdapter(ClientDoctorProfileActivity.this, new AdapterListener() {
                                @Override
                                public void onClick(Doctor doctor) {
                                    selectedDoctor = doctor;
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClientDoctorProfileActivity.this);
                                    showDoctorBinding = DialogShowDoctorBinding.inflate(getLayoutInflater(), null, false);
                                    setDialogShowValues();
                                    mBuilder.setView(showDoctorBinding.getRoot());
                                    alertShowDoctorDetails = mBuilder.create();
                                    alertShowDoctorDetails.show();
                                }

                                @Override
                                public void onChatClick(int position) {
                                    Doctor doctor = doctorList.get(position);
                                    String rawDoctor = new Gson().toJson(doctor);
                                    Intent intent = new Intent(ClientDoctorProfileActivity.this, ChatActivity.class);
                                    intent.putExtra("rawDoctor", rawDoctor);
                                    startActivity(intent);
                                }
                            }, doctorList);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(ClientDoctorProfileActivity.this, String.format("There are no such name as %s on the doctor's record of app", doctorName), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
