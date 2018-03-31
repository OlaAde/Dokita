package com.upload.adeogo.dokita.models;

import java.util.Date;

/**
 * Created by ademi on 24/03/2018.
 */

public class ModifiedMessage {

    private String id;
    private String text;
    private long createdAt;
    private Message.Image image;
    private Message.Voice voice;
    private String userId;
    private String name;
    private String avatar;
    private boolean online;

    public ModifiedMessage() {
    }

    public ModifiedMessage(String id, String text) {
        this(id, text, new Date().getTime());
    }

    public ModifiedMessage(String id, String text, long createdAt, Message.Image image, Message.Voice voice) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.image = image;
        this.voice = voice;
    }

    public ModifiedMessage(String id, String text, long createdAt, String userId, String name, String avatar, boolean online) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }

    public ModifiedMessage(String id, String text, long createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getCreatedAt() {
        return createdAt;
    }


    public Message.Voice getVoice() {
        return voice;
    }

    public String getStatus() {
        return "Sent";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Message.Image image) {
        this.image = image;
    }

    public void setVoice(Message.Voice voice) {
        this.voice = voice;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message.Image getImage() {
        return image;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }

}
