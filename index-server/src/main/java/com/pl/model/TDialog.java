package com.pl.model;

import java.util.Date;
import java.util.Objects;

public class TDialog {
    private Long id;

    private Long companyId;

    private Long taskId;

    private String telephone;

    private Integer status;

    private Date beginDate;

    private Date endDate;

    private String file_path;

    private Long agentId;

    private Integer isIntention;

    private String intentionLevel;

    private String focusLevel;

    private String intentLevel;

    private String ttsInfo;

    private Date createDate;

    private Integer priority;

    private Integer total_seconds;

    private String tablePostfix;

    private String taskName;

    private Long fileSize;

    private String errormsg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Integer getPriority() {
        return priority;
    }


    public void setPriority(Integer priority) {
        this.priority = priority;
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
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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


    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(Integer isIntention) {
        this.isIntention = isIntention;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel == null ? null : intentionLevel.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getTotal_seconds() {
        return total_seconds;
    }

    public void setTotal_seconds(Integer total_seconds) {
        this.total_seconds = total_seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDialog)) return false;
        TDialog customer = (TDialog) o;
        System.out.println(Objects.equals(telephone, customer.telephone));
        return Objects.equals(telephone, customer.telephone);
    }

    @Override
    public int hashCode() {
        return telephone.hashCode();
    }

    public String getTablePostfix() {
        return tablePostfix;
    }

    public void setTablePostfix(String tablePostfix) {
        this.tablePostfix = tablePostfix;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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