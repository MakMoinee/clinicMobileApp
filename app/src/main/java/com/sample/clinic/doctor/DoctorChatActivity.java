package com.sample.clinic.doctor;

import android.os.Bundle;
import android.util.Log;
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
import com.sample.clinic.Adapters.doctor.DoctorChatConvoAdapter;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.MessageListener;
import com.sample.clinic.Models.Communicate;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.Messenger;
import com.sample.clinic.databinding.ActivityChatsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorChatActivity extends AppCompatActivity {

    ActivityChatsBinding binding;

    Message2 selectedMessage;

    Messenger messenger;

    DoctorChatConvoAdapter adapter;

    List<String> messageList = new ArrayList<>();
    Message origMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setValues();
        String rawMsg2 = getIntent().getStringExtra("rawMsg2");
        selectedMessage = new Gson().fromJson(rawMsg2, new TypeToken<Message2>() {
        }.getType());

        if (selectedMessage != null) {
            messenger.getMessage(selectedMessage.getMessageID(), new MessageListener() {

                @Override
                public void onSuccess(Message message) {
                    origMessage = message;
                    adapter = new DoctorChatConvoAdapter(DoctorChatActivity.this, message.getMessages(), new AdapterListener() {
                        @Override
                        public void onClick(Doctor doctor) {
                            AdapterListener.super.onClick(doctor);
                        }
                    });
                    binding.recycler.setLayoutManager(new LinearLayoutManager(DoctorChatActivity.this));
                    binding.recycler.setAdapter(adapter);
                    addListeners();
                }

                @Override
                public void onError() {
                    Toast.makeText(DoctorChatActivity.this, "Failed To Load Messages", Toast.LENGTH_SHORT).show();
                }
            });
        }

        setListeners();
    }

    private void addListeners() {
        DatabaseReference ref = messenger.getRef();
        ref.child("messages").child(selectedMessage.getMessageID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);

                if (message != null) {
                    origMessage = message;
                    messageList = new ArrayList<>();
                    adapter = new DoctorChatConvoAdapter(DoctorChatActivity.this, message.getMessages(), new AdapterListener() {
                        @Override
                        public void onClick() {
                            AdapterListener.super.onClick();
                        }

                        @Override
                        public void onChatClick(int position) {
                            AdapterListener.super.onChatClick(position);
                        }
                    });
                    binding.recycler.setLayoutManager(new LinearLayoutManager(DoctorChatActivity.this));
                    binding.recycler.setAdapter(adapter);
                    binding.recycler.scrollToPosition(message.getMessages().size()-1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String msgTxt = binding.txtMessage.getText().toString();
            if (msgTxt.equals("")) {
                Toast.makeText(DoctorChatActivity.this, "Please Don't Message Field Empty", Toast.LENGTH_SHORT).show();
            } else {
                Users users = new MyUserPreferrence(DoctorChatActivity.this).getUsers();
                messageList.add(msgTxt);

                Calendar calendar = Calendar.getInstance();
                Communicate communicate = new Communicate.CommunicateBuilder()
                        .setRecipient(messageList)
                        .setRecipientDate(calendar.getTime().toString())
                        .build();


                origMessage.getMessages().add(communicate);

                Message message = new Message.MessageBuilder()
                        .setUserID(selectedMessage.getUserID())
                        .setRecipientUserID(users.getDocID())
                        .setMessageID(selectedMessage.getMessageID())
                        .setMessages(origMessage.getMessages())
                        .build();

                messenger.updateMessage(message, new MessageListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(DoctorChatActivity.this, "Successfully sent message", Toast.LENGTH_SHORT).show();
                        binding.txtMessage.setText("");
                        messageList = new ArrayList<>();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(DoctorChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setValues() {
        messenger = new Messenger();
    }
}
