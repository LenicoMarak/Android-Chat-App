package com.application.chat.FCMNotifiacation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMApiService {
    @Headers("Content-Type:application/json")
    @POST("v1/projects/textmeapp-ba431/messages:send")
    Call<ApiResponseBody> sendRemoteNotification(@Header ("Authorization") String authHeader,@Body FCMRequest fcmRequest);
}
