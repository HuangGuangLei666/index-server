package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.util.Date;

public class CallTask {
    private Long id;

    @LogCompareName(name="任务名称")
    private String taskName;

    private String ctType;

    private Long companyId;

    private Long businessId;

    private String uid;

    @LogCompareName(name="任务状态")
    private String taskState;

    private Integer status;

    @LogCompareName(name="开始时间")
    private Date beginDate;

    @LogCompareName(name="结束时间")
    private Date endDate;

    @LogCompareName(name="拨打数量")
    private Integer totalNumber;

    @LogCompareName(name="时间段")
    private String timeQuantum;

    private Date createDate;

    private Date modifyDate;

    private Integer repeat;

    private  Integer isTemp;

    @LogCompareName(name="发布类型")
    private Integer agentType;

    @LogCompareName(name="坐席类型")
    private Integer autoAgentType;

    @LogCompareName(name="坐席数量")
    private Integer autoAgentNum;

    @LogCompareName(name="坐席",isKeywordCompare=true,separator="|")
    private String selfAgentIds;

    private String additionalInfo;

    private String taskCate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCtType() {
        return ctType;
    }

    public void setCtType(String ctType) {
        this.ctType = ctType == null ? null : ctType.trim();
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState == null ? null : taskState.trim();
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

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(String timeQuantum) {
        this.timeQuantum = timeQuantum == null ? null : timeQuantum.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public Integer getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(Integer isTemp) {
        this.isTemp = isTemp;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public Integer getAutoAgentType() {
        return autoAgentType;
    }

    public void setAutoAgentType(Integer autoAgentType) {
        this.autoAgentType = autoAgentType;
    }

    public Integer getAutoAgentNum() {
        return autoAgentNum;
    }

    public void setAutoAgentNum(Integer autoAgentNum) {
        this.autoAgentNum = autoAgentNum;
    }

    public String getSelfAgentIds() {
        return selfAgentIds;
    }

    public void setSelfAgentIds(String selfAgentIds) {
        this.selfAgentIds = selfAgentIds;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getTaskCate() {
        return taskCate;
    }

    public void setTaskCate(String taskCate) {
        this.taskCate = taskCate;
    }
}