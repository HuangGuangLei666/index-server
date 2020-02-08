package com.pl.model;

import java.io.Serializable;
import java.util.Date;

public class TBusinessConfig implements Serializable {
    private Integer id;

    private String configType;

    private Long companyId;

    private Long businessId;

    private String intentA;

    private String intentB;

    private String intentC;

    private String intentD;

    private String status;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private String remark;

    private static final long serialVersionUID = 1L;

    public enum ConfigType {
        FOCUS("FOCUS", "关注点"),
        SCORE("SCORE", "得分"),
        INTENT("INTENT", "意向"),
        PUSH("PUSH", "推送");

        private String code;
        private String name;

        ConfigType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public enum Status {
        USED("USED", "启用"),
        UNUSED("UNUSED", "停用");

        private String code;
        private String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
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

    public String getIntentA() {
        return intentA;
    }

    public void setIntentA(String intentA) {
        this.intentA = intentA;
    }

    public String getIntentB() {
        return intentB;
    }

    public void setIntentB(String intentB) {
        this.intentB = intentB;
    }

    public String getIntentC() {
        return intentC;
    }

    public void setIntentC(String intentC) {
        this.intentC = intentC;
    }

    public String getIntentD() {
        return intentD;
    }

    public void setIntentD(String intentD) {
        this.intentD = intentD;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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
        this.updateBy = updateBy;
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
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "TBusinessConfig{" +
                "id=" + id +
                ", configType='" + configType + '\'' +
                ", companyId=" + companyId +
                ", businessId=" + businessId +
                ", intentA='" + intentA + '\'' +
                ", intentB='" + intentB + '\'' +
                ", intentC='" + intentC + '\'' +
                ", intentD='" + intentD + '\'' +
                ", status='" + status + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", updateBy='" + updateBy + '\'' +
                ", updateDate=" + updateDate +
                ", remark='" + remark + '\'' +
                '}';
    }
}