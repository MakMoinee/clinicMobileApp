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
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MessageListener;
import com.sample.clinic.Models.Communicate;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.Messenger;
import com.sample.clinic.databinding.ActivityChatsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatsBinding binding;
    Message2 selectedMsg;
    Messenger messenger;
    ChatConvoAdapter adapter;
    ProgressDialog pd;
    List<String> messageList = new ArrayList<>();
    LocalFirestore2 db;
    Message origMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String msgRaw = getIntent().getStringExtra("msgRaw");
        if (msgRaw.equals("")) {
            Toast.makeText(ChatDetailsActivity.this, "Failed to load message", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            setValues(msgRaw);
            setListeners();
        }
    }

    private void setListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.txtMessage.getText().toString();
            if (text.equals("")) {
                Toast.makeText(ChatDetailsActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                Users users = new MyUserPreferrence(ChatDetailsActivity.this).getUsers();

                messageList.add(text);
                Calendar calendar = Calendar.getInstance();
                Communicate communicate = new Communicate.CommunicateBuilder()
                        .setSender(messageList)
                        .setSenderDate(calendar.getTime().toString())
                        .build();
                origMessage.getMessages().add(communicate);

                Message message = new Message.MessageBuilder()
                        .setUserID(users.getDocID())
                        .setMessageID(origMessage.getMessageID())
                        .setRecipientUserID(selectedMsg.getDoctorID())
                        .setMessages(origMessage.getMessages())
                        .build();
                messenger.updateMessage(message, new MessageListener() {
                    @Override
                    public void onSuccess() {

                        db.updateMessage(message, selectedMsg.getDoctorName(), selectedMsg.getDocID(), new FireStoreListener() {
                            @Override
                            public void onSuccess() {
                                pd.dismiss();
                                Toast.makeText(ChatDetailsActivity.this, "Successfully Sent Message", Toast.LENGTH_SHORT).show();
                                binding.txtMessage.setText("");
                                messageList= new ArrayList<>();
                            }

                            @Override
                            public void onError() {
                                pd.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        pd.dismiss();
                        Toast.makeText(ChatDetailsActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void setValues(String msgRaw) {
        selectedMsg = new Gson().fromJson(msgRaw, new TypeToken<Message2>() {
        }.getType());
        db = new LocalFirestore2(ChatDetailsActivity.this);
        pd = new ProgressDialog(ChatDetailsActivity.this);
        pd.setMessage("Loading Messages ...");
        pd.setCancelable(false);
        pd.show();
        messenger = new Messenger();
        getAllMessageById();
    }

    private void getAllMessageById() {
        messenger.getMessage(selectedMsg.getMessageID(), new MessageListener() {
            @Override
            public void onSuccess(Message message) {
                origMessage = message;
                messageList = new ArrayList<>();
                pd.dismiss();
                adapter = new ChatConvoAdapter(ChatDetailsActivity.this, message.getMessages(), new AdapterListener() {
                    @Override
                    public void onClick() {
                        AdapterListener.super.onClick();
                    }

                    @Override
                    public void onChatClick(int position) {
                        AdapterListener.super.onChatClick(position);
                    }
                });
                binding.recycler.setLayoutManager(new LinearLayoutManager(ChatDetailsActivity.this));
                binding.recycler.setAdapter(adapter);
                binding.recycler.scrollToPosition(message.getMessages().size()-1);
                addListener();
            }

            @Override
            public void onError() {
                pd.dismiss();
                Toast.makeText(ChatDetailsActivity.this, "Failed to get messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addListener() {
        DatabaseReference ref = messenger.getRef();
        ref.child("messages").child(selectedMsg.getMessageID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    origMessage = message;
                    adapter = new ChatConvoAdapter(ChatDetailsActivity.this, message.getMessages(), new AdapterListener() {
                        @Override
                        public void onClick() {
                            AdapterListener.super.onClick();
                        }

                        @Override
                        public void onChatClick(int position) {
                            AdapterListener.super.onChatClick(position);
                        }
                    });
                    binding.recycler.setLayoutManager(new LinearLayoutManager(ChatDetailsActivity.this));
                    binding.recycler.setAdapter(adapter);
                    binding.recycler.scrollToPosition(message.getMessages().size()-1);
                } else {
                    binding.recycler.setAdapter(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
