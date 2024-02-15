package com.application.chat.Models;

public class Message {
    private String message;
    private String messsageId;
    private String reciver;
    private boolean seen;
    private String sender;
    private String timestamp;
    public Message(){}

    public Message(String sender, String reciver, String message, String timestamp){
        this.message=message;
        this.reciver=reciver;
        this.sender = sender;
        this.timestamp=timestamp;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setMesssageId(String messsageId) {
        this.messsageId = messsageId;
    }
    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMesssageId() {
        return messsageId;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
    public String getSender() {
        return sender;
    }
    public String getReciver() {
        return reciver;
    }
    public String getMessage() {
        return message;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public boolean isSeen() {
        return seen;
    }

}
