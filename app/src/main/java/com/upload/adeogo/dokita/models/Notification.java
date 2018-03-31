package com.upload.adeogo.dokita.models;

/**
 * Created by Adeogo on 11/25/2017.
 */

public class Notification {
    private String username, imageUrl, email, uid, text, topic, type, which;


    public Notification(){}

    public Notification(String username, String imageUrl, String email, String uid, String text, String topic, String type, String which) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.email = email;
        this.uid = uid;
        this.text = text;
        this.topic = topic;
        this.type = type;
        this.which = which;
    }

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getTopic() {
        return topic;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

}
