package com.thebridgestudio.amwayconference.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {
    @DatabaseField(id = true, columnName="_id")
    private long id;
    
    @DatabaseField
    private String title;
    
    @DatabaseField
    private String content;
    
    @DatabaseField
    private boolean read;
    
    public Message(){
    }
    
    public Message(long id, String title, String content, boolean read) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    protected String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

}
