package com.pl.model;

import java.util.Date;

/**
 * 业务表
 */
public class TBusiness {
    private Long id;

    private Long companyId;

    private String name;

    private String templateName;

    private String templateType;

    private Date createDate;

    private Date modifyDate;

    private Integer enableInterupt;

    private String controllAddr;

    private String algorithmAddr;

    private String remark;

    private Integer status;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName == null ? null : templateName.trim();
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

    public Integer getEnableInterupt() {
        return enableInterupt;
    }

    public void setEnableInterupt(Integer enableInterupt) {
        this.enableInterupt = enableInterupt;
    }

    public String getControllAddr() {
        return controllAddr;
    }

    public void setControllAddr(String controllAddr) {
        this.controllAddr = controllAddr == null ? null : controllAddr.trim();
    }

    public String getAlgorithmAddr() {
        return algorithmAddr;
    }

    public void setAlgorithmAddr(String algorithmAddr) {
        this.algorithmAddr = algorithmAddr == null ? null : algorithmAddr.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}