package com.application.chat.Models;

public class Friend {
    private String friendId;
    int messageCount;
    public Friend(){}
    public Friend(String friendId) {
        this.friendId = friendId;
    }
    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getMessageCount() {
        return messageCount;
    }
}
