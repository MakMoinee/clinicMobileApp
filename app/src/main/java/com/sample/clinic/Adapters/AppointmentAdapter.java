package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    Context mContext;
    List<Appointment> appointmentList;
    AdapterListener listener;

    public AppointmentAdapter(Context mContext, List<Appointment> appointmentList, AdapterListener listener) {
        this.mContext = mContext;
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ViewHolder holder, int position) {
        Appointment ap = appointmentList.get(position);
        holder.txtDoctorName.setText(ap.getDoctorName());
        holder.txtSpecialize.setVisibility(View.INVISIBLE);
        holder.txtBookDate.setText(String.format("Book Date & Time: %s", ap.getBookDateTime()));

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialize, txtBookDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txtDoctorName);
            txtSpecialize = itemView.findViewById(R.id.txtSpecialization);
            txtBookDate = itemView.findViewById(R.id.txtBookDate);
        }
    }
}
