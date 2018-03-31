package com.upload.adeogo.dokita.models;

import java.util.Date;

/**
 * Created by Adeogo on 10/28/2017.
 */

public class Question {
    private String text;
    private String name;
    private String photoUrl;
    private int you;
    private Date createdAt;

    public Question(){}

    public Question(String text, String name, String photoUrl, int you, Date createdAt) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.you = you;
        this.createdAt = createdAt;
    }

    public Question(String text, String name, int you, String photoUrl){
        this.text = text;
        this.name = name;
        this.you = you;
        this.photoUrl = photoUrl;
        createdAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getYou() {
        return you;
    }

    public void setYou(int you) {
        this.you = you;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
