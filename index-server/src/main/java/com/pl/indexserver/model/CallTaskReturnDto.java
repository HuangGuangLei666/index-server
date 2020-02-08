package com.pl.indexserver.model;

import com.pl.indexserver.query.BaseQuery;

public class CallTaskReturnDto extends BaseQuery {
    private Long id;

    private String taskName;

    private String ctType;

    private Long companyId;

    private Long bussinessId;

    private String businessName;

    private String uid;

    private String taskState;

    private Integer status;

    private String beginDate;

    private String endDate;

    private Integer totalNumber;

    private String timeQuantum;

    private String createDate;

    private String modifyDate;

    private Integer agentType;

    private Integer autoAgentType;

    private Integer autoAgentNum;

    private String selfAgentIds;

    private boolean showExportReport = false;

    //外呼任务附加信息
    public CallTaskAdditionalInfo additionalInfo;


    public CallTaskReturnDto() {
    }

    public CallTaskReturnDto(Long id, String taskName, String ctType, Long companyId, String businessName, String uid, String taskState, Integer status, String beginDate, String endDate, Integer totalNumber, String timeQuantum, String createDate, String modifyDate) {
        this.id = id;
        this.taskName = taskName;
        this.ctType = ctType;
        this.companyId = companyId;
        this.businessName = businessName;
        this.uid = uid;
        this.taskState = taskState;
        this.status = status;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.totalNumber = totalNumber;
        this.timeQuantum = timeQuantum;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

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
        this.ctType = ctType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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
        this.timeQuantum = timeQuantum;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
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

    public Long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(Long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public boolean isShowExportReport() {
        return showExportReport;
    }

    public void setShowExportReport(boolean showExportReport) {
        this.showExportReport = showExportReport;
    }

    public CallTaskAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(CallTaskAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
