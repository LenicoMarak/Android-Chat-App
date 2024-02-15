package com.application.chat.FCMNotifiacation;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientService {
    private static Retrofit retrofit;
    private static final String BASE_URL="https://fcm.googleapis.com/";
    public static Retrofit getClientService(){
        if (retrofit ==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
