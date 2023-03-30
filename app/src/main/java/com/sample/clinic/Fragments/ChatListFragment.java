package com.sample.clinic.Fragments;

import android.app.ProgressDialog;
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
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.MainButtonsListener;
import com.sample.clinic.Models.Message2;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.FragmentChatListBinding;

import java.util.List;

public class ChatListFragment extends Fragment {

    FragmentChatListBinding binding;
    Context mContext;
    MainButtonsListener listener;
    LocalFirestore2 fs;
    ProgressDialog pd;
    ChatListAdapter adapter;

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
                pd.dismiss();
                adapter = new ChatListAdapter(mContext, list, new AdapterListener() {
                    @Override
                    public void onChatClick(int position) {
                        AdapterListener.super.onChatClick(position);
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
