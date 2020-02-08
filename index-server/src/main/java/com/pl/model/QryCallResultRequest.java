package com.pl.model;

/**
 * @author HuangGuangLei
 * @Date 2019/7/4
 */
public class QryCallResultRequest {
    private String userName;
    private String timeStamp;
    private String checkCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
