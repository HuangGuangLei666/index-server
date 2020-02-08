package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/19
 */
public class QryFriendResp {
    private int retCode;
    private String retDesc;
    private List<QryFriendRespDto> retData;

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

    public List<QryFriendRespDto> getRetData() {
        return retData;
    }

    public void setRetData(List<QryFriendRespDto> retData) {
        this.retData = retData;
    }
}
