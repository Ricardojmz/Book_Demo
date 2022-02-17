package com.example.book_demo.Model;

public class ModelPdf {
    String vid, id, title, description, categoryId, url;
    long timestamp, viewsCount, downloadsCount;

    //constructor required for Firebase
    public ModelPdf() {

    }
    //Constructor all params

    public ModelPdf(String vid, String id, String title, String description, String categoryId, String url, long timestamp, long viewsCount, long downloadsCount) {
        this.vid = vid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
    }


    //Get and Set


    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }
}