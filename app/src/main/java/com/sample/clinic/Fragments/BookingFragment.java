package com.sample.clinic.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.R;
import com.sample.clinic.databinding.FragmentBookingBinding;

import java.text.DateFormat;
import java.util.Calendar;

public class BookingFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    FragmentBookingBinding binding;
    Context mContext;
    MainButtonsListener mainButtonsListener;
    DatePickerFragment dpDate;
    String selectedDate = "";

    public BookingFragment(Context c, MainButtonsListener l) {
        this.mContext = c;
        this.mainButtonsListener = l;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(getLayoutInflater(), container, false);
        setValues();
        return binding.getRoot();
    }

    private void setValues() {
        binding.navBottom.setSelectedItemId(R.id.action_booking);
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    mainButtonsListener.onHomeClick();
                    break;
                case R.id.action_consult:
                    mainButtonsListener.onConsultClick();
                    break;

                case R.id.action_settings:
                    mainButtonsListener.onNavClick();
                    break;

            }
            return false;
        });
        binding.navBottom.setSelectedItemId(R.id.action_booking);

        binding.btnAddBook.setOnClickListener(v -> {
            dpDate = new DatePickerFragment(this);
            dpDate.show(getActivity().getSupportFragmentManager(), "DATE PICK");

        });


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());

        if (selectedDate != null && selectedDate != "") {

        }
//        tvDate.setText(selectedDate);
    }
}
