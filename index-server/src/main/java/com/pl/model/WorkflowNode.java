package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.io.Serializable;

public class WorkflowNode implements Serializable {
    private Long id;

    @LogCompareName(name = "节点名称")
    private String name;

    private Long workflowId;

    private Long companyId;

    private Long businessId;

    private String paramter;

    private Integer isIntention;

    private String answer;

    private Integer enableInterupt;

    private Integer flag;

    private Long jump;

    private String craftId;

    private Integer score;

    public WorkflowNode() {
    }

    public WorkflowNode(Long id, String name, Long workflowId, Long companyId, Long businessId, String paramter, String answer, Long jump, String craftId, Integer score) {
        this.id =id;
        this.name = name;
        this.workflowId = workflowId;
        this.companyId = companyId;
        this.businessId = businessId;
        this.paramter = paramter;
        this.answer = answer;
        this.jump = jump;
        this.craftId = craftId;
        this.score =score;
    }

    public WorkflowNode(String name, Long workflowId, Long companyId, Long businessId, String paramter, Integer enableInterupt, Integer flag, Long jump, String craftId, Integer score) {
        this.name = name;
        this.workflowId = workflowId;
        this.companyId = companyId;
        this.businessId = businessId;
        this.paramter = paramter;
        this.enableInterupt = enableInterupt;
        this.flag = flag;
        this.jump = jump;
        this.craftId = craftId;
        this.score = score;
    }

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

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
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

    public String getParamter() {
        return paramter;
    }

    public void setParamter(String paramter) {
        this.paramter = paramter == null ? null : paramter.trim();
    }

    public Integer getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(Integer isIntention) {
        this.isIntention = isIntention;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer == null ? null : answer.trim();
    }

    public Integer getEnableInterupt() {
        return enableInterupt;
    }

    public void setEnableInterupt(Integer enableInterupt) {
        this.enableInterupt = enableInterupt;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Long getJump() {
        return jump;
    }

    public void setJump(Long jump) {
        this.jump = jump;
    }

    public String getCraftId() {
        return craftId;
    }

    public void setCraftId(String craftId) {
        this.craftId = craftId == null ? null : craftId.trim();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}