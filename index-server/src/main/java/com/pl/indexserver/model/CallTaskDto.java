package com.pl.indexserver.model;

import com.pl.indexserver.config.LogCompareName;

public class CallTaskDto {

    public long callTaskId;

    public String callTaskName;
    //业务标识
    public long businessId;

    //外呼开始时间 yyyy-mm-dd
    @LogCompareName(name="开始时间")
    public String beginDate;

    //外呼结束时间  yyyy-mm-dd
    @LogCompareName(name="结束时间")
    public String endDate;

    //外呼时间段 格式: 09-11|12-18|21-23
    @LogCompareName(name="时间段",isKeywordCompare=true,separator="|")
    public String timeQuantum;

    //坐席类型: 1:随机 2：自选
    public int agentType;

    //自选坐席ID列表  用'|'分割开
    public String selfAgentIds;

    //自选坐席类型 1:固话，2:手机
    public int autoAgentType;

    //自选坐席数量
    public int autoAgentNum;

    //外呼任务附加信息
    public CallTaskAdditionalInfo additionalInfo;

    public long getCallTaskId() {
        return callTaskId;
    }

    public void setCallTaskId(long callTaskId) {
        this.callTaskId = callTaskId;
    }

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
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

    public String getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(String timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public String getSelfAgentIds() {
        return selfAgentIds;
    }

    public void setSelfAgentIds(String selfAgentIds) {
        this.selfAgentIds = selfAgentIds;
    }

    public int getAutoAgentType() {
        return autoAgentType;
    }

    public void setAutoAgentType(int autoAgentType) {
        this.autoAgentType = autoAgentType;
    }

    public int getAutoAgentNum() {
        return autoAgentNum;
    }

    public void setAutoAgentNum(int autoAgentNum) {
        this.autoAgentNum = autoAgentNum;
    }

    public String getCallTaskName() {
        return callTaskName;
    }

    public void setCallTaskName(String callTaskName) {
        this.callTaskName = callTaskName;
    }

    public CallTaskAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(CallTaskAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}