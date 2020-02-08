package com.pl.model;

import java.io.Serializable;
import java.util.Date;

public class AlgorithmResponseMode implements Serializable {
    private Long id;

    private String name;

    private Long companyId;

    private Long businessId;

    private Long originalId;

    private Integer ruleType;

    private String keyWord;

    private Integer keyNum;

    private Integer matchValue;

    private Integer status;

    private Integer flag;

    private String intentFlag;

    private String uid;

    private Date createDate;

    private Date modifyDate;

    private Date synDate;

    private String content;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    public Integer getKeyNum() {
        return keyNum;
    }

    public void setKeyNum(Integer keyNum) {
        this.keyNum = keyNum;
    }

    public Integer getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(Integer matchValue) {
        this.matchValue = matchValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getIntentFlag() {
        return intentFlag;
    }

    public void setIntentFlag(String intentFlag) {
        this.intentFlag = intentFlag == null ? null : intentFlag.trim();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
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

    public Date getSynDate() {
        return synDate;
    }

    public void setSynDate(Date synDate) {
        this.synDate = synDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", companyId=").append(companyId);
        sb.append(", businessId=").append(businessId);
        sb.append(", originalId=").append(originalId);
        sb.append(", ruleType=").append(ruleType);
        sb.append(", keyWord=").append(keyWord);
        sb.append(", keyNum=").append(keyNum);
        sb.append(", matchValue=").append(matchValue);
        sb.append(", status=").append(status);
        sb.append(", flag=").append(flag);
        sb.append(", intentFlag=").append(intentFlag);
        sb.append(", uid=").append(uid);
        sb.append(", createDate=").append(createDate);
        sb.append(", modifyDate=").append(modifyDate);
        sb.append(", synDate=").append(synDate);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}