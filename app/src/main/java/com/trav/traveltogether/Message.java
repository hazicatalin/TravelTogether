package com.trav.traveltogether;

public class Message {
    private String sender, receiver, message;

    Message(){}
    public Message(String sender, String receiver, String message){
        this.sender=sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
