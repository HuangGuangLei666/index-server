package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/10/24
 */
public class RecommendResp {
    private int retCode;
    private String retDesc;
    private List<TUserinfoDto> retData;

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

    public List<TUserinfoDto> getRetData() {
        return retData;
    }

    public void setRetData(List<TUserinfoDto> retData) {
        this.retData = retData;
    }
}
