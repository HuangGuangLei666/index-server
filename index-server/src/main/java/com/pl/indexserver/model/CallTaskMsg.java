package com.pl.indexserver.model;

import java.io.Serializable;

/**
 * index 返回字段
 */
public class CallTaskMsg implements Serializable {

    private Long callTaskId;
    private Integer status;
    private String taskName;
    private String endDate;
    private Integer taskCount;
    private Integer finishToday;
    private Integer intentional;
    private Integer stillCount;
    private String stillConnect;
    private String expectMsg;

    public CallTaskMsg() {

    }

    public CallTaskMsg(Long callTaskId, Integer status, String taskName, String endDate, Integer taskCount, Integer finishToday, Integer intentional, Integer stillCount, String stillConnect) {
        this.callTaskId = callTaskId;
        this.status = status;
        this.taskName = taskName;
        this.endDate = endDate;
        this.taskCount = taskCount;
        this.finishToday = finishToday;
        this.intentional = intentional;
        this.stillCount = stillCount;
        this.stillConnect = stillConnect;
    }

    public Long getCallTaskId() {
        return callTaskId;
    }

    public void setCallTaskId(Long callTaskId) {
        this.callTaskId = callTaskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Integer getFinishToday() {
        return finishToday;
    }

    public void setFinishToday(Integer finishToday) {
        this.finishToday = finishToday;
    }

    public Integer getIntentional() {
        return intentional;
    }

    public void setIntentional(Integer intentional) {
        this.intentional = intentional;
    }

    public Integer getStillCount() {
        return stillCount;
    }

    public void setStillCount(Integer stillCount) {
        this.stillCount = stillCount;
    }

    public String getStillConnect() {
        return stillConnect;
    }

    public void setStillConnect(String stillConnect) {
        this.stillConnect = stillConnect;
    }

    public String getExpectMsg() {
        return expectMsg;
    }

    public void setExpectMsg(String expectMsg) {
        this.expectMsg = expectMsg;
    }

}