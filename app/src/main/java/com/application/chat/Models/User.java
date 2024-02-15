package com.application.chat.Models;

import java.util.List;

public class User {
  
    private String id;
    private String name;
    private String image;
    private String token;
    private boolean isOnline;

    public User(){}
    
    public User(String id,String name,String image,String token,boolean isOnline) {
        this.id=id;
        this.name=name;
        this.image=image;
        this.token=token;
        this.isOnline = isOnline;
    }
    
    public String getId() {
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getImage(){
        return this.image;
    }
    public String getToken(){
        return this.token;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
