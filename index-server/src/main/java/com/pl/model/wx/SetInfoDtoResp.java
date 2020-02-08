package com.pl.model.wx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/27
 */
public class SetInfoDtoResp {
    private int retCode;
    private String retDesc;
    private List<SetInfoDto> retDataList = new ArrayList<>();

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

    public List<SetInfoDto> getRetDataList() {
        return retDataList;
    }

    public void setRetDataList(List<SetInfoDto> retDataList) {
        this.retDataList = retDataList;
    }
}
