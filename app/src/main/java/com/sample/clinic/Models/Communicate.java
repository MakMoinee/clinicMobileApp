package com.sample.clinic.Models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Communicate {
    List<String> sender = new ArrayList<>();
    List<String> recipient = new ArrayList<>();
    String senderDate;
    String recipientDate;

    public Communicate(CommunicateBuilder builder) {
        this.sender = builder.sender;
        this.recipient = builder.recipient;
        this.senderDate = builder.senderDate;
        this.recipientDate = builder.recipientDate;
    }


    public static class CommunicateBuilder {
        List<String> sender;
        List<String> recipient;
        String senderDate;
        String recipientDate;

        public CommunicateBuilder setSender(List<String> sender) {
            this.sender = sender;
            return this;
        }

        public CommunicateBuilder setRecipient(List<String> recipient) {
            this.recipient = recipient;
            return this;
        }

        public CommunicateBuilder setSenderDate(String senderDate) {
            this.senderDate = senderDate;
            return this;
        }

        public CommunicateBuilder setRecipientDate(String recipientDate) {
            this.recipientDate = recipientDate;
            return this;
        }

        public Communicate build() {
            return new Communicate(this);
        }
    }
}
