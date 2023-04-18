package com.sample.clinic.Adapters.doctor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Communicate;
import com.sample.clinic.Models.Message;
import com.sample.clinic.R;

import java.util.List;

public class DoctorChatConvoAdapter extends RecyclerView.Adapter<DoctorChatConvoAdapter.ViewHolder> {

    Context mContext;
    List<Communicate> communicateList;
    AdapterListener listener;

    public DoctorChatConvoAdapter(Context mContext, List<Communicate> c, AdapterListener listener) {
        this.mContext = mContext;
        this.communicateList = c;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorChatConvoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_convo, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorChatConvoAdapter.ViewHolder holder, int position) {
        Communicate communicate = communicateList.get(position);
        if (communicate.getSender().size() > 0) {
            String txt = String.join("\n", communicate.getSender());
            if (communicate.getSender().size() > 0 && txt!="") {
                holder.txtRecipientMsg.setText(txt);
            } else {
                holder.cardRecipient.setVisibility(View.GONE);
            }

        } else {
            holder.cardRecipient.setVisibility(View.GONE);

        }

        if (communicate.getRecipient() != null) {
            if (communicate.getRecipient().size() > 0) {
                holder.txtSenderMsg.setText(String.join("\n", communicate.getRecipient()));
            } else {
                holder.cardSender.setVisibility(View.GONE);
            }
        } else {
            holder.cardSender.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return communicateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSenderMsg, txtRecipientMsg;
        CardView cardSender, cardRecipient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSenderMsg = itemView.findViewById(R.id.txtSenderMsg);
            txtRecipientMsg = itemView.findViewById(R.id.txtRecipientMsg);
            cardSender = itemView.findViewById(R.id.cardSender);
            cardRecipient = itemView.findViewById(R.id.cardRecipient);
        }
    }
}
