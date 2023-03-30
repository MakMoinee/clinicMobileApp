package com.sample.clinic.Interfaces;

import com.sample.clinic.Models.Message;

public interface MessageListener {
    default void onSuccess(Message message) {

    }

    default void onSuccess() {

    }

    void onError();
}
