package com.sample.clinic;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.LocalMail;
import com.sample.clinic.Services.ReminderNotif;
import com.sample.clinic.databinding.ActivityFillUpBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FillUpInfoActivity extends AppCompatActivity {

    ActivityFillUpBinding binding;
    LocalFirestore2 fs;
    ProgressDialog pd;
    String dateTimeStr = "";
    LocalMail mail = new LocalMail();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFillUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setValues();
        setListeners();
    }

    private void setValues() {
        fs = new LocalFirestore2(FillUpInfoActivity.this);
        pd = new ProgressDialog(FillUpInfoActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
    }

    private void setListeners() {
        binding.btnSaveBook.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String address = binding.editAddress.getText().toString();
            String medicalHistory = binding.editMedicalHistory.getText().toString();
            if (name.equals("") || address.equals("") || medicalHistory.equals("")) {
                Toast.makeText(FillUpInfoActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                pd.show();
                String selectedDate = getIntent().getStringExtra("selectedDate");
                String selectedTime = getIntent().getStringExtra("selectedTime");
                String hospitalName = getIntent().getStringExtra("hospitalName");
                String hospitalDataRaw = getIntent().getStringExtra("hospitalDataRaw");

                long timeInMilli = getIntent().getLongExtra("timeInMillis", 0);
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                dateTimeStr = "";
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeInMilli);
                    dateTimeStr = format.format(calendar.getTime());
                } catch (Exception e) {
                    Log.e("ERROR_PARSING_DATE", e.getMessage());

                }
                Users users = new MyUserPreferrence(FillUpInfoActivity.this).getUsers();
                Random randI = new Random();
                int myRandInt = randI.nextInt(100);
                int randomInt = myRandInt + 1;
                Bookings bookings = new Bookings.BookingsBuilder(name)
                        .setBookDate(selectedDate)
                        .setBookTime(selectedTime)
                        .setAddress(address)
                        .setUserID(users.getDocID())
                        .setHospitalDataRaw(hospitalDataRaw)
                        .setMedicalHistory(medicalHistory)
                        .setNotifID(randomInt)
                        .build();
                fs.addBooking(bookings, new FireStoreListener() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        showNotifs(bookings.getNotifID(), timeInMilli, String.format("Booked %s at %s", hospitalName, dateTimeStr));
                        mail.sendEmail(users.getEmail(), "PQ MEDFIND - CREATED BOOK", String.format("Hi %s,\nYou have successfully create a book with the clinic/hospital named: %s, Dated: %s\n\n\n\nThank You So Much For Using PQ MEDFIND", users.getFirstName(), hospitalName, (selectedDate + " " + selectedTime)));
                        Toast.makeText(FillUpInfoActivity.this, "Successfully Added Booking", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError() {
                        pd.dismiss();
                        Toast.makeText(FillUpInfoActivity.this, "Failed to add booking", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void showNotifs(int notifID, long notificationTime, String notificationText) {
        Intent notificationIntent = new Intent(FillUpInfoActivity.this, ReminderNotif.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_ID, notifID);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_TEXT, notificationText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notifID, notificationIntent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }
}
