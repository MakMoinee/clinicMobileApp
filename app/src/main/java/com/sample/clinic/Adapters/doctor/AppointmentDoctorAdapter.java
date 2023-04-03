package com.sample.clinic.Adapters.doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Users;
import com.sample.clinic.R;

import java.util.List;

public class AppointmentDoctorAdapter extends RecyclerView.Adapter<AppointmentDoctorAdapter.ViewHolder> {

    Context mContext;
    List<Appointment> appointmentList;

    List<Users> usersList;
    AdapterListener listener;

    public AppointmentDoctorAdapter(Context mContext, List<Appointment> appointmentList, List<Users> usersList, AdapterListener listener) {
        this.mContext = mContext;
        this.appointmentList = appointmentList;
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_doctor_appointments, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDoctorAdapter.ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        Users users = new Users();
        Boolean isUserFound = false;
        for (Users user : usersList) {
            if (user.getDocID().equals(appointment.getUserID())) {
                users = user;
                isUserFound = true;
                break;
            }
        }

        if (isUserFound) {
            holder.txtPatientName.setText(String.format("%s, %s %s", users.getLastName(), users.getFirstName(), users.getMiddleName()));
        }

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPatientName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPatientName = itemView.findViewById(R.id.txtPatientName);
        }
    }
}
