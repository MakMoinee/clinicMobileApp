package com.sample.clinic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sample.clinic.Adapters.ChatConvoAdapter;
import com.sample.clinic.Common.Common;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MessageListener;
import com.sample.clinic.Models.Communicate;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.Messenger;
import com.sample.clinic.databinding.ActivityChatsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ActivityChatsBinding binding;
    Doctor selectedDoctor;
    Messenger messenger;
    String userID;
    LocalFirestore2 db;
    ProgressDialog pd;
    DatabaseReference ref;

    Boolean hasRecipient = false;
    ChatConvoAdapter adapter;

    Message currentMessage;
    List<String> msgList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setValues();
        setListeners();
    }

    private void setValues() {
        String rawDoctor = getIntent().getStringExtra("rawDoctor");
        pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);
        db = new LocalFirestore2(ChatActivity.this);
        messenger = new Messenger();
        ref = messenger.getRef();
        Users users = new MyUserPreferrence(ChatActivity.this).getUsers();
        userID = users.getDocID();
        Log.e("selectedDoctor", rawDoctor);
        selectedDoctor = new Gson().fromJson(rawDoctor, new TypeToken<Doctor>() {
        }.getType());
        if (selectedDoctor == null) {

            finish();
        } else {
            Log.e("selectedDoctor", selectedDoctor.getDoctorName());
        }
    }

    private void setListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.txtMessage.getText().toString();
            if (msg.equals("")) {
                Toast.makeText(ChatActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                pd.show();

                msgList.add(msg);
                Calendar calendar = Calendar.getInstance();
                Communicate communicate = new Communicate.CommunicateBuilder()
                        .setSender(msgList)
                        .setSenderDate(calendar.getTime().toString())
                        .build();
                List<Communicate> communicateList = new ArrayList<>();
                communicateList.add(communicate);
                Message message = new Message.MessageBuilder()
                        .setUserID(userID)
                        .setRecipientUserID(selectedDoctor.getDocID())
                        .setMessages(communicateList)
                        .build();
                if (!hasRecipient) {
                    messenger.newMessage(message, new MessageListener() {
                        @Override
                        public void onSuccess(Message m) {
                            db.addMessage(m, selectedDoctor.getDoctorName(), new FireStoreListener() {
                                @Override
                                public void onSuccess() {
                                    pd.dismiss();
                                    Toast.makeText(ChatActivity.this, "Successfully Sent Message", Toast.LENGTH_SHORT).show();
                                    hasRecipient = true;
                                    binding.txtMessage.setText("");
                                    currentMessage = m;
                                    msgList= new ArrayList<>();
                                    hasRecipientListener(m.getMessageID());
                                }

                                @Override
                                public void onError() {
                                    pd.dismiss();
                                    Toast.makeText(ChatActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            pd.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    message.setMessageID(currentMessage.getMessageID());
                    messenger.updateMessage(message, new MessageListener() {
                        @Override
                        public void onSuccess(Message message) {
                            pd.dismiss();
                            Toast.makeText(ChatActivity.this, "Successfully Sent Message", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError() {
                            pd.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
    }

    private void hasRecipientListener(String messageID) {
        ref.child("messages").child(messageID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Message message = snapshot.getValue(Message.class);

                        if (message != null) {
                            adapter = new ChatConvoAdapter(ChatActivity.this, message.getMessages(), new AdapterListener() {
                                @Override
                                public void onChatClick(int position) {
                                    AdapterListener.super.onChatClick(position);
                                }
                            });

                            binding.recycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                            binding.recycler.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                    }
                });
    }
}
