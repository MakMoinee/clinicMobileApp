package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Communicate;
import com.sample.clinic.Models.Message;
import com.sample.clinic.R;

import java.util.List;

public class ChatConvoAdapter extends RecyclerView.Adapter<ChatConvoAdapter.ViewHolder> {

    Context mContext;
    List<Communicate> communicateList;
    AdapterListener listener;

    public ChatConvoAdapter(Context mContext, List<Communicate> c, AdapterListener listener) {
        this.mContext = mContext;
        this.communicateList = c;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatConvoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_convo, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatConvoAdapter.ViewHolder holder, int position) {
        Communicate communicate = communicateList.get(position);
        if (communicate.getSender().size() > 0) {
            holder.txtSenderMsg.setText(String.join("\n", communicate.getSender()));
        } else {
            holder.cardSender.setVisibility(View.GONE);
        }

        if(communicate.getRecipient()!=null){
            if (communicate.getRecipient().size() > 0) {
                holder.txtRecipientMsg.setText(String.join("\n", communicate.getRecipient()));
            } else {
                holder.cardRecipient.setVisibility(View.GONE);
            }
        } else {
            holder.cardRecipient.setVisibility(View.GONE);
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
