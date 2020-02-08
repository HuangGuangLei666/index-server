package com.pl.indexserver.model;

import java.util.Date;

public class StatisticsExportDto {

    private String ctName;
    private String ctPhone;
    private String isIntentionInfo;
    private Date beginDate;
    private int duration;
    private String  agentNum;
    private String intentionLevel;
    private String taskName;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(String agentNum) {
        this.agentNum = agentNum;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
