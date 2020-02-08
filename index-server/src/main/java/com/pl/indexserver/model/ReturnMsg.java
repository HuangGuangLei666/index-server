package com.pl.indexserver.model;

public class ReturnMsg {
    private Integer code;
    private Object content;
    private String token;
    private String errorMsg;

    public ReturnMsg() {
    }

    public ReturnMsg(Integer code, Object content, String token, String errorMsg) {
        this.code = code;
        this.content = content;
        this.token = token;
        this.errorMsg = errorMsg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
