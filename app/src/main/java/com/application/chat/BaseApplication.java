package com.application.chat;

import android.app.Application;

import androidx.activity.EdgeToEdge;

import com.application.chat.CatcheDb.ObjectRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ObjectRepository.init(this);
    }
}
