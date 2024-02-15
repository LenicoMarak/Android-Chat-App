package com.application.chat.FCMNotifiacation;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FCManager {
    public static void sendRemoteNotification(String token, HashMap<String, String> data) {
        FCMApiService apiService = ClientService.getClientService().create(FCMApiService.class);
        FCMRequest fcmRequest = new FCMRequest(token, data.get("title"),data.get("body"),data);
        apiService.sendRemoteNotification("",fcmRequest).enqueue(new Callback<ApiResponseBody>() {
            @Override
            public void onResponse(Call<ApiResponseBody> call, Response<ApiResponseBody> response) {
                if (response.isSuccessful()) {
                    ApiResponseBody apiResponseBody = response.body();
                    Log.e("ResponceSucces", "Notifiation sended");
                    Log.e("ResponceSucces", new Gson().toJson(apiResponseBody));
                } else {
                    Log.e("ResponceFailed", "Failed to send notification");
                    int statusCode = response.code();
                    if (statusCode == 403) {
                        Log.e("ResponseError", "Code: " + statusCode);
                        Log.e("ErrorBody", response.errorBody().toString());
                        Log.e("ResponseMessage", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseBody> call, Throwable t) {
                Log.e("FCMApiService", t.getMessage());
            }
        });
    }
    /*public static String getOAuthToken() {
        try {
            FileInputStream fileInputStream=new FileInputStream("serviceAc.json");
            FirebaseOptions firebaseOptions=new FirebaseOptions.Builder()
                    .set
            FirebaseApp.initializeApp(firebaseOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
