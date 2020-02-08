package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
public class UserBusinessResp {
    private int retCode;
    private String retDesc;
    private List<QryContentDto> retData;

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

    public List<QryContentDto> getRetData() {
        return retData;
    }

    public void setRetData(List<QryContentDto> retData) {
        this.retData = retData;
    }
}
