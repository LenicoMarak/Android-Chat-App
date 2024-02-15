package com.application.chat.FCMNotifiacation;

import com.google.gson.annotations.SerializedName;

public class ApiResponseBody {
    @SerializedName("status")
    private String status;
    @SerializedName("userData")
    private UserData userData;
    public String getStatus() {
        return status;
    }
    public UserData getUserData() {
        return userData;
    }

    private static class UserData{
        @SerializedName("userId")
        private String userId;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        public String getUserId() {
            return userId;
        }
        public String getName() {
            return name;
        }
        public String getEmail() {
            return email;
        }
    }
}
