package com.sample.clinic.Adapters.doctor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Users;
import com.sample.clinic.R;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
            holder.txtAppointmentDate.setText(String.format("Appointment Date: \n%s", appointment.getBookDateTime()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
            try {
                Date convertedDate = dateFormat.parse(appointment.getBookDateTime());
                long diffInMillies = Math.abs(System.currentTimeMillis() - convertedDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diff <= 0) {
                    holder.txtStatus.setTextColor(Color.RED);
                    holder.txtStatus.setText("Status: Invalid");
                }else{
                    holder.txtStatus.setTextColor(Color.GREEN);
                    holder.txtStatus.setText("Status: Valid");
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPatientName, txtAppointmentDate,txtStatus;
        ImageButton imgChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPatientName = itemView.findViewById(R.id.txtPatientName);
            txtAppointmentDate = itemView.findViewById(R.id.txtAppointmentDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgChat = itemView.findViewById(R.id.imgChat);
        }
    }
}
