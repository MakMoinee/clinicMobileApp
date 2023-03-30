package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.R;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    Context context;
    AdapterListener listener;
    List<Doctor> doctorList;

    public DoctorAdapter(Context context, AdapterListener listener, List<Doctor> doctorList) {
        this.context = context;
        this.listener = listener;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.txtDoctorName.setText(doctor.getDoctorName());
        holder.txtSpecialize.setText(doctor.getSpecialize());
        holder.txtContactNumber.setText(String.format("Contact Number: %s", doctor.getContactNumber()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Doctor doctor1 = doctorList.get(holder.getAbsoluteAdapterPosition());
                listener.onClick(doctor1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialize, txtContactNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txtDoctorName);
            txtSpecialize = itemView.findViewById(R.id.txtSpecialization);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
        }
    }
}
