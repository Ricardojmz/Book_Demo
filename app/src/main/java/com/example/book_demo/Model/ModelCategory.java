package com.example.book_demo.Model;

public class ModelCategory {
    //make sure to use same speellings for model variables and firebase

    String id, category, vid;
    long timestamp;

    //constructor empty required for firebase
    public ModelCategory() {
    }
    //parametrized constructor
    public ModelCategory(String id, String category, String vid, long timestamp) {
        this.id = id;
        this.category = category;
        this.vid = vid;
        this.timestamp = timestamp;
    }

    //Get and Set


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
