package com.pl.indexserver.model;

import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

public class StatisticeModelDto {

    private Long taskId;
    private String taskName;
    private Long diglogId;
    private String ctName;
    private String ctPhone;
    private int status;
    private int isIntention;
    private String isIntentionInfo;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date beginDate;
    private int duration;
    private String outNumber;
    private String intentionLevel;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getDiglogId() {
        return diglogId;
    }

    public void setDiglogId(Long diglogId) {
        this.diglogId = diglogId;
    }

    public String getCtName() {
        return ctName;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName;
    }

    public String getCtPhone() {
        return ctPhone;
    }

    public void setCtPhone(String ctPhone) {
        this.ctPhone = ctPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(int isIntention) {
        this.isIntention = isIntention;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
    }

    public String getIsIntentionInfo() {
        return isIntentionInfo;
    }

    public void setIsIntentionInfo(String isIntentionInfo) {
        this.isIntentionInfo = isIntentionInfo;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber;
    }
}
