package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context mContext;
    List<Message2> list;
    AdapterListener listener;

    public ChatListAdapter(Context mContext, List<Message2> list, AdapterListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        Message2 msg = list.get(position);
        holder.txtDoctorName.setText(msg.getDoctorName());
        String pattern = "yyyy-MM-dd hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        holder.txtChatDate.setText(simpleDateFormat.format(msg.getChatDateTime()));
        holder.itemView.setOnClickListener(v -> listener.onChatClick(holder.getAbsoluteAdapterPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(holder.getAbsoluteAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtChatDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txtChatName);
            txtChatDate = itemView.findViewById(R.id.txtChatDate);

        }
    }
}
