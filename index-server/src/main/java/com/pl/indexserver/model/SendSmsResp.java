package com.pl.indexserver.model;

/**
 * @author HuangGuangLei
 * @Date 2019/9/17
 */
public class SendSmsResp {
    private int retCode;
    private String retDesc;
    private String smsId;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getRetDesc() {
        return retDesc;
    }

    public void setRetDesc(String retDesc) {
        this.retDesc = retDesc;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }
}
