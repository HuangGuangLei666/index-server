package com.pl.model;

/**
 * 呼叫坐席表
 */
public class TCallAgent {
    private Long id;

    private Long companyId;

    private Long channelId;

    private String agentNum;

    private String outNumber;

    private Integer numberType;

    private Integer status;

    private Integer isUsed;

    private Long usedTaskid;

    private TCallChannel tCallChannel;

    private String extraInfo;

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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(String agentNum) {
        this.agentNum = agentNum == null ? null : agentNum.trim();
    }

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber == null ? null : outNumber.trim();
    }

    public Integer getNumberType() {
        return numberType;
    }

    public void setNumberType(Integer numberType) {
        this.numberType = numberType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }

    public Long getUsedTaskid() {
        return usedTaskid;
    }

    public void setUsedTaskid(Long usedTaskid) {
        this.usedTaskid = usedTaskid;
    }

    public TCallChannel gettCallChannel() {
        return tCallChannel;
    }

    public void settCallChannel(TCallChannel tCallChannel) {
        this.tCallChannel = tCallChannel;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}