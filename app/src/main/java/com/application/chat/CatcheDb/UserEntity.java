package com.application.chat.CatcheDb;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UserEntity {
    @Id
    public long id;
    public String login_id;
    public String ringtone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }
}
