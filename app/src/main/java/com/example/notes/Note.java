package com.example.notes;



public class Note {
    String Title,Content,id,timestamp;

    public Note() {
    }

    public Note(String title, String content, String timestamp,String id) {
        Title = title;
        Content = content;
        this.timestamp = timestamp;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
