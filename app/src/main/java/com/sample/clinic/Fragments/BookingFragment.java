package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.sample.clinic.Adapters.BookingAdapter;
import com.sample.clinic.EditBookingActivity;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.FragmentBookingBinding;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class BookingFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    FragmentBookingBinding binding;
    Context mContext;
    MainButtonsListener mainButtonsListener;
    DatePickerFragment dpDate;
    String selectedDate = "";
    LocalFirestore2 fs;
    BookingAdapter adapter;
    ProgressDialog pd;

    AlertDialog bookOptionsAlert;

    Button btnEditBooking, btnDeleteBooking;
    Bookings selectedBook;

    public BookingFragment(Context c, MainButtonsListener l) {
        this.mContext = c;
        this.mainButtonsListener = l;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(getLayoutInflater(), container, false);
        setValues();
        loadData();
        return binding.getRoot();
    }

    private void loadData() {

        pd = new ProgressDialog(mContext);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(true);
        pd.show();
        getAllBooking();
    }

    private void getAllBooking() {
        binding.recycler.setAdapter(null);
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getAllBookings(users.getDocID(), new FireStoreListener() {

            @Override
            public void onSuccess(List<Bookings> bookingsList) {
                pd.dismiss();
                binding.recycler.setAdapter(null);
                adapter = new BookingAdapter(mContext, bookingsList, new AdapterListener() {
                    @Override
                    public void onLongPress(Bookings bookings) {
                        selectedBook = bookings;
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_book_edit_delete, null, false);
                        mBuilder.setView(mView);
                        initBookingOptionsDialogViews(mView);
                        initBookingOptionsDialogListeners();
                        bookOptionsAlert = mBuilder.create();
                        bookOptionsAlert.show();
                    }

                });
                binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                binding.recycler.setItemAnimator(new DefaultItemAnimator());
                binding.recycler.setAdapter(adapter);
            }

            @Override
            public void onError() {
                pd.dismiss();
                Toast.makeText(mContext, "There are no booking records as of the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initBookingOptionsDialogListeners() {
        btnEditBooking.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditBookingActivity.class);
            intent.putExtra("bookingRaw", new Gson().toJson(selectedBook));
            mContext.startActivity(intent);
            bookOptionsAlert.dismiss();
        });
        btnDeleteBooking.setOnClickListener(v -> {
            pd.setCancelable(false);
            pd.show();
            fs.deleteBooking(selectedBook.getDocID(), new FireStoreListener() {
                @Override
                public void onSuccess() {
                    pd.dismiss();
                    pd.setCancelable(true);
                    Intent intent = new Intent("com.sample.clinic.REMINDER");
                    intent.putExtra("notification_id", (selectedBook.getNotifID() * -1));
                    mContext.sendBroadcast(intent);
                    Toast.makeText(mContext, "Succesfully Deleted Booking", Toast.LENGTH_SHORT).show();
                    bookOptionsAlert.dismiss();
                    binding.recycler.setAdapter(null);
                    loadData();
                }

                @Override
                public void onError() {
                    pd.dismiss();
                    pd.setCancelable(true);
                    Toast.makeText(mContext, "Failed to delete booking", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initBookingOptionsDialogViews(View mView) {
        btnEditBooking = mView.findViewById(R.id.btnEditBooking);
        btnDeleteBooking = mView.findViewById(R.id.btnDelete);
    }

    private void setValues() {
        fs = new LocalFirestore2(mContext);
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    mainButtonsListener.onHomeClick();
                    break;
                case R.id.action_settings:
                    mainButtonsListener.onNavClick();
                    break;

            }
            return false;
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

    @Override
    public void onResume() {
        super.onResume();
        pd.show();
        getAllBooking();
    }
}
