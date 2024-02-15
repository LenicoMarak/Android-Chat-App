package com.application.chat.FCMNotifiacation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.application.chat.ConversationActivity;
import com.application.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String CHANNEL_ID="MyChannel";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(fUser!=null){
            DatabaseReference userRef= FirebaseDatabase.getInstance().getReference("Users");
            DatabaseReference tokenRef=userRef.child(fUser.getUid()).child("token");
            tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String data=snapshot.getValue(String.class);
                    if(data==null){
                        tokenRef.setValue(token);
                        return;
                    }else if(data!=null && !data.equals(token)){
                        tokenRef.setValue(token);
                        return;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase","Token Database reference error");
                }
            });
        }
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remotemessage) {
        super.onMessageReceived(remotemessage);
        sendNotification(remotemessage);
    }
    public void sendNotification(RemoteMessage remotemessage){
        Map<String,String> data=remotemessage.getData();
        String title=data.get("title");
        String contentText=data.get("content");
        String senderid=data.get("sender");
        String image=data.get("image");
        String usertoken=data.get("token");
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent=new Intent(getApplicationContext(), ConversationActivity.class);
        intent.putExtra("uid",senderid);
        intent.putExtra("nam",title);
        intent.putExtra("image",image);
        intent.putExtra("token",usertoken);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationCompat=new NotificationCompat.Builder(this,CHANNEL_ID).
                setAutoCancel(true).
                setContentTitle(title).
                setContentText(contentText).
                setContentIntent(pendingIntent).
                setWhen(System.currentTimeMillis()).
                setSmallIcon(R.drawable.notification_icon);
        notificationManager.notify(1,notificationCompat.build());
    }
}
