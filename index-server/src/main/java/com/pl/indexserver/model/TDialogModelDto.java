package com.pl.indexserver.model;

import java.sql.Timestamp;
import java.util.Map;

public class TDialogModelDto {

    private Long id;
    private String ctName;
    private String ctPhone;
    private String isIntentionInfo;
    private Timestamp beginDate;
    private int duration;
    private String outNumber;
    private String intentionLevel;
    private String focusLevel;
    private String intentLevel;
    private String finalLevel;
    private String ctAddress;
    private int isIntention;
    private Long taskId;
    private Long fileSize;
    private String errormsg;
    private Map<String,Object> IntentionInfo;
    public Long getId() {
        return id;
    }

    public String getCtName() {
        return ctName;
    }

    public String getCtPhone() {
        return ctPhone;
    }

    public int getIsIntention() {
        return isIntention;
    }

    public int getDuration() {
        return duration;
    }

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public String getCtAddress() {
        return ctAddress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName;
    }

    public void setCtPhone(String ctPhone) {
        this.ctPhone = ctPhone;
    }

    public void setIsIntention(int isIntention) {
        this.isIntention = isIntention;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
    }

    public void setCtAddress(String ctAddress) {
        this.ctAddress = ctAddress;
    }

    public String getIsIntentionInfo() {
        return isIntentionInfo;
    }

    public void setIsIntentionInfo(String isIntentionInfo) {
        this.isIntentionInfo = isIntentionInfo;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public Map<String, Object> getIntentionInfo() {
        return IntentionInfo;
    }

    public void setIntentionInfo(Map<String, Object> intentionInfo) {
        IntentionInfo = intentionInfo;
    }

    public String getFocusLevel() {
        return focusLevel;
    }

    public void setFocusLevel(String focusLevel) {
        this.focusLevel = focusLevel;
    }

    public String getIntentLevel() {
        return intentLevel;
    }

    public void setIntentLevel(String intentLevel) {
        this.intentLevel = intentLevel;
    }

    public String getFinalLevel() {
        return finalLevel;
    }

    public void setFinalLevel(String finalLevel) {
        this.finalLevel = finalLevel;
    }
}
