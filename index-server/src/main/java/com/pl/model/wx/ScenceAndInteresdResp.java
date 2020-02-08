package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/6
 */
public class ScenceAndInteresdResp {
    private int retCode;
    private String retDesc;
    private List<TMall> retData;

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

    public List<TMall> getRetData() {
        return retData;
    }

    public void setRetData(List<TMall> retData) {
        this.retData = retData;
    }
}
