package com.sample.clinic.Services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sample.clinic.Common.Common;
import com.sample.clinic.Interfaces.MessageListener;
import com.sample.clinic.Models.Message;

import java.util.HashMap;
import java.util.Map;

public class Messenger {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public Messenger() {
        ref = FirebaseDatabase.getInstance().getReference();
        db = FirebaseDatabase.getInstance();

    }

    public DatabaseReference getRef() {
        return this.ref;
    }

    public void newMessage(Message message, MessageListener listener) {

        Map<String, Object> messageMap = Common.getMsgMap(message);
        String messageId = this.ref.child("messages").push().getKey();
        this.ref.child("messages").child(messageId).setValue(messageMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    listener.onError();
                } else {
                    message.setMessageID(messageId);
                    listener.onSuccess(message);
                }
            }
        });


    }

    public void updateMessage(Message message, MessageListener listener) {
        Map<String, Object> messageMap = Common.getMsgMap(message);
        this.ref.child("messages").child(message.getMessageID()).setValue(messageMap, (error, ref) -> {
            if (error != null) {
                listener.onError();
            } else {
                listener.onSuccess(message);
            }
        });
    }

    public void deleteMessage(String messageID, MessageListener listener) {
        this.ref.child("messages").child(messageID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    listener.onSuccess();
                } else {
                    Log.e("ERROR", error.getMessage());
                    listener.onError();
                }
            }
        });
    }

}
