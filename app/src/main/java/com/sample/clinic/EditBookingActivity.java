package com.sample.clinic;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Fragments.DatePickerFragment;
import com.sample.clinic.Fragments.TimePickerFragment;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.databinding.ActivityEditBookingBinding;

import java.text.DateFormat;
import java.util.Calendar;

public class EditBookingActivity extends AppCompatActivity {
    ActivityEditBookingBinding binding;
    Bookings bookings;
    DatePickerFragment dpDate;
    String selectedDate = "", selectedTime = "";
    TimePickerFragment tpTime;


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
        if (bookings != null) {
            binding.editName.setText(bookings.getClientName());
            binding.editAddress.setText(bookings.getAddress());
            binding.editMedicalHistory.setText(bookings.getMedicalHistory());
            binding.txtBookDate.setText(String.format("Book Date: %s", bookings.getBookDate()));
            binding.txtBookTime.setText(String.format("Book Time: %s", bookings.getBookTime()));
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
                            if (hourOfDay > 12) {
                                selectedTime = String.format("%s:%s pm", (hourOfDay - 12), minute);
                            } else {
                                selectedTime = String.format("%s:%s am", hourOfDay, minute);
                            }
                            binding.txtBookDate.setText(String.format("Book Date: %s", selectedDate));
                            binding.txtBookTime.setText(String.format("Book Time: %s", selectedTime));

                        }, EditBookingActivity.this);
                        tpTime.show(getSupportFragmentManager(), "TIME PICK");
                    }
                });
                dpDate.show(getSupportFragmentManager(), "DATE PICK");
            });
        }
    }

}
