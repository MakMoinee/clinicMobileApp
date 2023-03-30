package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sample.clinic.Adapters.ChatListAdapter;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Interfaces.MessageListener;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.Services.Messenger;
import com.sample.clinic.databinding.FragmentChatListBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    FragmentChatListBinding binding;
    Context mContext;
    MainButtonsListener listener;
    LocalFirestore2 fs;
    ProgressDialog pd;
    ChatListAdapter adapter;
    List<Message2> msgList = new ArrayList<>();
    Messenger messenger;

    public ChatListFragment(Context mContext, MainButtonsListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(LayoutInflater.from(mContext), container, false);
        setValues();
        loadData();
        return binding.getRoot();
    }

    private void loadData() {
        pd.show();
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getMessages(users.getDocID(), new FireStoreListener() {
            @Override
            public void onSuccessMessage(List<Message2> list) {
                msgList = list;
                pd.dismiss();
                adapter = new ChatListAdapter(mContext, list, new AdapterListener() {
                    @Override
                    public void onChatClick(int position) {

                    }

                    @Override
                    public void onLongClick(int position) {
                        Message2 msg = msgList.get(position);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        DialogInterface.OnClickListener dListener = (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_NEGATIVE:

                                    messenger.deleteMessage(msg.getMessageID(), new MessageListener() {

                                        @Override
                                        public void onSuccess() {
                                            fs.deleteMessage(msg.getDocID(), new FireStoreListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Toast.makeText(mContext, "Successfully Deleted Chat", Toast.LENGTH_SHORT).show();
                                                    binding.recycler.setAdapter(null);
                                                    loadData();
                                                }

                                                @Override
                                                public void onError() {
                                                    Toast.makeText(mContext, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError() {
                                            Toast.makeText(mContext, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    break;
                                default:
                                    dialog.dismiss();
                                    break;
                            }
                        };

                        mBuilder.setMessage("Are You Sure You Want To Delete Chat?")
                                .setNegativeButton("Yes", dListener)
                                .setPositiveButton("No", dListener)
                                .show();
                    }
                });
                binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                binding.recycler.setAdapter(adapter);
            }

            @Override
            public void onError() {
                pd.dismiss();
                Toast.makeText(mContext, "There are no chats history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValues() {
        messenger = new Messenger();
        fs = new LocalFirestore2(mContext);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Messages ...");
        pd.setCancelable(false);
        binding.navBottom.setSelectedItemId(R.id.action_chat);
        binding.navBottom.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    listener.onHomeClick();
                    return true;
                case R.id.action_booking:
                    listener.onBookingClick();
                    return true;
                case R.id.action_settings:
                    listener.onNavClick();
                    return true;
                case R.id.action_appointment:
                    listener.onConsultClick();
                    return true;
            }
            return false;
        });
    }
}
