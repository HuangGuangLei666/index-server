package com.pl.indexserver.model;

/**
 * @author HuangGuangLei
 * @Date 2019/9/17
 */
public class CheckSmsCodeResp {
    private int retCode;
    private String retDesc;

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

    public CheckSmsCodeResp(int retCode, String retDesc) {
        this.retCode = retCode;
        this.retDesc = retDesc;
    }

    public CheckSmsCodeResp() {

    }
}
