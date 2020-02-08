package com.pl.model;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/7/3
 */
public class ChatPhone {
    private int code;
    private String callTaskName;
    private Long callTaskId;
    private String bussinessName;
    private Long bussinessId;
    private List<String> toAddWeXinPhones;

    public String getCallTaskName() {
        return callTaskName;
    }

    public void setCallTaskName(String callTaskName) {
        this.callTaskName = callTaskName;
    }

    public Long getCallTaskId() {
        return callTaskId;
    }

    public void setCallTaskId(Long callTaskId) {
        this.callTaskId = callTaskId;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public Long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(Long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public List<String> getToAddWeXinPhones() {
        return toAddWeXinPhones;
    }

    public void setToAddWeXinPhones(List<String> toAddWeXinPhones) {
        this.toAddWeXinPhones = toAddWeXinPhones;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
