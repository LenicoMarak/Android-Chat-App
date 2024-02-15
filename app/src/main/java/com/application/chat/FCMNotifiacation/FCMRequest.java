package com.application.chat.FCMNotifiacation;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class FCMRequest {
    @SerializedName("to")
    private String to;
    @SerializedName("notification")
    private Notification notification;
    @SerializedName("data")
    private HashMap<String,String> data;

    public FCMRequest(String to,String title,String body,HashMap<String,String> data) {
        this.to=to;
        this.notification=new Notification(title,body);
        this.data=data;
    }
    public String getTo() {
        return to;
    }

    public Notification getNotification() {
        return notification;
    }
    public HashMap<String, String> getHashData() {
        return data;
    }
    private static class Notification{
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;
        public Notification(String title,String body) {
            this.title = title;
            this.body=body;
        }
        public String getTitle() {
            return title;
        }
        public String getBody() {
            return body;
        }
    }
}
