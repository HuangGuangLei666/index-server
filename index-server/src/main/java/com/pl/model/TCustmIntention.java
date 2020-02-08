package com.pl.model;

import java.util.Date;

public class TCustmIntention {
    private Long id;

    private Long companyId;

    private Long taskId;

    private String taskName;

    private String telephone;

    private Long agentId;

    private String outNumber;

    private String custmName;

    private Integer status;

    private Date beginDate;

    private Date endDate;

    private Integer totalSeconds;

    private Integer isIntention;

    private String intentionLevel;

    private String focusLevel;

    private String intentLevel;

    private String foucs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber == null ? null : outNumber.trim();
    }

    public String getCustmName() {
        return custmName;
    }

    public void setCustmName(String custmName) {
        this.custmName = custmName == null ? null : custmName.trim();
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

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
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

    public String getFoucs() {
        return foucs;
    }

    public void setFoucs(String foucs) {
        this.foucs = foucs == null ? null : foucs.trim();
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
}

