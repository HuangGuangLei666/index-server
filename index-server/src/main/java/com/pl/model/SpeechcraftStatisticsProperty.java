package com.pl.model;

import java.io.Serializable;
import java.util.Date;

public class SpeechcraftStatisticsProperty implements Serializable {
    private Integer id;

    private Long companyId;

    private Long businessId;

    private String craftId;

    private String craftType;

    private String tableName;

    private String propertyName;

    private String propertyKey;

    private String defaultValue;

    private String correctKeyword;

    private String correctValue;

    private String wrongKeyword;

    private String wrongValue;

    private String otherKeyword;

    private String otherValue;

    private Integer sort;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private String remark;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCraftId() {
        return craftId;
    }

    public void setCraftId(String craftId) {
        this.craftId = craftId == null ? null : craftId.trim();
    }

    public String getCraftType() {
        return craftType;
    }

    public void setCraftType(String craftType) {
        this.craftType = craftType == null ? null : craftType.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.trim();
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey == null ? null : propertyKey.trim();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue == null ? null : defaultValue.trim();
    }

    public String getCorrectKeyword() {
        return correctKeyword;
    }

    public void setCorrectKeyword(String correctKeyword) {
        this.correctKeyword = correctKeyword == null ? null : correctKeyword.trim();
    }

    public String getCorrectValue() {
        return correctValue;
    }

    public void setCorrectValue(String correctValue) {
        this.correctValue = correctValue == null ? null : correctValue.trim();
    }

    public String getWrongKeyword() {
        return wrongKeyword;
    }

    public void setWrongKeyword(String wrongKeyword) {
        this.wrongKeyword = wrongKeyword == null ? null : wrongKeyword.trim();
    }

    public String getWrongValue() {
        return wrongValue;
    }

    public void setWrongValue(String wrongValue) {
        this.wrongValue = wrongValue == null ? null : wrongValue.trim();
    }

    public String getOtherKeyword() {
        return otherKeyword;
    }

    public void setOtherKeyword(String otherKeyword) {
        this.otherKeyword = otherKeyword == null ? null : otherKeyword.trim();
    }

    public String getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue == null ? null : otherValue.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", companyId=").append(companyId);
        sb.append(", businessId=").append(businessId);
        sb.append(", craftId=").append(craftId);
        sb.append(", craftType=").append(craftType);
        sb.append(", tableName=").append(tableName);
        sb.append(", propertyName=").append(propertyName);
        sb.append(", propertyKey=").append(propertyKey);
        sb.append(", defaultValue=").append(defaultValue);
        sb.append(", correctKeyword=").append(correctKeyword);
        sb.append(", correctValue=").append(correctValue);
        sb.append(", wrongKeyword=").append(wrongKeyword);
        sb.append(", wrongValue=").append(wrongValue);
        sb.append(", otherKeyword=").append(otherKeyword);
        sb.append(", otherValue=").append(otherValue);
        sb.append(", sort=").append(sort);
        sb.append(", createBy=").append(createBy);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}