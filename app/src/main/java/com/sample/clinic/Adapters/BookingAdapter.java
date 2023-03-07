package com.sample.clinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.R;
import com.sample.clinic.databinding.ContentBookBinding;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    Context mContext;
    List<Bookings> bookingsList;

    ContentBookBinding binding;
    AdapterListener listener;


    public BookingAdapter(Context mContext, List<Bookings> bookingsList, AdapterListener l) {
        this.mContext = mContext;
        this.bookingsList = bookingsList;
        this.listener = l;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ContentBookBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        Bookings bookings = bookingsList.get(position);
        holder.txtBookDate.setText(String.format("Book Date: %s", bookings.getBookDate()));
        holder.txtBookTime.setText(String.format("Book Time: %s", bookings.getBookTime()));
        holder.txtClientName.setText(String.format("Client Name: %s", bookings.getClientName()));
        holder.txtAddress.setText(String.format("Address: %s", bookings.getAddress()));
        holder.txtStatus.setText(String.format("%s", bookings.getStatus()));
        if (bookings.getStatus().equalsIgnoreCase("active") || bookings.getStatus().equalsIgnoreCase("done")) {
            holder.txtStatus.setTextColor(Color.GREEN);
        } else if (bookings.getStatus().equalsIgnoreCase("inactive")) {
            holder.txtStatus.setTextColor(Color.RED);
        } else if (bookings.getStatus().equalsIgnoreCase("cancelled")) {
            holder.txtStatus.setTextColor(Color.YELLOW);
        }
        holder.itemView.setOnLongClickListener(v -> {
            Bookings bookings1 = bookingsList.get(holder.getAbsoluteAdapterPosition());
            listener.onLongPress(bookings1);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBookDate, txtBookTime, txtClientName, txtAddress, txtStatus;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBookDate = itemView.findViewById(R.id.txtBookDate);
            txtBookTime = itemView.findViewById(R.id.txtBookTime);
            txtClientName = itemView.findViewById(R.id.txtClientName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            card = itemView.findViewById(R.id.card);
        }
    }


}
