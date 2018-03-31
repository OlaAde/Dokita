package com.upload.adeogo.dokita.models;

/**
 * Created by ademi on 21/03/2018.
 */

public class MedicalArticle {
    private String sourceId, sourceName, author, title, description, url, imageUrl, publisgedAt;

    public MedicalArticle(){

    }
    public MedicalArticle(String sourceId, String sourceName, String author, String title, String description, String url, String imageUrl, String publisgedAt) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publisgedAt = publisgedAt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisgedAt() {
        return publisgedAt;
    }

    public void setPublisgedAt(String publisgedAt) {
        this.publisgedAt = publisgedAt;
    }
}
