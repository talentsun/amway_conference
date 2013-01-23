package com.thebridgestudio.amwayconference.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {
    @DatabaseField(id = true, columnName="_id")
    private int id;
    
    @DatabaseField
    private String content;
    
    @DatabaseField
    private long timestamp;
    
    @DatabaseField
    private boolean read;
    
    public Message(){
    }
    
    public Message(int id, String content, long timestamp, boolean read) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
