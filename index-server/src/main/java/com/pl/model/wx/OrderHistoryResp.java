package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/16
 */
public class OrderHistoryResp {
    private int retCode;
    private String retDesc;
    private List<OrderHistory> retData;

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

    public List<OrderHistory> getRetData() {
        return retData;
    }

    public void setRetData(List<OrderHistory> retData) {
        this.retData = retData;
    }
}
