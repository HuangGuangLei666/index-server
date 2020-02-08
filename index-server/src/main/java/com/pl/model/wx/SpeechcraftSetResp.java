package com.pl.model.wx;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SpeechcraftSetResp {
    private int retCode;
    private String retDesc;
    private List<LabelVo> retData = new ArrayList<>();

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

    public List<LabelVo> getRetData() {
        return retData;
    }

    public void setRetData(List<LabelVo> retData) {
        this.retData = retData;
    }
}
