package com.sample.clinic.Models;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    String messageID;
    String userID;
    String recipientUserID;
    List<Communicate> messages;

    public Message(MessageBuilder builder) {
        this.messageID = builder.messageID;
        this.userID = builder.userID;
        this.recipientUserID = builder.recipientUserID;
        this.messages = builder.messages;
    }

    public static class MessageBuilder {
        String messageID;
        String userID;
        String recipientUserID;
        List<Communicate> messages;


        public MessageBuilder setMessageID(String messageID) {
            this.messageID = messageID;
            return this;
        }

        public MessageBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public MessageBuilder setRecipientUserID(String recipientUserID) {
            this.recipientUserID = recipientUserID;
            return this;
        }

        public MessageBuilder setMessages(List<Communicate> messages) {
            this.messages = messages;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
