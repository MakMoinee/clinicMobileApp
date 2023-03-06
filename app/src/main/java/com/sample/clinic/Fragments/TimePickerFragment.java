package com.sample.clinic.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog dialog;
    TimePickerDialog.OnTimeSetListener listener;
    Context mContext;

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener, Context mContext) {
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        dialog = new TimePickerDialog(mContext, listener, hour, minute, false);
        return dialog;
    }
}
