package com.application.chat.CatcheDb;

import android.content.Context;

import io.objectbox.BoxStore;


public class ObjectRepository {
    private static BoxStore boxStore;
    public static void init(Context context){
        boxStore=MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }
    public static BoxStore getBoxStore() {
        return boxStore;
    }
}
