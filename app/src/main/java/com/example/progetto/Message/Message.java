package com.example.progetto.Message;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "message")
public class Message extends User{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "sender")
    private User sender;
    @ColumnInfo(name = "createdAt")
    private long createdAt;

    public Message (String message, User sender, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}

class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    private String userId;
    @ColumnInfo(name = "nickname")
    private String nickname;
    @ColumnInfo(name = "profileUrl")
    private String profileUrl;

    public String getNickname() {
        return nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getUserId() {
        return userId;
    }
}
