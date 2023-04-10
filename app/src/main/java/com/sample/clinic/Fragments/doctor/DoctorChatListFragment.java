package com.sample.clinic.Fragments.doctor;

import android.content.Context;
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
import com.sample.clinic.Interfaces.DoctorActivityListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.FragmentDoctorChatListBinding;

import java.util.ArrayList;
import java.util.List;

public class DoctorChatListFragment extends Fragment {

    FragmentDoctorChatListBinding binding;
    Context mContext;
    DoctorActivityListener listener;
    LocalFirestore2 fs;
    ChatListAdapter adapter;

    public DoctorChatListFragment(Context mContext, DoctorActivityListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorChatListBinding.inflate(LayoutInflater.from(mContext), container, false);
        loadChats();
        return binding.getRoot();
    }

    private void loadChats() {
        fs = new LocalFirestore2(mContext);
        Users users = new MyUserPreferrence(mContext).getUsers();
        fs.getDoctorMessages(users.getDocID(), new FireStoreListener() {
            @Override
            public void onSuccessMessage(List<Message2> list) {
                fs.getAllUsers(new FireStoreListener() {
                    @Override
                    public void onSuccessUsers(List<Users> usersList) {
                        List<Users> newUserList = new ArrayList<>();
                        for (Message2 msg : list) {
                            for (Users users1 : usersList) {
                                if (msg.getUserID().equals(users1.getDocID())) {
                                    newUserList.add(users1);
                                }
                            }
                        }
                        adapter = new ChatListAdapter(mContext, list, new AdapterListener() {
                            @Override
                            public void onClick() {
                                AdapterListener.super.onClick();
                            }

                            @Override
                            public void onChatClick(int position) {
                                AdapterListener.super.onChatClick(position);
                            }
                        }, newUserList);
                        binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.recycler.setAdapter(adapter);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext, "There are no chats available", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError() {
                Toast.makeText(mContext, "There are no chats available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
