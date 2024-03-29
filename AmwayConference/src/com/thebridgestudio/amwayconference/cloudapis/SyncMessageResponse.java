package com.thebridgestudio.amwayconference.cloudapis;

import java.util.List;

import it.restrung.rest.annotations.JsonProperty;
import it.restrung.rest.marshalling.response.AbstractJSONResponse;

public class SyncMessageResponse extends AbstractJSONResponse {
  private static final long serialVersionUID = 1L;

  @JsonProperty(value = "result")
  private int result;

  @JsonProperty(value = "data")
  private List<Message> messages;

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> data) {
    this.messages = data;
  }

  public SyncMessageResponse() {}

  public static class Message extends AbstractJSONResponse {
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "id")
    private long id;

    @JsonProperty(value = "date")
    private long date;

    @JsonProperty(value = "content")
    private String content;

    @JsonProperty(value = "valid")
    private boolean valid;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public long getDate() {
      return date;
    }

    public void setDate(long date) {
      this.date = date;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public boolean getValid() {
      return valid;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public Message() {}
  }
}
