package com.thebridgestudio.amwayconference.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {
  @DatabaseField(id = true, columnName = "_id")
  private long id;

  @DatabaseField
  private String content;

  @DatabaseField
  private boolean read;

  @DatabaseField
  private long date;

  public Message() {}

  public Message(long id, String content, boolean read, long date) {
    this.id = id;
    this.content = content;
    this.read = read;
    this.date = date;
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

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

}
