package com.sample.clinic;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Fragments.DatePickerFragment;
import com.sample.clinic.Fragments.TimePickerFragment;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.ReminderNotif;
import com.sample.clinic.databinding.ActivityEditBookingBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditBookingActivity extends AppCompatActivity {
    ActivityEditBookingBinding binding;
    Bookings bookings;
    DatePickerFragment dpDate;
    String selectedDate = "", selectedTime = "";
    TimePickerFragment tpTime;

    LocalFirestore2 fs;
    String dateTimeStr = "";
    long timeInMilli = 0;
    NearPlacesResponse currentHospital;
    ProgressDialog pd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String bookingRaw = getIntent().getStringExtra("bookingRaw");
        bookings = new Gson().fromJson(bookingRaw, new TypeToken<Bookings>() {
        }.getType());
        setValues();
    }

    private void setValues() {
        fs = new LocalFirestore2(EditBookingActivity.this);
        pd = new ProgressDialog(EditBookingActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
        if (bookings != null) {
            binding.editName.setText(bookings.getClientName());
            binding.editAddress.setText(bookings.getAddress());
            binding.editMedicalHistory.setText(bookings.getMedicalHistory());
            binding.txtBookDate.setText(String.format("Book Date: %s", bookings.getBookDate()));
            binding.txtBookTime.setText(String.format("Book Time: %s", bookings.getBookTime()));
            currentHospital = new Gson().fromJson(bookings.getHospitalDataRaw(), new TypeToken<NearPlacesResponse>() {
            }.getType());
            selectedDate = bookings.getBookDate();

            binding.btnSetBookDate.setOnClickListener(v -> {
                dpDate = new DatePickerFragment((view, year, month, dayOfMonth) -> {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.YEAR, year);
                    mCalendar.set(Calendar.MONTH, month);
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());

                    if (selectedDate != null && selectedDate != "") {
                        tpTime = new TimePickerFragment((view1, hourOfDay, minute) -> {
                            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mCalendar.set(Calendar.MINUTE, minute);
                            String minuteStr = "";
                            if (minute < 10) {
                                minuteStr = String.format("0%s", minute);
                            } else {
                                minuteStr = String.format("%s", minute);
                            }
                            if (hourOfDay > 12) {
                                selectedTime = String.format("%s:%s pm", (hourOfDay - 12), minuteStr);
                            } else {
                                selectedTime = String.format("%s:%s am", hourOfDay, minuteStr);
                            }
                            binding.txtBookDate.setText(String.format("Book Date: %s", selectedDate));
                            binding.txtBookTime.setText(String.format("Book Time: %s", selectedTime));

                        }, EditBookingActivity.this);
                        timeInMilli = mCalendar.getTimeInMillis();
                        tpTime.show(getSupportFragmentManager(), "TIME PICK");
                    } else {
                        timeInMilli = mCalendar.getTimeInMillis();
                    }
                });
                dpDate.show(getSupportFragmentManager(), "DATE PICK");
            });

            binding.btnUpdateBook.setOnClickListener(v -> {
                String name = binding.editName.getText().toString();
                String address = binding.editAddress.getText().toString();
                String medicalHistory = binding.editMedicalHistory.getText().toString();
                if (name.equals("") || address.equals("") || medicalHistory.equals("")) {
                    Toast.makeText(EditBookingActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    dateTimeStr = "";
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timeInMilli);
                        dateTimeStr = format.format(calendar.getTime());
                    } catch (Exception e) {
                        Log.e("ERROR_PARSING_DATE", e.getMessage());

                    }


                    Bookings b = new Bookings.BookingsBuilder(name)
                            .setAddress(address)
                            .setMedicalHistory(medicalHistory)
                            .setUserID(bookings.getUserID())
                            .setBookDate(selectedDate)
                            .setBookTime(selectedTime)
                            .setHospitalDataRaw(bookings.getHospitalDataRaw())
                            .setNotifID(bookings.getNotifID())
                            .build();
                    b.setDocID(bookings.getDocID());
                    fs.updateBooking(b, new FireStoreListener() {
                        @Override
                        public void onSuccess() {
                            pd.dismiss();
                            showNotifs(bookings.getNotifID(), timeInMilli, String.format("Booked %s at %s", currentHospital.getName(), dateTimeStr));
                            Toast.makeText(EditBookingActivity.this, "Successfully Updated Booking", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError() {
                            pd.dismiss();
                            Toast.makeText(EditBookingActivity.this, "Failed to update booking, Please Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @SuppressLint("NewApi")
    private void showNotifs(int notifID, long notificationTime, String notificationText) {
        Intent notificationIntent = new Intent(EditBookingActivity.this, ReminderNotif.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_ID, notifID);
        notificationIntent.putExtra(ReminderNotif.NOTIFICATION_TEXT, notificationText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notifID, notificationIntent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

}
