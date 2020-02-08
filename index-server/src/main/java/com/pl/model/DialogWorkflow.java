package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.util.ArrayList;
import java.util.Date;

public class DialogWorkflow {
    private Long id;

    private Long companyId;

    private Long businessId;

    private Long parentId;
    @LogCompareName(name = "流程名称")
    private String name;

    private Integer stateRule;

    private Integer enableInterupt;

    private Integer triggerMode;

    private String triggerOrder;

    private Integer level;

    private Integer status;

    private String paramter;

    private Integer sort;

    private Date modifyDate;

    private ArrayList<DialogWorkflow> children;

    public DialogWorkflow() {

    }

    public DialogWorkflow(Long companyId, Long businessId, String name, String paramter) {
        this.companyId = companyId;
        this.businessId = businessId;
        this.name = name;
        this.paramter = paramter;
    }

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

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStateRule() {
        return stateRule;
    }

    public void setStateRule(Integer stateRule) {
        this.stateRule = stateRule;
    }

    public Integer getEnableInterupt() {
        return enableInterupt;
    }

    public void setEnableInterupt(Integer enableInterupt) {
        this.enableInterupt = enableInterupt;
    }

    public Integer getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(Integer triggerMode) {
        this.triggerMode = triggerMode;
    }

    public String getTriggerOrder() {
        return triggerOrder;
    }

    public void setTriggerOrder(String triggerOrder) {
        this.triggerOrder = triggerOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getParamter() {
        return paramter;
    }

    public void setParamter(String paramter) {
        this.paramter = paramter == null ? null : paramter.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public ArrayList<DialogWorkflow> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<DialogWorkflow> children) {
        this.children = children;
    }
}