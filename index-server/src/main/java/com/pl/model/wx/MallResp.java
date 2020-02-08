package com.pl.model.wx;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MallResp {
    private int retCode;
    private String retDesc;
    private List<MallVo> retData = new ArrayList<>();
    private List<MallVo> retDataNo = new ArrayList<>();

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

    public List<MallVo> getRetData() {
        return retData;
    }

    public void setRetData(List<MallVo> retData) {
        this.retData = retData;
    }

    public List<MallVo> getRetDataNo() {
        return retDataNo;
    }

    public void setRetDataNo(List<MallVo> retDataNo) {
        this.retDataNo = retDataNo;
    }
}
