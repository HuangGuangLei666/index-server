package com.pl.model.wx;

import java.util.Date;

public class TQctivationcode {
    private Integer id;

    private String code;

    private Integer usedUserId;

    private String codeMealId;

    private Integer agentId;

    private Integer status;

    private String codeType;

    private Date createTime;

    private Date usedTime;

    private Date expireTime;

    private String tradeNo;

    public TQctivationcode(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public TQctivationcode() {
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getUsedUserId() {
        return usedUserId;
    }

    public void setUsedUserId(Integer usedUserId) {
        this.usedUserId = usedUserId;
    }

    public String getCodeMealId() {
        return codeMealId;
    }

    public void setCodeMealId(String codeMealId) {
        this.codeMealId = codeMealId == null ? null : codeMealId.trim();
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType == null ? null : codeType.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}