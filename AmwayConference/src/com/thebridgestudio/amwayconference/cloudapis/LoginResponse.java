package com.thebridgestudio.amwayconference.cloudapis;

import it.restrung.rest.annotations.JsonProperty;
import it.restrung.rest.marshalling.response.AbstractJSONResponse;

public class LoginResponse extends AbstractJSONResponse {
  private static final long serialVersionUID = 1L;

  @JsonProperty(value = "result")
  private int result;

  @JsonProperty(value = "data")
  private Data data;

  @JsonProperty(value = "error_code")
  private String errorMsg;

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public LoginResponse() {}

  public static class Data extends AbstractJSONResponse {
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "account")
    private String account;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAccount() {
      return account;
    }

    public void setAccount(String account) {
      this.account = account;
    }

    public Data() {}
  }

}
