package com.example.myplayandroid.Class;

/**
 * Created by zhongzhiqiang on 19-4-3.
 */

public class Message {

    private String author;
    private String category;
    private String title;
    private String time;
    private String url;

    public Message(String author, String category, String title, String time, String url) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.time = time;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}