package com.example.srinivasprasad.instacare;

import java.util.Date;

public class Comments {

    String user_id,comment;
    public Date timestamp;

    public Comments(){}

    public Comments(String user_id, String comment, Date timestamp) {
        this.user_id = user_id;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
