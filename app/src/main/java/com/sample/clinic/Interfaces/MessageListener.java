package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.Message;

import java.util.List;

public interface MessageListener {
    default void onSuccess(Message message) {

    }

    default void onSuccess(List<Message> messageList) {

    }

    default void onSuccess() {

    }

    void onError();
}
