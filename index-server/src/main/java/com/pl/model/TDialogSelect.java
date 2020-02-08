package com.pl.model;

import java.sql.Timestamp;

public class TDialogSelect {

    private Long id;
    private String ctName;
    private String ctPhone;
    private int status;
    private int isIntention;
    private Timestamp beginDate;
    private int duration;
    private String outNumber;
    private String intentionLevel;
    private String focusLevel;
    private String intentLevel;
    private String ctAddress;
    private String ttsInfo;
    private Long fileSize;
    private String errormsg;

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

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getTtsInfo() {
        return ttsInfo;
    }

    public void setTtsInfo(String ttsInfo) {
        this.ttsInfo = ttsInfo;
    }
}
