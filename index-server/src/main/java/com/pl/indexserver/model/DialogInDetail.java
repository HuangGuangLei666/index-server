package com.pl.indexserver.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HGL
 * @Date 2018/12/27
 */
public class DialogInDetail implements Serializable {

    private String id;
    private Long companyId;
    private Long taskId;
    private String telephone;
    private int status;
    private Date beginDate;
    private Date endDate;
    private int totalSeconds;
    private String filePath;
    private Long agentId;
    private int isIntention;
    private String intentionLevel;
    private String focusLevel;
    private String intentLevel;
    private Date createDate;
    private String ttsInfo;
    private int fileSize;
    private String errormsg;

    private String dialogId;
    private String detailRecords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public int getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(int isIntention) {
        this.isIntention = isIntention;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTtsInfo() {
        return ttsInfo;
    }

    public void setTtsInfo(String ttsInfo) {
        this.ttsInfo = ttsInfo;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getDetailRecords() {
        return detailRecords;
    }

    public void setDetailRecords(String detailRecords) {
        this.detailRecords = detailRecords;
    }
}
