package com.thebridgestudio.amwayconference.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {
    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField
    private String content;
    
    @DatabaseField
    private long timestamp;
    
    @DatabaseField
    private boolean read;
    
    public Message(){
    }
    
    public Message(String content, long timestamp, boolean read) {
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }

    protected long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    protected boolean isRead() {
        return read;
    }

    protected void setRead(boolean read) {
        this.read = read;
    }

}
