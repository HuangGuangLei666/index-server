package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/6
 */
public class VoiceDtoResp {
    private int retCode;
    private String retDesc;
    private List<VoiceDto> retData;

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

    public List<VoiceDto> getRetData() {
        return retData;
    }

    public void setRetData(List<VoiceDto> retData) {
        this.retData = retData;
    }
}
