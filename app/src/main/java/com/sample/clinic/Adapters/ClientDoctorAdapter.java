package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.R;

import java.util.List;

public class ClientDoctorAdapter extends RecyclerView.Adapter<ClientDoctorAdapter.ViewHolder> {

    Context context;
    AdapterListener listener;
    List<Doctor> doctorList;

    public ClientDoctorAdapter(Context context, AdapterListener listener, List<Doctor> doctorList) {
        this.context = context;
        this.listener = listener;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public ClientDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientDoctorAdapter.ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.txtDoctorName.setText(doctor.getDoctorName());
        holder.txtSpecialize.setText(doctor.getSpecialize());
        holder.txtContactNumber.setText(String.format("Contact Number: %s", doctor.getContactNumber()));
        holder.itemView.setOnClickListener(v -> {
            Doctor doctor1 = doctorList.get(holder.getAbsoluteAdapterPosition());
            listener.onClick(doctor1);
        });
        holder.btnChat.setVisibility(View.VISIBLE);
        holder.btnChat.setOnClickListener(v -> {
            listener.onChatClick(holder.getAbsoluteAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialize, txtContactNumber;
        ImageButton btnChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txtDoctorName);
            txtSpecialize = itemView.findViewById(R.id.txtSpecialization);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
            btnChat = itemView.findViewById(R.id.btnChat);
        }
    }
}
